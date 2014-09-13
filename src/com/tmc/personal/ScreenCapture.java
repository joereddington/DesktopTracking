/**
 * Code modified from code given in
 * http://whileonefork.blogspot.co.uk/2011/02/java-multi-monitor-screenshots.html following a SE
 * question at
 * http://stackoverflow.com/questions/10042086/screen-capture-in-java-not-capturing-whole-screen and
 * then modified by a code review at
 * http://codereview.stackexchange.com/questions/10783/java-screengrab
 */
package com.tmc.personal;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

// TODO: improve screencapture tool by having it not take a photo when the mouse hasn't moved...

class ScreenCapture implements Runnable {

  private int minsBetweenScreenshots = 5;
  private String workingDirectory = "";

  public ScreenCapture(String string, int i) {
    workingDirectory = string;
    minsBetweenScreenshots = i;
  }


  private void tryWritingScreenshotToFile(int indexOfPicture) {
    try {
      Image scaledImg = takeScreenshot();
      storeImageToFile(workingDirectory + "ScreenCapture" + indexOfPicture, scaledImg);
    } catch (IOException e1) {
      // skip, move to the next one
    } catch (AWTException e) {
      // photo didn't work skip and move on.
    }
  }


  private Image takeScreenshot() throws AWTException {
    Rectangle allScreenBounds = getAllScreenBounds();
    Robot robot = new Robot();
    BufferedImage img = robot.createScreenCapture(allScreenBounds);
    return scaleImage(img);
  }

  private void storeImageToFile(String filename, Image scaledImg) throws IOException {
    ImageIO.write(toBufferedImage(scaledImg), "jpg",
        new File(filename + ActivityLogger.getDateTime() + ".jpg"));
  }

  // code from
  // http://www.java-forums.org/new-java/2790-how-save-image-jpg.html
  private BufferedImage toBufferedImage(Image src) {
    int w = src.getWidth(null);
    int h = src.getHeight(null);
    int type = BufferedImage.TYPE_INT_RGB; // other options
    BufferedImage dest = new BufferedImage(w, h, type);
    Graphics2D g2 = dest.createGraphics();
    g2.drawImage(src, 0, 0, null);
    g2.dispose();
    return dest;
  }

  private Image scaleImage(BufferedImage img) {
    int scaledWidth = (int) (img.getWidth() * 0.5);
    int scaledHeight = (int) (img.getHeight() * 0.5);
    Image scaledImg =
        img.getScaledInstance(scaledWidth, scaledHeight, BufferedImage.SCALE_AREA_AVERAGING);
    return scaledImg;
  }

  /**
   * Okay so all we have to do here is find the screen with the lowest x, the screen with the lowest
   * y, the screen with the higtest value of X+ width and the screen with the highest value of
   * Y+height
   * 
   * @return A rectangle that covers the all screens that might be nearby...
   */
  private Rectangle getAllScreenBounds() {
    Rectangle allScreenBounds = new Rectangle();
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] screens = ge.getScreenDevices();

    int farx = 0;
    int fary = 0;
    for (GraphicsDevice screen : screens) {
      Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
      // finding the one corner
      if (allScreenBounds.x > screenBounds.x) {
        allScreenBounds.x = screenBounds.x;
      }
      if (allScreenBounds.y > screenBounds.y) {
        allScreenBounds.y = screenBounds.y;
      }
      // finding the other corner
      if (farx < (screenBounds.x + screenBounds.width)) {
        farx = screenBounds.x + screenBounds.width;
      }
      if (fary < (screenBounds.y + screenBounds.height)) {
        fary = screenBounds.y + screenBounds.height;
      }
      allScreenBounds.width = farx - allScreenBounds.x;
      allScreenBounds.height = fary - allScreenBounds.y;
    }
    return allScreenBounds;
  }

  @Override
  public void run() {
    int indexOfPicture = 1000;
    while (true) {
      tryWritingScreenshotToFile(indexOfPicture++);
      ActivityLogger.waitForNextSampleTime(minsBetweenScreenshots);
    }
  }

}

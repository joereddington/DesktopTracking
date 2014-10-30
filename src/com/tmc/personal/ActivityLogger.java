package com.tmc.personal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ActivityLogger {

  public static void main(String[] args) {

    String workingDirectory = "../output/";
    CurrentWindowLogger nameGrabber = new CurrentWindowLogger(workingDirectory, 1);
    ScreenCapture picGrabber = new ScreenCapture(workingDirectory, 5);
    CurrentWiFiLogger wifiGrabber = new CurrentWiFiLogger(workingDirectory, 10);
    Thread tn = new Thread(nameGrabber);
    tn.start();
    Thread tp = new Thread(picGrabber);
    tp.start();
    Thread tw = new Thread(wifiGrabber);
    tw.start();
  }

  public static void waitForNextSampleTime(int minsToWait) {
    try {
      TimeUnit.MINUTES.sleep(minsToWait);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // from http://www.coderanch.com/t/409980/java/java/append-file-timestamp
  public final static String getDateTime() {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    return df.format(new Date());
  }

}

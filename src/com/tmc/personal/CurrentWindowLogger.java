package com.tmc.personal;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CurrentWindowLogger implements Runnable {

  private int minsBetweenScreenshots = 1;
  private String workingDirectory = "";

  public CurrentWindowLogger(String string, int i) {
    workingDirectory = string;
    minsBetweenScreenshots = i;
  }

  protected void log(String response, String filename) {
    try {
      toFile(workingDirectory + filename, ActivityLogger.getDateTime() + response);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  protected void toFile(String filename, String text) throws IOException {
    FileWriter fstream = new FileWriter(filename + ".txt", true);
    BufferedWriter out = new BufferedWriter(fstream);
    out.write(text);
    out.close();
  }

  protected String call(String command) {
    Runtime r = Runtime.getRuntime();
    String returnValue = "";
    try {
      Thread.sleep(1000);
      returnValue = takeCommand(r, command);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return returnValue;
  }

  private String takeCommand(Runtime r, String command) {
    String returnValue = "";
    String[] splitCommand = command.split(" ");
    try {// should slow this down a little
      Process p = r.exec(splitCommand);
      returnValue = getStandardOutputOfProcess(p);
      checkProcessForFailure(p);
      p.waitFor();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return returnValue;
  }

  private void checkProcessForFailure(Process p) {
    try {// Check for failure
      if (p.waitFor() != 0) {
        System.out
            .println("There was a problem calling the command line tool, we have recovered and will try again on next cycle");
      }
    } catch (InterruptedException e) {
      System.err.println(e);
    }
  }

  private String getStandardOutputOfProcess(Process p) throws IOException {
    String returnValue;
    InputStream in = p.getInputStream();
    BufferedInputStream buf = new BufferedInputStream(in);
    InputStreamReader inread = new InputStreamReader(buf);
    BufferedReader bufferedreader = new BufferedReader(inread);
    // Read the ls output
    String line;
    returnValue = "";
    while ((line = bufferedreader.readLine()) != null) {
      returnValue = returnValue + line + "\n";
    }
    bufferedreader.close();
    inread.close();
    buf.close();
    in.close();
    return returnValue;
  }

  @Override
  public void run() {
    while (true) {
      log(call("osascript " + workingDirectory + "../activewindow"),"results");
      ActivityLogger.waitForNextSampleTime(minsBetweenScreenshots);
    }
  }

}

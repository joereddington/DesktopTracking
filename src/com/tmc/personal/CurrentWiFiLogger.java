package com.tmc.personal;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CurrentWiFiLogger extends CurrentWindowLogger implements Runnable {

  private int minsBetweenScreenshots = 1;
  private String workingDirectory = "";

  public CurrentWiFiLogger(String string, int i) {
    super(string, i);
  }

 



  @Override
  public void run() {
    while (true) {
      System.out.println("Here");
      log(call("wifiname").replace("\n", ","), "wifi");
      ActivityLogger.waitForNextSampleTime(minsBetweenScreenshots);
    }
  }

}

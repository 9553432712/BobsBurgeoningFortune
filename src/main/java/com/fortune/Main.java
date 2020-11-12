package com.fortune;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Main {

  public static String CURRENCY_TYPE = "USD";
  public static String FILE_NAME = "/bobs_crypto.txt";
  public static ScriptEngine engine;


  public static void main(String a[]) {
    System.out.println("******************************The dashboard is******************************");
    Map<String, Integer> data = read(FILE_NAME);
    String externalServiceOutput;
    List<String> finalDashBoard = new ArrayList<>();
    for (Map.Entry<String, Integer> d : data.entrySet()) {
      externalServiceOutput = callService(d.getKey(), CURRENCY_TYPE);
      Double price = read(externalServiceOutput, CURRENCY_TYPE);
      finalDashBoard.add(String
          .format("Current value of %s of %s quantity value is %s", d.getKey(), d.getValue(), price * d.getValue()));
    }
    for (String s : finalDashBoard) {
      System.out.println(s);
    }
  }

  public static Map<String, Integer> read(String fileName) {
    Map<String, Integer> data = null;
    try {
      InputStream is = Main.class.getResourceAsStream(fileName);
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      String line;
      String s[];
      data = new HashMap<String, Integer>();
      while ((line = reader.readLine()) != null) {
//        System.out.println(line);
        if (line != null && line.length() > 0) {
          if (line.contains("=")) {
            s = line.split("=");
            if (s.length == 2) {
              if (data.containsKey(s[0])) {
                data.put(s[0], data.get(s[0]) + Integer.parseInt(s[1]));
              } else {
                data.put(s[0], Integer.parseInt(s[1]));
              }
            }
          }
        }
      }

    } catch (FileNotFoundException fileNotFoundException) {
      System.out.println("File not found");
      fileNotFoundException.printStackTrace();
    } catch (NullPointerException nullPointer) {
      System.out.println("File not found");
      nullPointer.printStackTrace();
    } catch (IOException ioException) {
      System.out.println("Exception occured while process the file");
      ioException.printStackTrace();
    } catch (NumberFormatException numberFormatException) {
      System.out.println("Data in the file inconsistent");
      numberFormatException.printStackTrace();
    }
//    System.out.println(data);
    return data;
  }

  public static Double read(String inputJsonString, String currencyType) {
    Double resultData = 0D;
    try {

      ScriptEngineManager sem = new ScriptEngineManager();
      engine = sem.getEngineByName("javascript");


      String script = "Java.asJSONCompatible(" + inputJsonString + ")";
      Object result = engine.eval(script);
      Map<String, Double> contents = (Map) result;
      resultData = contents.get(currencyType);
      contents.forEach((t, u) -> {
//        System.out.println(t.toString());
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resultData;
  }

  public static String callService(String cryptoType, String currencyType) {
    String result = null;
    try {
      String u =
          String.format("https://min-api.cryptocompare.com/data/price?fsym=%s&tsyms=%s", cryptoType, currencyType);

      URL url = new URL(u);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Accept", "application/json");
      if (conn.getResponseCode() != 200) {
        throw new RuntimeException("Failed : HTTP Error code : "
            + conn.getResponseCode());
      }
      InputStreamReader in = new InputStreamReader(conn.getInputStream());
      BufferedReader br = new BufferedReader(in);
      result = br.readLine();

      conn.disconnect();
      return result;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }
}

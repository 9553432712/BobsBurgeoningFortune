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
    System.out.println("-----------------HELLO BOB-------------------------");
    Map<String, Integer> data = readFile(FILE_NAME);
    String externalServiceOutput;
    List<String> finalDashBoard = new ArrayList<>();
    for (Map.Entry<String, Integer> d : data.entrySet()) {
      externalServiceOutput = callService(d.getKey(), CURRENCY_TYPE);
      Double price = convertStringToJson(externalServiceOutput, CURRENCY_TYPE);
      finalDashBoard.add(String
          .format("%s of %s quantity is %s", d.getKey(), d.getValue(), price * d.getValue()));
      System.out.println(finalDashBoard);
    }
    System.out.println(finalDashBoard);
  }

  public static Map<String, Integer> readFile(String fileName) {
    Map<String, Integer> data = null;
    try {
      InputStream is = Main.class.getResourceAsStream(fileName);
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      String line;
      String s[];
      data = new HashMap<String, Integer>();
      while ((line = reader.readLine()) != null) {
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

    } catch (NullPointerException nullPointer) {
      System.out.println("File not found");

    } catch (IOException ioException) {
      System.out.println("Exception occured while process the file");

    } catch (Exception e) {
      System.out.println("Data in the file inconsistent");

    }
    return data;
  }

  public static Double convertStringToJson(String inputJsonString, String currencyType) {
    Double resultData = 0D;
    try {

      ScriptEngineManager sem = new ScriptEngineManager();
      engine = sem.getEngineByName("javascript");


      String script = "Java.asJSONCompatible(" + inputJsonString + ")";
      Object result = engine.eval(script);
      Map<String, Double> contents = (Map) result;
      resultData = contents.get(currencyType);

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

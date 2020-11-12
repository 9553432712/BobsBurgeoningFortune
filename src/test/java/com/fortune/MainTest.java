package com.fortune;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import java.util.Map;

class MainTest {

  @Test
  public void testCallService() throws Exception {
    String result = Main.callService("BTC", "EUR");
    assertNotNull("current value of BTC", result);
  }

  @Test
  public void testParseJson() throws Exception {
    Double result = Main.convertStringToJson("{'EUR': 123.0}", "EUR");
    assertEquals(Double.valueOf(123.0), result);
  }

  @Test
  public void testRead() throws Exception {
    Map<String, Integer> result = Main.readFile("/bobs_crypto.txt");
    assertNotNull(result);
    assertEquals(3, result.size());
  }
}

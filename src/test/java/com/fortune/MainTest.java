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
    Double result = Main.read("{'EUR': 123.0}", "EUR");
    assertEquals(Double.valueOf(123.0), result);
  }

  @Test
  public void testRead_withInvalidFile_shouldReturnNull() throws Exception {
    Map<String, Integer> result = Main.read("fileName");
    assertNull(result);
  }

  @Test
  public void testRead_withValidFile_shouldReturnMap() throws Exception {
    Map<String, Integer> result = Main.read("/bobs_crypto.txt");
    assertNotNull(result);
    assertEquals(3, result.size());
  }
}

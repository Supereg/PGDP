package utils;

import org.junit.Test;
import utils.CRC;

import static org.junit.Assert.assertEquals;

public class CRCTestSol {

  // 1P
  @Test
  public void testDegree() {
    assertEquals(0, new CRC(0b1).getDegree());
    assertEquals(1, new CRC(0b10).getDegree());
    assertEquals(8, new CRC(0b101011101).getDegree());
    assertEquals(10, new CRC(0b11101011101).getDegree());
    assertEquals(31, new CRC(0b10000001001101010101010101111100).getDegree());
  }
  
  // 1P
  @Test
  public void testSimple() {
    assertEquals(6, new CRC(0b100110).crcASCIIString("x"));
    assertEquals(26, new CRC(0b100110).crcASCIIString("xyz"));
    assertEquals(24, new CRC(0b100110).crcASCIIString("uUUu"));
    assertEquals(697572, new CRC(0b1001101010101010101111100).crcASCIIString("a"));
    assertEquals(1921812, new CRC(0b1001101010101010101111100).crcASCIIString("aXc"));
    assertEquals(13855476, new CRC(0b1001101010101010101111100).crcASCIIString("foo"));
  }

  // 1P
  @Test
  public void testMoreThanInt() {
    assertEquals(18, new CRC(0b100110).crcASCIIString("PinguineSindSoKnuffig"));
    assertEquals(20, new CRC(0b100110).crcASCIIString("EsSchneitHeute"));
    assertEquals(2707304, new CRC(0b1001101010101010101111100).crcASCIIString("Lalalalalalalala"));
    assertEquals(14353824, new CRC(0b1001101010101010101111100).crcASCIIString("IchBinSoKreativImTestSchreiben"));
  }
  
  // 1P
  @Test
  public void testNegPol() {
    assertEquals(2042480604, new CRC(0b10000001001101010101010101111100).crcASCIIString("KatzenSindAllerdingsAuchNett"));
    assertEquals(665772440, new CRC(0b10000001001101010101010101111100).crcASCIIString("Hmhmhmhm"));
  }
  
  // 1P
  @Test public void testLong() {
    String s = "DiesWirdEinSehrLangerStringHaha";
    StringBuilder longBuilder = new StringBuilder();
    for(int i = 0; i < 50000; i++)
      longBuilder.append(s);
    assertEquals(195, new CRC(0b100110111).crcASCIIString(longBuilder.toString()));
    assertEquals(132, new CRC(0b10100110111).crcASCIIString(longBuilder.toString()));
    assertEquals(2566, new CRC(0b1010011011110).crcASCIIString(longBuilder.toString()));
  }

  @Test
  public void testSheetExample() {
    assertEquals(26, new CRC(0b100110).crcASCIIString("az"));
  }

  @Test
  public void testPiazza() {
    assertEquals(6, new CRC(0b100110).crcASCIIString("ab"));
    assertEquals(12, new CRC(0b100110).crcASCIIString("abc"));
    assertEquals(30, new CRC(0b100110).crcASCIIString("abd"));

    assertEquals(26, new CRC(0b100110).crcASCIIString("pinguin"));
    assertEquals(24, new CRC(0b100110).crcASCIIString("Plagiat"));
    assertEquals(26, new CRC(0b100110).crcASCIIString("exmatrikulation"));
    assertEquals(12, new CRC(0b100110).crcASCIIString("SenseiZeigMirDenWeg"));
    assertEquals(30, new CRC(0b100110).crcASCIIString("testTest"));
    assertEquals(12, new CRC(0b100110).crcASCIIString("abUndZu"));
    assertEquals(18, new CRC(0b100110).crcASCIIString("striiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiing"));
    assertEquals(10, new CRC(0b100110).crcASCIIString("PGdP"));

    assertEquals(15149548, new CRC(0b1001101010101010101111100).crcASCIIString("ab"));
    assertEquals(10378820, new CRC(0b1001101010101010101111100).crcASCIIString("abc"));
    assertEquals(1373232, new CRC(0b1001101010101010101111100).crcASCIIString("abd"));
  }

}
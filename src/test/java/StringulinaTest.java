import org.junit.Test;

import static org.junit.Assert.*;

public class StringulinaTest {
  /*
   * Tests für Teilaufgabe 1
   */

  // 0.5P
  @Test
  public void testSubstringPosStandard() {
    assertEquals(0, Stringulina.substringPos("halloduda", "halloduda"));
    assertEquals(2, Stringulina.substringPos("halloduda", "llo"));
    assertEquals(2, Stringulina.substringPos("hallodudahalloduda", "llo"));
  }

  // 0.2P
  @Test
  public void testSubstringPosNotFound() {
    assertEquals(-1, Stringulina.substringPos("halloduda", "alli"));
  }

  // 0.2P
  @Test
  public void testSubstringPosNotFoundEmpty() {
    assertEquals(-1, Stringulina.substringPos("", "alli"));
  }

  // 0.1P
  @Test
  public void testSubstringPosLonger() {
    assertEquals(-1, Stringulina.substringPos("halloduda", "hallodudax"));
  }

  /*
   * Tests für Teilaufgabe 2
   */

  // 0.2P
  @Test
  public void testCountSubstringZero() {
    assertEquals(0, Stringulina.countSubstring("halloduhalloda", "llox"));
  }

  // 0.2P
  @Test
  public void testCountSubstringOne() {
    assertEquals(1, Stringulina.countSubstring("halloduhalloda", "du"));
  }

  // 0.2P
  @Test
  public void testCountSubstringTwo() {
    assertEquals(2, Stringulina.countSubstring("halloduhalloda", "hall"));
  }

  // 0.2P
  @Test
  public void testCountSubstringEmpty() {
    assertEquals(0, Stringulina.countSubstring("", "hall"));
  }
  
  // 0.2P
  @Test
  public void testCountSubstringSame() {
    assertEquals(3, Stringulina.countSubstring("bxxxxxz", "xxx"));
  }

  /*
   * Tests für Teilaufgabe 3
   */

  // 0.5P
  @Test
  public void testcorrectlyBracketedSimple() {
    assertTrue(Stringulina.correctlyBracketed("hall()"));
    assertTrue(Stringulina.correctlyBracketed("hall(x)"));
    assertFalse(Stringulina.correctlyBracketed("hall(x"));
    assertFalse(Stringulina.correctlyBracketed("hallx)"));
    assertFalse(Stringulina.correctlyBracketed("(hallx"));
  }

  // 0.5P
  @Test
  public void testcorrectlyBracketedMultipleTypes() {
    assertTrue(Stringulina.correctlyBracketed("{hal]l()"));
    assertTrue(Stringulina.correctlyBracketed("ha[]ll(x)"));
    assertFalse(Stringulina.correctlyBracketed("{hall(x}"));
    assertFalse(Stringulina.correctlyBracketed("ha{l]lx)"));
    assertFalse(Stringulina.correctlyBracketed("(h[[[allx"));
  }

  // 0.5P
  @Test
  public void testcorrectlyBracketedComplex() {
    assertTrue(Stringulina.correctlyBracketed("{(hal]l((())()))["));
    assertTrue(Stringulina.correctlyBracketed("{()()(hal]l((()abc)(x)()))[()"));
    assertFalse(Stringulina.correctlyBracketed("{()()(hal]l((()))))[()"));
    assertFalse(Stringulina.correctlyBracketed("{()()()hal]l((())()))[()"));
    assertFalse(Stringulina.correctlyBracketed("{()()(hal]l((())()))[("));
    assertFalse(Stringulina.correctlyBracketed("{)()()(hal]l((())()))[()"));
  }

  // 0.5P
  @Test
  public void testcorrectlyBracketedNoBrackets() {
    assertTrue(Stringulina.correctlyBracketed("Pinguin"));
    assertTrue(Stringulina.correctlyBracketed(""));
  }

  /*
   * Tests für Teilaufgabe 4
   */

  // 0.25P
  @Test
  public void testMatchesEqual() {
    assertTrue(Stringulina.matches("Foo", "Foo"));
  }

  // 0.5P
  @Test
  public void testMatchesSimple() {
    assertTrue(Stringulina.matches("Foo", "Fo{2}"));
  }

  // 0.25P für Gruppe 1 {
  @Test
  public void testMatchesDot() {
    assertTrue(Stringulina.matches("Foo", "F.o"));
  }

  @Test
  public void testMatchesDotNoMatch() {
    assertFalse(Stringulina.matches("Foo", "F..o"));
  }
  // } (Ende Gruppe 1)

  // 2P für Gruppe 2, 0.25 Abzug pro Fehlschlag {
  @Test
  public void testMatchesTwo() {
    assertTrue(Stringulina.matches("FoooBaaaaar", "Fo{3}Baa{4}r"));
  }

  @Test
  public void testMatchesMissingEnd() {
    assertFalse(Stringulina.matches("FoooBaaaaar", "Fo{3}Baa{4}"));
  }

  @Test
  public void testMatchesMissingStart() {
    assertFalse(Stringulina.matches("FoooBaaaaar", "o{3}Baa{4}r"));
  }

  @Test
  public void testMatchesMultiOne() {
    assertTrue(Stringulina.matches("FoooBaaaaar", "F{1}o{3}Baa{4}r"));
  }

  @Test
  public void testMatchesMultiDoubleDigitTrue() {
    assertTrue(Stringulina.matches("FoooBaaaaaaaaaaar", "F{1}o{3}Baa{10}r"));
  }

  @Test
  public void testMatchesMultiDoubleDigitFalse() {
    assertFalse(Stringulina.matches("FoooBaaaaaaaaaaaar", "F{1}o{3}Baa{10}r"));
  }

  @Test
  public void testMatchesMultiDoubleDigitDot() {
    assertTrue(Stringulina.matches("FoooBaaaaaaaaaaar", "F{1}.{3}B.a{10}r"));
  }

  @Test
  public void testMatchesMultiDoubleDigitDotFalse() {
    assertFalse(Stringulina.matches("FoooBaaaaaaaaaaar", "F{1}.{2}B.a{10}r"));
  }
  // } (Ende Gruppe 2)

  // 1P für Gruppe 3, alles oder nichts {
  @Test
  public void testMatchesLongTrue() {
    int n = 124356;
    String pattern = "F{" + n + "}.{" + (2 * n) + "}r";
    StringBuilder inputBuilder = new StringBuilder();
    for (int i = 0; i < n; i++)
      inputBuilder.append("F");
    String baz = "PinguineSindSoSuperSuess";
    for (int i = 0; i < 2 * n; i++)
      inputBuilder.append(baz.charAt(i % baz.length()));
    inputBuilder.append('r');
    assertTrue(Stringulina.matches(inputBuilder.toString(), pattern));
  }

  @Test
  public void testMatchesLongFalse1() {
    int n = 124356;
    String pattern = "F{" + n + "}.{" + (2 * n + 1) + "}r";
    StringBuilder inputBuilder = new StringBuilder();
    for (int i = 0; i < n; i++)
      inputBuilder.append("F");
    String baz = "PinguineSindSoSuperSuess";
    for (int i = 0; i < 2 * n; i++)
      inputBuilder.append(baz.charAt(i % baz.length()));
    inputBuilder.append('r');
    assertFalse(Stringulina.matches(inputBuilder.toString(), pattern));
  }

  @Test
  public void testMatchesLongFalse2() {
    int n = 124356;
    String pattern = "F{" + (2 * n) + "}.{" + (2 * n) + "}r";
    StringBuilder inputBuilder = new StringBuilder();
    for (int i = 0; i < n; i++)
      inputBuilder.append("F");
    String baz = "PinguineSindSoSuperSuess";
    for (int i = 0; i < 2 * n; i++)
      inputBuilder.append(baz.charAt(i % baz.length()));
    inputBuilder.append('r');
    assertFalse(Stringulina.matches(inputBuilder.toString(), pattern));
  }
  // } (Ende Gruppe 3)

  // 0.5P Abzug, falls dieser Test nicht durchläuft
  @Test
  public void testMatchesZero() {
    assertTrue(Stringulina.matches("Pinguin", "X{0}y{0}P.{1}nge{0}ui{1}.w{0}"));
    assertFalse(Stringulina.matches("Pingeuin", "X{0}y{0}P.{1}nge{0}ui{1}.w{0}"));
  }
}

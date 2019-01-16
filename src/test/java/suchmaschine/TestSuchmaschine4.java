package suchmaschine;

import org.junit.Before;
import suchmaschine.Author;
import suchmaschine.Date;
import suchmaschine.Document;
import suchmaschine.WordCountsArray;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestSuchmaschine4 {
  private ArrayList<String> reqAttr;

  @Before
  public void init() {
    reqAttr = new ArrayList<String>();
    reqAttr.add("Author");
    reqAttr.add("Date");
    reqAttr.add("Review");
    reqAttr.add("Document");
  }
  
  @org.junit.Test
  public void CheckDateConstructor() throws Exception {
    Author felice = new Author("Felice", "Testmensch", new Date(5, 1, 2010), "Muenchen", "xyz@web.de");
    //Author anders = new Author("Philips", "Mauter", new Date(15, 1, 2010), "Muenchen", "xyz@web.de");
    Date birthday = felice.getBirthday();
    assertEquals(birthday.getDay(), 5);
    assertEquals(birthday.getMonth(), 1);
    assertEquals(birthday.getYear(), 2010);
  }
  
  @org.junit.Test
  public void CheckDocument() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder();
    // Es ist besser, Ehre zu verdienen, sie aber nicht erwiesen zu bekommen, als
    // Ehre erwiesen zu bekommen, sie jedoch nicht zu verdienen.
    sb.append("\n>>> Test HA2.4: tCheckDocument");
    for (String s : mySUFFICES) {
      boolean contains = false;
      for (String s1 : Document.SUFFICES) {
        if (s.toLowerCase().equals(s1.toLowerCase())) {
          contains = true;
          break;
        }
      }
      if (contains == false) {
        System.out.println("Missing: " + s);
        sb.append("\n>>> Document.SUFFICE enthält nicht alle Elemente.");
        right = false;
        break;
      }
    }
    String content = "Harry versuchte übrigens nicht zum ersten Mal die Sache zu erklären";
    Document doc = new Document("Harry Potter", "Deutsch", "Top Seller", new Date(5, 6, 1998),
        new Author("Joanne", "K. Rowling", new Date(31, 7, 1965), "Londen", "joanne@potter.com"), content);
    WordCountsArray wca = doc.getWordCounts();
    if (wca.size() != 11) {
      sb.append("\n>>> Document.getWordCounts liefert falsche anzahl an Wörtern.");
      right = false;
    }

    Method[] methods = Document.class.getDeclaredMethods();
    for (Method m : methods) {
      if (m.getName().toLowerCase().equals("tokenize")) {
        m.setAccessible(true);
        String[] str = (String[]) m.invoke(doc, "Er wechselte finstere Blicke mit seiner Gattin Petunia.");
        if (str.length != 8) {
          sb.append("\n>>> Document.tokenize zerlegt Sätze falsch.");
          right = false;
        }
      } else if (m.getName().toLowerCase().equals("sufficesequal")) {
        m.setAccessible(true);
        boolean ret = (boolean) m.invoke(doc, "gehen", "gehen", 3);
        boolean ret2 = (boolean) m.invoke(doc, "schlafen", "gehen", 3);
        if (!ret || ret2) {
          sb.append("\n>>> Document.sufficesequal berechnet Suffix falsch.");
          right = false;
        }
      } else if (m.getName().toLowerCase().equals("findsuffix")) {
        m.setAccessible(true);
        String str = (String) m.invoke(doc, "fakfaksdj");
        if (!str.toLowerCase().equals("")) {
          sb.append("\n>>> Document.findsuffix findet falschen Suffix.");
          right = false;
        }
        str = (String) m.invoke(doc, "nachhaltig");
        if (!str.toLowerCase().equals("haltig")) {
          sb.append("\n>>> Document.findsuffix findet falschen Suffix.");
          right = false;
        }
      } else if (m.getName().toLowerCase().equals("cutsuffix")) {
        m.setAccessible(true);
        String str = (String) m.invoke(doc, "freundschaft", "schaft");
        if (!str.toLowerCase().equals("freund")) {
          sb.append("\n>>> Document.cutsuffix findet falschen Suffix.");
          right = false;
        }
        str = (String) m.invoke(doc, "freundschaft", "los");
        if (!str.toLowerCase().equals("freundschaft")) {
          sb.append("\n>>> Document.cutsuffix findet falschen Suffix.");
          right = false;
        }
      } else if (m.getName().toLowerCase().equals("addtext")) {
        m.setAccessible(true);
        String str = (String) m.invoke(doc, "hello world");
        wca = doc.getWordCounts();
        if (wca.size() != 2) {
          sb.append("\n>>> Document.addtext nicht nach Spezifikation.");
          right = false;
        }
      }
    }

    assertTrue(sb.toString(), right);
  }

  @org.junit.Test
  public void CheckWordCountsArray() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder();
    // Es ist besser, Ehre zu verdienen, sie aber nicht erwiesen zu bekommen, als
    // Ehre erwiesen zu bekommen, sie jedoch nicht zu verdienen.
    sb.append("\n>>> Zusatzaufgabe!! Test HA2.7: tCheckWordCountsArrayZusatz");
    WordCountsArray wca = new WordCountsArray(3);
    wca.add("hello", 10);
    wca.add("world", 5);
    wca.add("test", 3);
    wca.add("", 1);
    if (wca.size() != 3) {
      sb.append("\n>>> WordCountsArray.size() liefert falschen wert. SOLL 3, IST " + wca.size());
      right = false;
    }
    wca.add("my", 8);
    wca.add("kdkdk", 0);
    if (wca.size() != 5) {
      sb.append("\n>>> WordCountsArray.size() liefert falschen wert. SOLL 5, IST " + wca.size());
      right = false;
    }
    if (wca.getCount(3) != 8) {
      sb.append(
          "\n>>> WordCountsArray.getCount(index) liefert falschen wert. Alternative WordCountsArray.add(word,count) speichert Häufigkeit falsch ab.");
      right = false;
    }

    wca.setCount(4, 1);
    if (wca.getCount(4) != 1) {
      sb.append("\n>>> WordCountsArray.setCount(index,count) setzt Häufigkeit flasch.");
      right = false;
    }

    //prueft, ob illegale Indeces in getCount abgefangen werden
    if (wca.getCount(-1) != -1){ 
      sb.append("\n >>> WordCountsArray.setCount(index) faengt negativen Index nicht ab");
      right = false;
    }
   
    //prueft, ob setCount negative Counts abfängt
    wca.setCount(4, -1);
    if (wca.getCount(4) == -1){
      sb.append("\n>>> WordCountsArray.setCount(index, count) faengt negative counts nicht ab");
      right = false;
    }

    assertTrue(sb.toString(), right);
  }

  private Object setVal(String typeName) {
    Object param = null;
    switch (typeName) {
    case "int":
    case "char":
    case "byte":
    case "short":
    case "long":
      param = 5;
      break;
    case "boolean":
      param = 1;
      break;
    case "float":
      param = 5f;
      break;
    case "double":
      param = 5d;
      break;
    case "java.lang.string":
      param = "XZUEE";
      break;
    }
    return param;
  }

  /**
   * Most common german suffices
   */
  public static final String[] mySUFFICES = { "ab", "al", "ant", "artig", "bar", "chen", "ei", "eln", "en", "end", "ent",
          "er", "fach", "fikation", "fizieren", "faehig", "gemaeß", "gerecht", "haft", "haltig", "heit", "ie", "ieren", "ig", "in",
          "ion", "iren", "isch", "isieren", "isierung", "ismus", "ist", "itaet", "iv", "keit", "kunde", "legen", "lein",
          "lich", "ling", "logie", "los", "mal", "meter", "mut", "nis", "or", "sam", "schaft", "tum", "ung", "voll", "wert",
          "wuerdig" };
}

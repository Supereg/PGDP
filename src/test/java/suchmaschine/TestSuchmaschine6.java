package suchmaschine;

import org.junit.Test;
import suchmaschine.*;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestSuchmaschine6 {

  @Test
  public void CheckEquals() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder();
    sb.append("\n>>> Test CheckEquals");
    // Teste Date.equals
    Date d1 = new Date();
    Date d2 = new Date();
    boolean value = d1.equals(d2);
    if (!value) {
      sb.append("\n>>> equals(Date date) is Falsch implementiert.");
      right = false;
    }
    Date d3 = new Date(1, 1, 2000);
    boolean value2 = d1.equals(d3);
    if (value2) {
      sb.append("\n>>> equals(Date date) is Falsch implementiert.");
      right = false;
    }
    // Teste Author.equals
    Author a1 = new Author("Joanne", "K. Rowling", new Date(31, 7, 1965), "Londen", "joanne@potter.com");
    Author a2 = new Author("adasddasdas", "qweqweqwe", new Date(1, 10, 1980), "dasdasd", "asdsadas");
    Author a3 = new Author("Joanne", "K. Rowling", new Date(31, 7, 1965), "Londen", "joanne@potter.com");
    value2 = a1.equals(a2);
    if (value2) {
      sb.append("\n>>> equals(Author author) is Falsch implementiert.");
      right = false;
    }
    value2 = a1.equals(a3);
    if (!value2) {
      sb.append("\n>>> equals(Author author) is Falsch implementiert.");
      right = false;
    }

    // Teste WordCount.equals

    WordCount wc = null;
    WordCount wc2 = null;
    try {
      wc = new WordCount("Wort");
      wc2 = new WordCount("Wort");
    } catch (NoSuchMethodError e) {
      try {
        wc = new WordCount("Wort", 0);
        wc2 = new WordCount("Wort", 0);
      } catch (NoSuchMethodError e1) {
        right = false;
        sb.append("\n>>> Falsche Signatur bei dem WordCount Konstruktor");
      }
    }
    value = wc.equals(wc2);
    if (!value) {
      sb.append("\n>>> equals(WordCount wc) is Falsch implementiert.");
      right = false;
    }
    wc.incrementCount(2);
    value = wc.equals(wc2);
    if (value) {
      sb.append("\n>>> equals(WordCount wc) is Falsch implementiert.");
      right = false;
    }

    // Teste WordCountsArray.equals

    WordCountsArray wca = new WordCountsArray(3);
    wca.add("hello", 10);
    wca.add("test", 3);
    WordCountsArray wca2 = new WordCountsArray(3);
    wca2.add("hello", 10);
    wca2.add("test", 3);
    value = wca.equals(wca2);
    if (!value) {
      sb.append("\n>>> equals(WordCount wc) is Falsch implementiert.");
      right = false;
    }
    wca.add("tes2", 0);
    value = wca.equals(wca2);
    if (value) {
      sb.append("\n>>> equals(WordCount wc) is Falsch implementiert.");
      right = false;
    }

    // Teste Document.equals
    String text = "harry versuchte uebrigens nicht zum ersten male die sache zu erklaeren";
    String text2 = "harry versuchte uebrigenss nichtt zumm erstenn malee diee sachee zuu erklaerenn";
    Document doc1 = new Document("harry Potter", "Deutsch", "Top Seller", new Date(5, 6, 1998), a1, text);
    Document doc2 = new Document("harry Potter", "Deutsch", "Top Seller", new Date(5, 6, 1998), a1, text);
    Document doc3 = new Document("harry Potter 3", "Englisch", "blae", new Date(5, 6, 2001), a2, text2);

    value = doc1.equals(doc2);
    if (!value) {
      sb.append("\n>>> equals(Document document) is Falsch implementiert.");
      right = false;
    }
    value2 = doc1.equals(doc3);
    if (value2) {
      sb.append("\n>>> equals(Document document) is Falsch implementiert.");
      right = false;
    }

    // Teste Review.equals
    String content = "das ist ein mieses review";
    Review rev1 = new Review(a1, doc1, text, d1, 1, content);
    Review rev2 = new Review(a1, doc1, text, d1, 1, content);
    Review rev3 = new Review(a2, doc2, text2, d2, 3, content);

    value = rev1.equals(rev2);
    if (!value) {
      sb.append("\n>>> equals(Review review) is Falsch implementiert.");
      right = false;
    }
    value = rev1.equals(rev3);
    if (value) {
      sb.append("\n>>> equals(Review review) is Falsch implementiert.");
      right = false;
    }
    assertTrue(sb.toString(), right);
  }

  @Test
  public void WordCountsArray() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder();
    sb.append("\n>>> Test Erweiterung der Klasse WordCountsArray");
    WordCountsArray wca = new WordCountsArray(3);
    wca.add("hello", 10);
    wca.add("test", 3);
    // Teste getWord(index)
    if (!wca.getWord(0).equals("hello")) {
      sb.append(
          "\n>>> WordCountsArray.getWord(index) liefert falschen wert oder add(word,count) ist falsch implementiert");
      right = false;
    }
    if (wca.getWord(-1) != null) {
      sb.append("\n>>> WordCountsArray.getWord(index) liefert falschen wert. SOLL null sein");
      right = false;
    }
    if (wca.getWord(-10) != null) {
      sb.append("\n>>> WordCountsArray.getWord(index) liefert falschen wert. SOLL null sein");
      right = false;
    }
    // Teste getIndexOfWord(word)
    if (wca.getIndexOfWord("test") != 1) {
      sb.append("\n>>> WordCountsArray.getIndexOfWord(word) liefert falschen wert. SOLL 1 sein ist " + wca.getIndexOfWord("test"));
      right = false;
    }
    if (wca.getIndexOfWord("bla") != -1) {
      sb.append("\n>>> WordCountsArray.getIndexOfWord(word) liefert falschen wert. SOLL -1 sein ist " + wca.getIndexOfWord("test"));
      right = false;
    }

    // Teste getCount(index) und add(word,count)
    wca.add("hello", 10);
    wca.add("hello", -1);
    wca.add("", 10);
    wca.add(null, 10);
    if (wca.getCount(0) != 20 || wca.size() != 2) {
      sb.append(
          "\n>>> WordCountsArray.getCount(index) liefert falschen wert. Alternative WordCountsArray.add(word,count) speichert Haeufigkeit falsch ab.");
      right = false;
    }

    // Teste wordsEqual

    WordCountsArray wca2 = new WordCountsArray(3);
    wca2.add("hello", 10);
    wca2.add("test", 3);
    WordCountsArray wca3 = new WordCountsArray(3);
    wca3.add("test", 3);
    wca3.add("hello", 10);

    Method[] methods = WordCountsArray.class.getDeclaredMethods();
    for (Method m : methods) {
      if (m.getName().toLowerCase().equals("wordsequal")) {
        m.setAccessible(true);
        boolean value = (boolean) m.invoke(wca, wca2);
        if (!value) {
          sb.append("\n>>> WordCountsArray.wordsEqual ist falsch implementiert.");
          right = false;
        }
        value = (boolean) m.invoke(wca, wca3);
        if (value) {
          sb.append("\n>>> WordCountsArray.wordsEqual ist falsch implementiert.");
          right = false;
        }
      }
    }

    // Teste scalarProduct

    Method m = WordCountsArray.class.getDeclaredMethod("scalarProduct", WordCountsArray.class);
    m.setAccessible(true);
    double value0 = (double) m.invoke(wca, wca2);
    if (value0 != 209) {
      sb.append("\n>>> WordCountsArray.scalarProduct ist falsch implementiert. SOLL 209 ist " + value0);
      right = false;
    }
    value0 = (double) m.invoke(wca, wca3);
    if (value0 != 0) {
      sb.append("\n>>> WordCountsArray.scalarProduct ist falsch implementiert. SOLL 0 ist " + value0);
      right = false;
    }

    // Teste sort

    WordCountsArray wca5 = new WordCountsArray(5);
    wca5.add("e", 5);
    wca5.add("d", 4);
    wca5.add("c", 3);
    wca5.add("b", 2);
    wca5.add("a", 1);
    wca5.sort();
    // Ein bisschen unelegant, aber wollte vermeiden wca.equals falls das falsch
    // implementiert ist
    if (wca5.getWord(0) != "a" || wca5.getWord(1) != "b" || wca5.getWord(2) != "c" || wca5.getWord(3) != "d"
        || wca5.getWord(4) != "e") {
      sb.append("\n>>> WordCountsArray.sort ist falsch implementiert (reihenfolge von Woerter passt nicht)");
      right = false;
    }
    if (wca5.getCount(0) != 1 || wca5.getCount(1) != 2 || wca5.getCount(2) != 3 || wca5.getCount(3) != 4
        || wca5.getCount(4) != 5) {
      sb.append("\n>>> WordCountsArray.sort ist falsch implementiert (reihenfolge von Haeufigkeiten passt nicht)");
      right = false;
    }

    // Teste similarity

    double value = wca5.computeSimilarity(wca5);
    if (value != 1) {
      sb.append("\n>>> WordCountsArray.computeSimilarity ist falsch implementiert SOLL 1 sein ist " + value);
      right = false;
    }
    WordCountsArray wca6 = new WordCountsArray(5);
    wca6.add("e", 1);
    wca6.add("d", 0);
    wca6.add("c", 0);
    wca6.add("b", 0);
    wca6.add("a", 0);
    wca6.sort();
    value = wca5.computeSimilarity(wca6);
    if (value != 0.674199862463242) {
      sb.append("\n>>> WordCountsArray.computeSimilarity ist falsch implementiert SOLL 0.674199862463242 sein ist " + value);
      right = false;
    }
    value = wca5.computeSimilarity(wca);
    if (value != 0) {
      sb.append("\n>>> WordCountsArray.computeSimilarity ist falsch implementiert SOLL 0 sein ist " + value);
      right = false;
    }

    assertTrue(sb.toString(), right);
  }
  
  /**
   * Dieser Test kann zum Testen von BucketSort verwendet werden!
   */
  @Test
  public void testWordCountsArraySort() {
    WordCountsArray wca = new WordCountsArray(5);
    wca.add("esdfsd", 6);
    wca.add("eadfsd", 6);
    wca.add("ddfsd", 5);
    wca.add("cdfsf", 5);
    wca.add("cdfef", 5);
    wca.add("bfsfd", 5);
    wca.add("eddfsd", 6);
    wca.add("afsadf", 6);
    wca.add("cdaef", 5);
    wca.add("ab", 2);
    wca.add("aa", 2);
    wca.add("b", 1);
    
    wca.sort();

    for(int i = 0; i < wca.size(); i++) {
      if(i > 0)
        assertTrue(wca.getWord(i - 1).compareTo(wca.getWord(i)) < 0);
      // Häufigkeiten sind entsprechend der Länge gewählt
      assertEquals(wca.getWord(i).length(), wca.getCount(i));
    }
  }

  @Test
  public void DocumentCollection() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder();
    sb.append("\n>>> Test Erweiterung der Klasse WordCountsArray");
    DocumentCollection docCol = new DocumentCollection();
    String text = "harry versuchte uebrigens nicht zum ersten male die sache zu erklaeren";
    String text2 = "harry versuchte uebrigenss nichtt zumm erstenn malee diee sachee zuu erklaerenn";
    Document doc = new Document("harry Potter", "Deutsch", "Top Seller", new Date(5, 6, 1998),
        new Author("Joanne", "K. Rowling", new Date(31, 7, 1965), "Londen", "joanne@potter.com"), text);
    Document doc2 = new Document("harry Potter 2", "Deutsch", "Top Seller", new Date(5, 6, 1998),
        new Author("Joanne", "K. Rowling", new Date(31, 7, 1965), "Londen", "joanne@potter.com"), text2);
    Document doc3 = new Document("harry Potter 3", "Deutsch", "Top Seller", new Date(5, 6, 1998),
        new Author("Joanne", "K. Rowling", new Date(31, 7, 1965), "Londen", "joanne@potter.com"), text);

    // Teste isEmpty()
    if (!docCol.isEmpty()) {
      sb.append("\n>>> DocumentCollection.isEmpty ist falsch implementiert.");
      right = false;
    }

    docCol.prependDocument(doc);
    docCol.prependDocument(doc2);
    docCol.prependDocument(null);

    if (docCol.isEmpty()) {
      sb.append("\n>>> DocumentCollection.isEmpty ist falsch implementiert.");
      right = false;
    }

    // Teste numDocuments()
    if (docCol.numDocuments() != 2) {
      sb.append("\n>>> DocumentCollection.size ist falsch implementiert. SOLL 2 ist " + docCol.numDocuments());
      right = false;
    }

    // Teste getFirst()
    if (!docCol.getFirstDocument().getTitle().equals("harry Potter 2")) {
      sb.append("\n>>> DocumentCollection.getFirst ist falsch implementiert");
      right = false;
    }
    // Teste getLastDocument()
    if (!docCol.getLastDocument().getTitle().equals("harry Potter")) {
      sb.append("\n>>> DocumentCollection.getLast ist falsch implementiert");
      right = false;
    }
    // Teste get(index)
    if (!docCol.get(0).getTitle().equals("harry Potter 2")) {
      sb.append("\n>>> DocumentCollection.get ist falsch implementiert");
      right = false;
    }
    // Teste get(index)
    if (!docCol.get(1).getTitle().equals("harry Potter")) {
      sb.append("\n>>> DocumentCollection.get ist falsch implementiert");
      right = false;
    }
    // Teste get(index)
    if (docCol.get(-1) != null) {
      sb.append("\n>>> DocumentCollection.get ist falsch implementiert (bei illegalem Index)");
      right = false;
    }
    // Teste get(index)
    if (docCol.get(3) != null) {
      sb.append("\n>>> DocumentCollection.get ist falsch implementiert (bei illegalem Index)");
      right = false;
    }
    docCol.removeFirstDocument();
    // Teste removeFirstDocument()
    if (!docCol.getFirstDocument().getTitle().equals("harry Potter")) {
      sb.append("\n>>> DocumentCollection.removeFirst ist falsch implementiert");
      right = false;
    }
    docCol.prependDocument(doc2);
    docCol.removeLastDocument();
    // Teste removeLastDocument()
    if (!docCol.getFirstDocument().getTitle().equals("harry Potter 2")) {
      sb.append("\n>>> DocumentCollection.removeFirst ist falsch implementiert");
      right = false;
    }
    // Teste remove(index)
    boolean value = docCol.remove(0);
    if (value == false || !docCol.isEmpty()) {
      sb.append("\n>>> DocumentCollection.remove ist falsch implementiert");
      right = false;
    }
    docCol.prependDocument(doc);
    docCol.prependDocument(doc2);
    // Teste remove(index)
    value = docCol.remove(10);
    if (value == true || (docCol.numDocuments() != 2)) {
      sb.append("\n>>> DocumentCollection.remove ist falsch implementiert");
      right = false;
    }
    value = docCol.remove(-1);
    if (value == true || (docCol.numDocuments() != 2)) {
      sb.append("\n>>> DocumentCollection.remove ist falsch implementiert");
      right = false;
    }
    // Teste indexOf(doc)
    if (docCol.indexOf(doc) != 1) {
      sb.append("\n>>> DocumentCollection.indexOf ist falsch implementiert SOLL 1 ist" + docCol.indexOf(doc));
      right = false;
    }
    // Teste indexOf(doc)
    if (docCol.indexOf(doc2) != 0) {
      sb.append("\n>>> DocumentCollection.indexOf ist falsch implementiert SOLL 0 ist" + docCol.indexOf(doc2));
      right = false;
    }
    // Teste indexOf(doc)
    if (docCol.indexOf(doc3) != -1) {
      sb.append("\n>>> DocumentCollection.indexOf ist falsch implementiert SOLL -1 ist " + docCol.indexOf(doc3));
      right = false;
    }
    // Teste contains(doc)
    if (docCol.contains(doc3)) {
      sb.append("\n>>> DocumentCollection.contains ist falsch implementiert");
      right = false;
    }
    // Teste contains(doc)
    if (!docCol.contains(doc)) {
      sb.append("\n>>> DocumentCollection.contains ist falsch implementiert");
      right = false;
    }
    Method[] methods = DocumentCollection.class.getDeclaredMethods();
    // Teste allWords()
    for (Method m : methods) {
      if (m.getName().toLowerCase().equals("allwords")) {
        m.setAccessible(true);

        WordCountsArray wca = (WordCountsArray) m.invoke(docCol);
        if (wca.size() != 20) {
          sb.append(
              "\n>>> WordCountsArray.allWords ist falsch implementiert. SOLL 20 Elemente haben, hat " + wca.size());
          right = false;
        }
      }
    }
    // Teste addZeroWordsToDocuments()
    for (Method m : methods) {
      if (m.getName().toLowerCase().equals("addzerowordstodocuments")) {
        m.setAccessible(true);
        m.invoke(docCol);
        if (docCol.getFirstDocument().getWordCounts().size() != 20) {
          sb.append("\n>>> WordCountsArray.addWordsToDocumentsWithCountZero ist falsch implementiert.");
          right = false;
        }
        if (docCol.getLastDocument().getWordCounts().size() != 20) {
          sb.append("\n>>> WordCountsArray.addWordsToDocumentsWithCountZero ist falsch implementiert.");
          right = false;
        }
        int ind = docCol.getLastDocument().getWordCounts().getIndexOfWord("zumm");
        if (docCol.getLastDocument().getWordCounts().getCount(ind) != 0) {
          sb.append("\n>>> WordCountsArray.addWordsToDocumentsWithCountZero ist falsch implementiert.");
          right = false;
        }

        ind = docCol.getFirstDocument().getWordCounts().getIndexOfWord("zum");
        if (docCol.getFirstDocument().getWordCounts().getCount(ind) != 0) {
          sb.append("\n>>> WordCountsArray.addWordsToDocumentsWithCountZero ist falsch implementiert.");
          right = false;
        }
      }
    }
    // Teste match()
    /* Match got reimplemented with paper 07
    String searchQuery = "harry zum";
    docCol.match(searchQuery);

    // Teste getQuerySimilarity(index)
    if (docCol.getQuerySimilarity(0) != 0.42640143271122083) {
      sb.append(
          "\n>>> WordCountsArray.getQuerySimilarity liefert liefert falschen wert. Alternativ match() ist falsch, oder sortBySimilarityDesc() ist falsch. SOLL 0.42640143271122083 ist "
              + docCol.getQuerySimilarity(0));
      right = false;
    }
    if (docCol.getQuerySimilarity(1) != 0.21320071635561041) {
      sb.append(
          "\n>>> WordCountsArray.get®QuerySimilarity liefert liefert falschen wert. Alternativ match() ist falsch, oder sortBySimilarityDesc() ist falsch. SOLL 0.21320071635561041 ist "
              + docCol.getQuerySimilarity(1));
      right = false;
    }
    */

    assertTrue(sb.toString(), right);
  }

}

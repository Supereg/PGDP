package suchmaschine;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertTrue;

public class TestSuchmaschine7 {

  @org.junit.Test
  public void LinkedDocumentAndLinkedDocumentCollection() throws Exception {
    DocumentCollectionTest.FileContent[] fileContents = {
            new DocumentCollectionTest.FileContent("a", "blablabla link:b tralalalal link:c tetsetse ende"),
            new DocumentCollectionTest.FileContent("b", "ich bin b und verlinke niemanden"),
            new DocumentCollectionTest.FileContent("c", "dies ist die datei c die auf datei d verlinkt link:d"),
            new DocumentCollectionTest.FileContent("d", "es ist so ein schoener tag verlink einfach mal auf datei c link:c link:e"),
            new DocumentCollectionTest.FileContent("e", "ich habe gar keine ahnung was ich hier tue und verlinke mal auf datei b link:b link:e link:e")
    };

    DocumentCollectionTest.createFiles(fileContents);

    boolean right = true;
    StringBuilder sb = new StringBuilder();
    sb.append("\n>>> Test LinkedDocumentAndLinkedDocumentCollection");

    Class<?> clazz = Class.forName("suchmaschine.LinkedDocument");
    Method[] methods;
    if (!clazz.getSuperclass().getSimpleName().toLowerCase().equals("document")) {
      sb.append("\n>>> Document ist nicht Super-Klasse von " + clazz.getName() + ".");
      right = false;
    } else {
      methods = clazz.getDeclaredMethods();
      for (Method m : methods) {
        // Test Method findOutgoingIds, 4.1 iii)
        if (m.getName().toLowerCase().equals("findoutgoingids")) {
          if (!Modifier.isPrivate(m.getModifiers())) {
            right = false;
            sb.append("\n>>> " + clazz.getName() + "." + m.getName() + " ist nicht private.");
          } else if (!m.getReturnType().getName().equals("[Ljava.lang.String;")) {
            right = false;
            sb.append("\n>>> Rueckgabetyp von " + clazz.getName() + "." + m.getName() + " nicht korrekt.");
          } else if (m.getParameterTypes().length != 1) {
            right = false;
            sb.append("\n>>> Parametertyp von " + clazz.getName() + "." + m.getName() + " nicht korrekt.");
          } else {
            LinkedDocument ld = new LinkedDocument("Hans", "de", "Kurzes Maerchen", new Date(),
                new Author("Brueder", "Grimm", new Date(), "Wohnort irgendwo", "hans@grimm.comm"),
                "hans hatte sieben link:dateisieben jahre bei seinem herrn gedient", "ISBN:1");
            m.setAccessible(true);
            String[] s = (String[]) m.invoke(ld,
                "es ist einmal eine alte geiss link:dateiziege gewesen die hatte sieben link:dateisieben junge zicklein");
            if (s.length != 2) {
              right = false;
              sb.append("\n>>> " + clazz.getName() + "." + m.getName() + " liefert nicht gefordertes Ergebnis.");
            }
          }
        }
        // Test Method setLinkCountZero, 4.1 iv)
        else if (m.getName().toLowerCase().equals("setlinkcountzero")) {
          if (!Modifier.isPrivate(m.getModifiers())) {
            right = false;
            sb.append("\n>>> " + clazz.getName() + "." + m.getName() + " ist nicht private.");
          } else if (!m.getReturnType().getName().equals("void")) {
            right = false;
            sb.append("\n>>> Rueckgabetyp von " + clazz.getName() + "." + m.getName() + " nicht korrekt.");
          } else if (m.getParameterTypes().length > 0) {
            right = false;
            sb.append("\n>>> Parameter von " + clazz.getName() + "." + m.getName() + " nicht korrekt.");
          } else {
            LinkedDocument ld = new LinkedDocument("Hans", "de", "Kurzes Maerchen", new Date(),
                new Author("Brueder", "Grimm", new Date(), "Wohnort irgendwo", "hans@grimm.comm"),
                "hans hatte sieben link:dateisieben jahre bei seinem herrn gedient", "ISBN:1");
            m.setAccessible(true);
            int index = ld.getWordCounts().getIndexOfWord("link:dateisieb");
            int count = ld.getWordCounts().getCount(index);
            m.invoke(ld);
            int count2 = ld.getWordCounts().getCount(index);
            if (count2 != 0) {
              right = false;
              sb.append("\n>>> " + clazz.getName() + "." + m.getName() + " wird falsch ausgefuehrt.");
            }
          }
        }
      }
    }

    // Test 4.1 v)
    clazz = Class.forName("suchmaschine.LinkedDocumentCollection");
    if (!clazz.getSuperclass().getSimpleName().toLowerCase().equals("documentcollection")) {
      sb.append("\n>>> DocumentCollection ist nicht Super-Klasse von " + clazz.getName() + ".");
      right = false;
    } else {
      LinkedDocument ld1 = new LinkedDocument("Hans", "de", "Kurzes Maerchen", new Date(),
          new Author("Brueder", "Grimm", new Date(), "Wohnort irgendwo", "hans@grimm.comm"),
          "hans hatte sieben link:dateisieben jahre bei seinem herrn gedient", "ISBN:1");
      LinkedDocument ld2 = new LinkedDocument("Hans", "de", "Kurzes Maerchen", new Date(),
          new Author("Brueder", "Grimm", new Date(), "Wohnort irgendwo", "hans@grimm.comm"),
          "es ist einmal eine alte geiss link:dateiziege gewesen die hatte sieben link:dateisieben junge zicklein",
          "ISBN:2");
      LinkedDocumentCollection ldc = new LinkedDocumentCollection();

      ldc.prependDocument(ld1);
      ldc.prependDocument(ld2);
      ldc.appendDocument(ld2);

      LinkedDocument ldt = (LinkedDocument) ldc.getFirstDocument();
      if (!ldt.getID().equals(ld2.getID())) {
        sb.append("\n>>> " + clazz.getName() + ".prependDocument oder " + clazz.getName()
            + ".appendDocument berechnet was falsch");
        right = false;
      }
      ldt = (LinkedDocument) ldc.getLastDocument();
      if (!ldt.getID().equals(ld1.getID())) {
        sb.append("\n>>> " + clazz.getName() + ".prependDocument oder " + clazz.getName()
            + ".appendDocument berechnet was falsch");
        right = false;
      }
    }

    // Test 4.1 vii) viii), ix), x), xi)
    LinkedDocument atxt = LinkedDocument.createLinkedDocumentFromFile("a");
    LinkedDocument btxt = LinkedDocument.createLinkedDocumentFromFile("b");
    LinkedDocument ctxt = LinkedDocument.createLinkedDocumentFromFile("c");
    LinkedDocument dtxt = LinkedDocument.createLinkedDocumentFromFile("d");
    LinkedDocument etxt = LinkedDocument.createLinkedDocumentFromFile("e");

    LinkedDocumentCollection ldc = new LinkedDocumentCollection();
    ldc.appendDocument(atxt);
    int sizeBefore = ldc.numDocuments();
    ldc = ldc.crawl();
    if ((ldc.numDocuments() != 5) || (ldc.numDocuments() < sizeBefore)) {
      sb.append("\n>>> LinkedDocumentCollection.crawl berechnet was falsch. Groeße Returnvalue: Soll 5, IST "
          + ldc.numDocuments());
      right = false;
    }
    ldc.calculateIncomingLinks();

    for (int i = 0; i < ldc.numDocuments(); i++) {
      LinkedDocument ld = (LinkedDocument) ldc.get(i);
      // System.out.println("Datei "+ld.getID()+": Outgoing
      // "+ld.getOutgoingLinks().numDocuments()+", Incoming
      // "+ld.getIncomingLinks().numDocuments());
      switch (ld.getID()) {
      case "a":
        atxt = ld;
        break;
      case "b":
        btxt = ld;
        break;
      case "c":
        ctxt = ld;
        break;
      case "d":
        dtxt = ld;
        break;
      case "e":
        etxt = ld;
        break;
      }
    }

    if ((atxt != null) && (atxt.getOutgoingLinks().numDocuments() != 2)) {
      sb.append("\n>>> LinkedDocument.getOutgoingLinks berechnet falsch. Groeße Returnvalue: Soll 2, IST "
          + atxt.getOutgoingLinks().numDocuments());
      right = false;
    } else if ((btxt != null) && (btxt.getOutgoingLinks().numDocuments() != 0)) {
      sb.append("\n>>> LinkedDocument.getOutgoingLinks berechnet falsch. Groeße Returnvalue: Soll 0, IST "
          + btxt.getOutgoingLinks().numDocuments());
      right = false;
    }
    if ((ctxt != null) && (ctxt.getIncomingLinks().numDocuments() != 2)) {
      sb.append("\n>>> LinkedDocument.getIncomingLinks berechnet falsch. Groeße Returnvalue: Soll 2, IST "
          + ctxt.getIncomingLinks().numDocuments());
      right = false;
    } else if ((btxt != null) && (btxt.getIncomingLinks().numDocuments() != 2)) {
      sb.append("\n>>> LinkedDocument.getIncomingLinks berechnet falsch. Groeße Returnvalue: Soll 1, IST "
          + btxt.getIncomingLinks().numDocuments());
      right = false;
    }

    // ldc.match("blab ");
    // System.out.println(ldc.getQuerySimilarity(0));

    DocumentCollectionTest.deleteFiles(fileContents);

    assertTrue(sb.toString(), right);
  }

  @org.junit.Test
  public void WeightAndSimilarity() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder();
    sb.append("\n>>> Test WeightAndSimilarity");

    DocumentCollection docCol = new DocumentCollection();
    String text = "harry versuchte uebrigens nicht zum ersten male die sache zu erklaeren";
    String text2 = "harry versuchte uebrigens nicht zum ersten male die sache zu harry versuchte uebrigens nicht zum ersten male die sache zu";
    String text3 = "harry versuchte uebrigens nicht zum ersten male die sache zu erklaeren harry versuchte uebrigens nicht zum ersten male die sache zu erklaeren harry versuchte uebrigens nicht zum ersten male die sache zu erklaeren";
    Document doc = new Document("harry Potter", "Deutsch", "Top Seller", new Date(5, 6, 1998),
        new Author("Joanne", "K. Rowling", new Date(31, 7, 1965), "Londen", "joanne@potter.com"), text);
    Document doc2 = new Document("harry Potter 2", "Deutsch", "Top Seller", new Date(5, 6, 1998),
        new Author("Joanne", "K. Rowling", new Date(31, 7, 1965), "Londen", "joanne@potter.com"), text2);
    Document doc3 = new Document("harry Potter 3", "Deutsch", "Top Seller", new Date(5, 6, 1998),
        new Author("Joanne", "K. Rowling", new Date(31, 7, 1965), "Londen", "joanne@potter.com"), text3);

    docCol.prependDocument(doc);
    docCol.prependDocument(doc2);
    docCol.prependDocument(doc3);

    // Mache wca und wca2 vergleichbar
    Method[] methodsDocCol = DocumentCollection.class.getDeclaredMethods();
    for (Method m : methodsDocCol) {
      if (m.getName().toLowerCase().equals("addzerowordstodocuments")) {
        m.setAccessible(true);
        m.invoke(docCol);
      }
    }

    WordCountsArray wca2 = docCol.get(0).getWordCounts(); // doc3
    WordCountsArray wca = docCol.get(1).getWordCounts(); // doc2

    // Teste noOfDocumentsContainingWord()
    if (docCol.noOfDocumentsContainingWord("versuchte") != 3) {
      sb.append("\n>>> DocumentCollection.noOfDocumentsContainingWord ist falsch implementiert. SOLL 3 sein ist "
          + docCol.noOfDocumentsContainingWord("versuchte"));
      right = false;
    }

    // Teste calculateWeights(DocumentCollection dc)

    Method[] methods = WordCountsArray.class.getDeclaredMethods();
    for (Method m : methods) {
      if (m.getName().toLowerCase().equals("calculateweights")) {
        m.setAccessible(true);
        m.invoke(wca, docCol);
      }
    }

    WordCount[] internalWca = null;
    Field[] fields = WordCountsArray.class.getDeclaredFields();
    for (Field f : fields) {
      if (f.getType().isArray()) {
        f.setAccessible(true);
        internalWca = (WordCount[]) f.get(wca);
      }
    }

    if (internalWca != null) {
      if (internalWca[0].getWeight() != 0.5753641449035617) {
        sb.append(
            "\n>>> DocumentCollection.calculateWeights(DocumentCollection dc) ist falsch implementiert. SOLL 0.5753641449035617 sein ist "
                + internalWca[0].getWeight());
        right = false;
      }
    } else {
      sb.append("\n>>> There seems to be a problem with WordCountsArray, no internal array found! ");
      right = false;
    }

    // Teste calculateNormalizedWeights(DocumentCollection dc)
    for (Method m : methods) {
      if (m.getName().toLowerCase().equals("calculatenormalizedweights")) {
        m.setAccessible(true);
        m.invoke(wca, docCol);
      }
    }

    for (Field f : fields) {
      if (f.getType().isArray()) {
        f.setAccessible(true);
        internalWca = (WordCount[]) f.get(wca);
      }
    }

    if (internalWca != null) {
      if (internalWca[0].getNormalizedWeight() != 0.316227766016838) {
        sb.append(
            "\n>>> DocumentCollection.calculateNormalizedWeights(DocumentCollection dc) ist falsch implementiert. SOLL 0.316227766016838 sein ist "
                + internalWca[0].getNormalizedWeight());
        right = false;
      }
    } else {
      sb.append("\n>>> There seems to be a problem with WordCountsArray, no internal array found! ");
      right = false;
    }
    // Teste scalarProduct(WordCountsArray wca, DocumentCollection dc)

    wca2 = docCol.get(0).getWordCounts();
    wca = docCol.get(1).getWordCounts();

    // Rechne normalizedWeights fuer wca2
    for (Method m : methods) {
      if (m.getName().toLowerCase().equals("calculatenormalizedweights")) {
        m.setAccessible(true);
        m.invoke(wca2, docCol);
      }
    }

    double scalar = -1;

    for (Method m : methods) {
      if (m.getName().toLowerCase().equals("scalarproduct") && m.getParameterTypes().length == 2) {
        m.setAccessible(true);
        scalar = (double) m.invoke(wca, wca2, docCol);
      }
    }

    if (scalar == -1) {
      sb.append("\n>>> WordCountsArray.scalarProduct(WordCountsArray wca, DocumentCollection dc) not found");
      right = false;
    } else if (scalar != 0.7954236646622413) {
      sb.append(
          "\n>>> WordCountsArray.scalarProduct(WordCountsArray wca, DocumentCollection dc) ist falsch implementiert. SOLL 0.7954236646622413 sein ist "
              + scalar);
      right = false;
    }

    LinkedDocument atxt = new LinkedDocument("harry", "", "", null, null,
        "blablabla tralalalal tetsetse ende", "harry"); // removed link, since match(...) would try to load it

    LinkedDocumentCollection ldc = new LinkedDocumentCollection();
    ldc.prependDocument(atxt);

    ldc.match("blablabla ");
    double d = ldc.getQuerySimilarity(0);
    if (d != 0.20840410544601642) {
      sb.append(
          "\n>>> DocumentCollection.match(query) ist falsch implementiert. SOLL 0.20840410544601642 sein ist " + d);
      right = false;
    }

    assertTrue(sb.toString(), right);
  }

}

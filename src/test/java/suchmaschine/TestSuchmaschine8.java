package suchmaschine;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class TestSuchmaschine8 {

  private static final DocumentCollectionTest.FileContent[] FILE_CONTENTS = new DocumentCollectionTest.FileContent[] {
          new DocumentCollectionTest.FileContent("A.txt", "blablabla link:B.txt tralalalal link:C.txt tetsetse ende"),
          new DocumentCollectionTest.FileContent("B.txt", "ich bin B und verlinke niemanden, außer link:A.txt link:C.txt und link:D.txt"),
          new DocumentCollectionTest.FileContent("C.txt", "dies ist die datei c die auf datei d verlinkt link:D.txt"),
          new DocumentCollectionTest.FileContent("D.txt", "es ist so ein schoener tag.. verlink einfach mal auf datei c link:C.txt")
  };

  @BeforeClass
  public static void createFiles() throws IOException {
    DocumentCollectionTest.createFiles(FILE_CONTENTS);
  }

  @AfterClass
  public static void deleteFiles() throws IOException {
    DocumentCollectionTest.deleteFiles(FILE_CONTENTS);
  }

  @Test
  public void PageRank() throws Exception {
    boolean right = true;
    StringBuilder sb = new StringBuilder();
    sb.append("\n>>> Test PageRank");

    LinkedDocumentCollection ldc = new LinkedDocumentCollection();
    ldc.appendDocument((LinkedDocument.createLinkedDocumentFromFile("A.txt")));
    int sizeBefore = ldc.numDocuments();
    ldc = ldc.crawl();
    if ((ldc.numDocuments() != 4) || (ldc.numDocuments() < sizeBefore)) {
      sb.append(
          "\n>>> LinkedDocumentCollection.crawl() ist falsch. # gecrawlter Dokumente: Soll 4, IST " + ldc.numDocuments());
      right = false;
    }

    double[] probe = ldc.pageRank(0.85);
    double[] probeRec = ldc.pageRankRec(0.85);
    double[] target = {0.054713, 0.060753, 0.448551, 0.435982};

    for (int i = 0; i < target.length; i++) {
      probe[i] = Math.floor(probe[i] * Math.pow(10, 6));
      probeRec[i] = Math.floor(probeRec[i] * Math.pow(10, 6));
      target[i] =  Math.floor(target[i] * Math.pow(10, 6));
    }

    for (int i = 0; i < target.length; i++)
      if (probe[i] != target[i]) {
        right = false;
        sb.append("\n>>> Pagerank iterativ wird falsch berechnet.");
        sb.append("\n>>> SOLL: " + Arrays.toString(target));
        sb.append(", IST: " + Arrays.toString(probe));
        break;
      }

    for (int i = 0; i < target.length; i++)
      if (probeRec[i] != target[i]) {
        right = false;
        sb.append("\n>>> Pagerank rekursiv wird falsch berechnet.");
        sb.append("\n>>> SOLL: " + Arrays.toString(target));
        sb.append(", IST: " + Arrays.toString(probeRec));
        break;
      }

    assertTrue(sb.toString(), right);
  }

  @Test
  public void similarity() throws Exception {
    boolean right = true;
    double dampingfac = 0.85;
    double weightingfac = 0.6;
    double[] result = new double[0];
    StringBuilder sb = new StringBuilder();
    sb.append("\n>>> Test similarity:");

    LinkedDocumentCollection ldc = new LinkedDocumentCollection();
    ldc.appendDocument(LinkedDocument.createLinkedDocumentFromFile("A.txt"));
    ldc = ldc.crawl();

    Method[] methods = ldc.getClass().getDeclaredMethods();
    for (Method m : methods) {
      if (m.getName().toLowerCase().equals("sortbyrelevance")) {
        m.setAccessible(true);
        result = (double[]) m.invoke(ldc, dampingfac, weightingfac);
      }
    }

    if ((result == null) || (result.length == 0)) {
      right = false;
      String res = (result == null) ? "null" : "length==0";
      sb.append("\n>>> " + ldc.getClass().getName() + ".sortByRelevance() liefert " + res);
    } else {
      double[] pr = ldc.pageRank(0.85);
      double target = 0.179420;

      result[0] = Math.floor(result[0] * 1000000);
      target *= 1000000;

      if (target != result[0]) {
        sb.append("\n>>> " + ldc.getClass().getName() + ".sortByRelevance berechnet falsch. SOLL " + target + ", IST "
            + result[0]);
        right = false;
      }
    }

    assertTrue(sb.toString(), right);
  }
}

package testit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import suchmaschine.DocumentCollectionTest;

import java.io.*;
import java.net.Socket;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TestItServerTest {

  public static final DocumentCollectionTest.FileContent[] FILE_CONTENTS = {
          new DocumentCollectionTest.FileContent("A.txt", "blablabla link:B.txt tralalalal link:C.txt tetsetse ende"),
          new DocumentCollectionTest.FileContent("B.txt", "ich bin B und verlinke link:Pinguine.txt niemanden, außer link:A.txt link:C.txt und link:D.txt"),
          new DocumentCollectionTest.FileContent("C.txt", "dies ist die datei c die auf datei d verlinkt link:D.txt"),
          new DocumentCollectionTest.FileContent("D.txt", "es ist so ein schoener tag.. verlink einfach mal auf datei c link:C.txt"),
          new DocumentCollectionTest.FileContent("E.txt", "pinguine link:Pinguine.txt verlinken ueblich nur auf auf link:C.txt"),
          new DocumentCollectionTest.FileContent("Pinguine.txt", "pinguine sind lustige tierchen link:Tierchen.txt aus dem ewigen eis"),
          new DocumentCollectionTest.FileContent("Tierchen.txt", "tierchen sind kleine link:A.txt tiere")
  };

  private static Thread testItServer;
  @BeforeClass
  public static void startTestItServer() throws IOException {
    DocumentCollectionTest.createFiles(FILE_CONTENTS);
    testItServer = new Thread(() -> TestItServer.main(new String[0]));
    testItServer.start();
    try {
      Thread.sleep(1000); // ensure it is started
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("deprecation")
  @AfterClass
  public static void stopTestItServer() {
    testItServer.stop();
    DocumentCollectionTest.deleteFiles(FILE_CONTENTS);
  }

  private static void testPrompt(BufferedReader in) throws IOException {
    char[] buf = new char[2];
    in.read(buf);
    assertArrayEquals(new char[] { '>', ' ' }, buf);
  }

  private static void skipNewline(BufferedReader in) throws IOException {
    in.mark(100);
    char c = (char)in.read();
    if(c == '\n')
      return;
    in.reset();
  }

  public static void testClient() throws IOException {
    Socket socket = new Socket("127.0.0.1", 8000);

    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

      testPrompt(in);
      out.println("add B.txt:link:A.txt link:E.txt");
      testPrompt(in);
      out.println("add A.txt:blablabla link:B.txt tralalalal link:C.txt tetsetse ende");
      testPrompt(in);
      out.println("add C.txt:dies ist die datei c die auf datei d verlinkt link:D.txt");
      testPrompt(in);
      out.println("add D.txt:es ist so ein schoener tag.. verlink einfach mal auf datei c link:C.txt");
      testPrompt(in);
      out.println("add E.txt:pinguine link:Pinguine.txt verlinken ueblich nur auf auf link:C.txt");
      testPrompt(in);
      out.println("crawl");
      testPrompt(in);

      out.println("query einmal");
      assertEquals("1. D.txt; Relevanz: 0.28615880434508845".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("2. C.txt; Relevanz: 0.14554138926995028".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("3. A.txt; Relevanz: 0.03854293763345878".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("4. B.txt; Relevanz: 0.024952177065648565".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("5. Tierchen.txt; Relevanz: 0.02278451036368182".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("6. E.txt; Relevanz: 0.01917610382432923".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("7. Pinguine.txt; Relevanz: 0.01672127269676851".substring(0, 30), in.readLine().trim().substring(0, 30));
      skipNewline(in);
      testPrompt(in);

      out.println("query datei");
      assertEquals("1. C.txt; Relevanz: 0.31378237144529836".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("2. D.txt; Relevanz: 0.24228781996357535".substring(0, 29), in.readLine().trim().substring(0, 29));
      assertEquals("3. A.txt; Relevanz: 0.038542937633458785".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("4. B.txt; Relevanz: 0.02495217706564857".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("5. Tierchen.txt; Relevanz: 0.022784510363681825".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("6. E.txt; Relevanz: 0.01917610382432923".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("7. Pinguine.txt; Relevanz: 0.01672127269676851".substring(0, 30), in.readLine().trim().substring(0, 30));
      skipNewline(in);
      testPrompt(in);

      out.println("list");

      assertEquals("C.txt", in.readLine().trim());
      assertEquals("D.txt", in.readLine().trim());
      assertEquals("A.txt", in.readLine().trim());
      assertEquals("B.txt", in.readLine().trim());
      assertEquals("Tierchen.txt", in.readLine().trim());
      assertEquals("E.txt", in.readLine().trim());
      assertEquals("Pinguine.txt", in.readLine().trim());
      skipNewline(in);
      testPrompt(in);

      out.println("count ein");
      assertEquals("C.txt: 0x", in.readLine().trim());
      assertEquals("D.txt: 1x", in.readLine().trim());
      assertEquals("A.txt: 0x", in.readLine().trim());
      assertEquals("B.txt: 0x", in.readLine().trim());
      assertEquals("Tierchen.txt: 0x", in.readLine().trim());
      assertEquals("E.txt: 0x", in.readLine().trim());
      assertEquals("Pinguine.txt: 0x", in.readLine().trim());
      skipNewline(in);
      testPrompt(in);

      out.println("pageRank");
      assertEquals("C.txt; PageRank: 0.3638534731748756".substring(0, 25), in.readLine().trim().substring(0, 25));
      assertEquals("D.txt; PageRank: 0.3307040228654089".substring(0, 25), in.readLine().trim().substring(0, 25));
      assertEquals("A.txt; PageRank: 0.09635734408364695".substring(0, 25), in.readLine().trim().substring(0, 25));
      assertEquals("B.txt; PageRank: 0.06238044266412142".substring(0, 25), in.readLine().trim().substring(0, 25));
      assertEquals("Tierchen.txt; PageRank: 0.05696127590920456".substring(0, 25), in.readLine().trim().substring(0, 25));
      assertEquals("E.txt; PageRank: 0.047940259560823074".substring(0, 25), in.readLine().trim().substring(0, 25));
      assertEquals("Pinguine.txt; PageRank: 0.041803181741921276".substring(0, 25), in.readLine().trim().substring(0, 25));
      skipNewline(in);
      testPrompt(in);

    } finally {
      socket.close();
    }
  }

  @Test(timeout = 3000)
  public void testSingle() throws IOException {
    /*
     * 4P
     *
     * Working directory für den Server: suchmaschine_testdocs
     */
    testClient();
  }

  @Test(timeout = 10000)
  public void testMulti() throws IOException {
    /*
     * 1P Abzug, wenn dieser Test nicht funktioniert
     *
     * Working directory für den Server: suchmaschine_testdocs
     */
    testClient();
    testClient();
    testClient();
    testClient();
  }

}

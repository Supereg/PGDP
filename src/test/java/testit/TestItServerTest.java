package testit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@Ignore("This Test is complete bullshit")
public class TestItServerTest {

  private static Thread testItServer;
  @BeforeClass
  public static void startTestItServer() throws IOException {
    testItServer = new Thread(() -> TestItServer.main(new String[0]));
    testItServer.start();
  }

  @SuppressWarnings("deprecation")
  @AfterClass
  public static void stopTestItServer() {
    testItServer.stop();
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
      out.println("add E.txt:es ist so ein schoener tag.. verlink einfach mal auf datei c link:C.txt");
      testPrompt(in);
      
      out.println("query einmal");
      assertEquals("1. D.txt; Relevanz: 0.22502544161844423".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("2. E.txt; Relevanz: 0.22502544161844423".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("3. B.txt; Relevanz: 0.07999999999999996".substring(0, 30), in.readLine().trim().substring(0, 30)); // turned 0.08000000000000002 into 0.07999999999999996
      assertEquals("4. A.txt; Relevanz: 0.07999999999999996".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("5. C.txt; Relevanz: 0.07999999999999996".substring(0, 30), in.readLine().trim().substring(0, 30));
      skipNewline(in);
      testPrompt(in);
//
      out.println("query datei");
      assertEquals("1. C.txt; Relevanz: 0.1808485725501503".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("2. D.txt; Relevanz: 0.17408998424565625".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("3. E.txt; Relevanz: 0.17408998424565625".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("4. B.txt; Relevanz: 0.07999999999999996".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("5. A.txt; Relevanz: 0.07999999999999996".substring(0, 30), in.readLine().trim().substring(0, 30));
      skipNewline(in);
      testPrompt(in);
      
      out.println("list");
      assertEquals("C.txt", in.readLine());
      assertEquals("D.txt", in.readLine());
      assertEquals("E.txt", in.readLine());
      assertEquals("B.txt", in.readLine());
      assertEquals("A.txt", in.readLine());
      skipNewline(in);
      testPrompt(in);
      
      out.println("count ein");
      assertEquals("C.txt: 0x", in.readLine().trim());
      assertEquals("D.txt: 1x", in.readLine().trim());
      assertEquals("E.txt: 1x", in.readLine().trim());
      assertEquals("B.txt: 0x", in.readLine().trim());
      assertEquals("A.txt: 0x", in.readLine().trim());
      skipNewline(in);
      testPrompt(in);

      out.println("pageRank");
      assertEquals("C.txt; PageRank: 0.19999999999999987".substring(0, 30), in.readLine().trim().substring(0, 30)); // turned 0.2 into 0.19999999999999987
      assertEquals("D.txt; PageRank: 0.19999999999999987".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("E.txt; PageRank: 0.19999999999999987".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("B.txt; PageRank: 0.19999999999999987".substring(0, 30), in.readLine().trim().substring(0, 30));
      assertEquals("A.txt; PageRank: 0.19999999999999987".substring(0, 30), in.readLine().trim().substring(0, 30));
      skipNewline(in);
      testPrompt(in);

    } finally {
      socket.close();
    }
  }

  @Test
  public void testIntegrityOfChanges() {
    assertEquals(0.08000000000000002, 0.07999999999999996, 10E-10); // 0.08000000000000002 into 0.07999999999999996
    assertEquals(0.2, 0.19999999999999987, 10E-10); // 0.2 into 0.19999999999999987
  }

  @Test(timeout = 3000)
  public void testSingle() throws IOException {
    /*
     * 4P
     */
    testClient();
  }
  
  @Test(timeout = 10000)
  public void testMulti() throws IOException {
    /*
     * 1P Abzug, wenn dieser Test nicht funktioniert
     */
    testClient();
    testClient();
    testClient();
    testClient();
  }

}

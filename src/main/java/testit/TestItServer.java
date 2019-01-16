package testit;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TestItServer {

    /*
        Bzgl Blatt 10:
        Es können sich mehrere Clients gleichzeitig verbinden (Networking is von grund auf parallel)! Allerdings können
        nicht mehrere Anfragen gleichzeitig bearbeitet werden, da wir unseren TestItServer single threaded programmiert
        haben. Heißt, dass die Anfrage eines Clients erst komplett abgearbeitet werden muss (bis dieser Verbindung
        verliert oder "exit" eingebt), bevor die Anfrage des nächsten Clients (mit call von #serverSocket.accept())
        begonnen wird. Damit ein gleichzeitiges Benutzten möglich ist, müsste das Abarbeiten der Anfrage auf einen
        externen Thread ausgelagert werden.
     */

    public static void main(String[] args) {
        SynchronizedLdcWrapper ldcWrapper = new SynchronizedLdcWrapper();
        try (ServerSocket serverSocket = new ServerSocket(8000)) {
            //noinspection InfiniteLoopStatement
            for (;;) {
                System.out.println("Waiting for client...");

                try {
                    Socket socket = serverSocket.accept();
                    TestItThread clientThread = new TestItThread(ldcWrapper, socket);
                    clientThread.start();
                    System.out.println("Accepted client!");
                } catch (IOException e) {
                    System.err.println("Error establishing client socket: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Unable to establish serverSocket!");
            e.printStackTrace();
        }
    }

}
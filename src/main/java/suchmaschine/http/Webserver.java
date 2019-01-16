package suchmaschine.http;

import suchmaschine.LinkedDocument;
import suchmaschine.LinkedDocumentCollection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Webserver {

    private static final LinkedDocumentCollection DOCUMENT_COLLECTION;
    private static final TemplateProcessor TEMPLATE_PROCESSOR;

    static { // crawling at beginning
        LinkedDocumentCollection collection = new LinkedDocumentCollection();
        collection.appendDocument(
                new LinkedDocument("B.txt", "", "", null, null, "link:A.txt link:E.txt", "B.txt")
        );
        DOCUMENT_COLLECTION = collection.crawl();
        TEMPLATE_PROCESSOR = new TemplateProcessor("search.html");
    }

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(80)) {
            //noinspection EndlessStream,ResultOfMethodCallIgnored
            Stream.iterate(0, i -> 0).peek(i -> {
                System.out.println("Waiting for client...");

                try {
                    Socket socket = serverSocket.accept();
                    WebserverThread clientThread = new WebserverThread(TEMPLATE_PROCESSOR, DOCUMENT_COLLECTION, socket);
                    clientThread.start();
                    System.out.println("Accepted client!");
                } catch (IOException e) {
                    System.err.println("Error establishing socket connection: " + e.getMessage());
                }
            }).collect(Collectors.toSet());
        } catch (IOException e) {
            System.err.println("Unable to establish serverSocket!");
            e.printStackTrace();
        }
    }

}
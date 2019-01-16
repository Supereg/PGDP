import de.andi.minijava.Terminal;
import http.*;
import http.exceptions.InvalidHttpMethodException;
import http.exceptions.InvalidRequestException;

import java.io.*;
import java.net.Socket;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WebserverThread extends Thread {

    private static final Lock collectionLock = new ReentrantLock(true);

    private final Socket socket;
    private final TemplateProcessor processor;
    private final LinkedDocumentCollection collection;

    public WebserverThread(TemplateProcessor processor, LinkedDocumentCollection collection, Socket socket) {
        this.socket = socket;
        this.processor = processor;
        this.collection = collection;
    }

    @Override
    public void run() {
        try (socket) { // try-resource with socket!
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                 PrintWriter out = new PrintWriter(outputStream)) {
                HttpResponse response;

                requestHandling: {
                    HttpRequest request;
                    try {
                        request = new HttpRequest(in.readLine());
                    } catch (InvalidHttpMethodException e) {
                        response = new HttpResponse(HttpStatus.MethodNotAllowed, "Method not allowed");
                        break requestHandling;
                    } catch (InvalidRequestException e) {
                        response = new HttpResponse(HttpStatus.BadRequest, "Bad Request");
                        break requestHandling;
                    }

                    if (!request.getMethod().equals(HttpMethod.GET)) {
                        response = new HttpResponse(HttpStatus.MethodNotAllowed, "Method not allowed");
                        break requestHandling;
                    }

                    String path = request.getPath();
                    String queryParam = request.getQueryParameter("query");
                    switch (path) {
                        case "/":
                            response = handleMainPage();
                            break;
                        case "/search":
                            if (queryParam == null || queryParam.isEmpty())
                                response = handleMainPage();
                            else {
                                queryParam = queryParam.replace("+", " "); // '+' are generated for spaces when sending forms with GET
                                response = handleSearchQuery(queryParam);
                            }
                            break;
                        case "/favicon.ico": // explicitly ignore favicon.ico, otherwise it would try to read it from disk every time
                            response = new HttpResponse(HttpStatus.NotFound, "favicon not found");
                            break;
                        default:
                            response = handleFileRequest(path.substring(1)); // handle file with '/' remove
                            break;
                    }
                }

                out.print(response.toString());
                out.flush();
            } catch (IOException e) {
                System.err.println("Error occurred handling client: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Error occurred trying to establish in/out streams: " + e.getMessage());
        }
    }

    private HttpResponse handleMainPage() {
        Map<String, String> variables = new HashMap<>();
        variables.put("%search_value", "");
        variables.put("%content", "");

        return processSearchTemplate(variables);
    }

    private HttpResponse handleSearchQuery(String query) {
        // since we currently have a lock, we save the size (which doesn't change) but is critical when sorting, since
        // it iterates over all documents in order to count all documents; size is later used.

        List<Entry<String, Double>> relevanceByNameList;
        collectionLock.lock(); // locking the collection
        try {
            // first of all #match MUST be locked since it rewrites order of the LinkedDocumentList
            collection.match(query);

            // this iteration MUST also be locked, since it expects the indices calculated above and those must not
            // be changed until the current relevance was read
            relevanceByNameList = documentStream(collection)
                    .map(d -> (LinkedDocument) d)
                    .map(d -> new SimpleEntry<>(d.getID(), collection.getRelevance(collection.indexOf(d))))
                    .collect(Collectors.toList());
        } finally {
            collectionLock.unlock(); // unlocking the collection
        }

        List<String> contentParts = new ArrayList<>();
        contentParts.add("<table style=\"margin-left: auto; margin-right: auto\" border=\"1px solid black\">\n");
        contentParts.add(
                "<tr>\n" +
                        "    <td><b>ID</b></td>\n" +
                        "    <td><b>Page</b></td>\n" +
                        "    <td><b>Relevance</b></td>\n" +
                        "</tr>\n"
        );

        for (int i = 0; i < relevanceByNameList.size(); i++) {
            Entry<String, Double> entry = relevanceByNameList.get(i);
            contentParts.add("<tr>\n" +
                    "    <td>" + (i + 1) + "</td>\n" +
                    "    <td><a href=\"" + entry.getKey() + "\">" + entry.getKey() + "</a></td>\n" +
                    "    <td>" + entry.getValue() + "</td>\n" +
                    "</tr>\n");
        }

        contentParts.add("</table>\n");

        Map<String, String> variables = new HashMap<>();
        variables.put("%search_value", query);
        variables.put("%content", String.join("", contentParts));

        return processSearchTemplate(variables);
    }

    private HttpResponse handleFileRequest(String fileName) {
        String[] lines = Terminal.readFile(fileName);

        if (lines == null || lines.length < 2) // shouldn't it rather be lines.length != 2? instructions states however length < 2
            return new HttpResponse(HttpStatus.NotFound, "Not Found"); // Improve body

        return new HttpResponse(HttpStatus.Ok, lines[1] + System.lineSeparator());
    }

    private HttpResponse processSearchTemplate(Map<String, String> variables) {
        String body = processor.replace(variables); // doesn't need to be synchronized @see TemplateProcessor#replace
        if (body == null)
            return new HttpResponse(HttpStatus.BadRequest, "Internal server error");
        return new HttpResponse(HttpStatus.Ok, body);
    }

    // Don't know if we should also include DocumentCollection in our solution, so keep it save and include it here
    private Stream<Document> documentStream(DocumentCollection collection) {
        return StreamSupport.stream(Spliterators.spliterator(collection.iterator(), collection.numDocuments(), 0), false);
    }

}
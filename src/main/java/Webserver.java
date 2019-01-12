import de.andi.minijava.Terminal;
import http.*;
import http.exceptions.InvalidHttpMethodException;
import http.exceptions.InvalidRequestException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Webserver {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(80)) {

            //noinspection EndlessStream,ResultOfMethodCallIgnored
            Stream.iterate(0, i -> 0).peek(i -> {
                System.out.println("Waiting for client...");

                try (Socket socket = serverSocket.accept()) {
                    ClientHandler handler = new ClientHandler(socket);
                    handler.handle();
                } catch (IOException e) {
                    System.err.println("Client disconnected: " + e.getMessage());
                }
            }).collect(Collectors.toSet());
        } catch (IOException e) {
            System.err.println("Unable to establish serverSocket!");
            e.printStackTrace();
        }
    }

    private static HttpResponse handleMainPage() {
        Map<String, String> variables = new HashMap<>();
        variables.put("%search_value", "");
        variables.put("%content", "");

        return processSearchTemplate(variables);
    }

    private static HttpResponse handleSearchQuery(String query) {
        LinkedDocumentCollection resultCollection;
        LinkedDocumentCollection collection = new LinkedDocumentCollection();
        collection.appendDocument(
                new LinkedDocument("B.txt", "", "", null, null, "link:A.txt link:E.txt", "B.txt")
        );
        resultCollection = collection.crawl();
        resultCollection.match(query);


        List<String> contentParts = new ArrayList<>();
        contentParts.add("<table style=\"margin-left: auto; margin-right: auto\" border=\"1px solid black\">\n");
        contentParts.add(
                "<tr>\n" +
                "    <td><b>ID</b></td>\n" +
                "    <td><b>Page</b></td>\n" +
                "    <td><b>Relevance</b></td>\n" +
                "</tr>\n"
        );

        List<String> rows =  documentStream(resultCollection).map(d -> (LinkedDocument) d).map(document -> {
            int documentIndex = resultCollection.indexOf(document);
            double relevance = resultCollection.getRelevance(documentIndex);

            return "<tr>\n" +
                    "    <td>" + (documentIndex + 1) + "</td>\n" +
                    "    <td><a href=\"" + document.getID() + "\">" + document.getID() + "</a></td>\n" +
                    "    <td>" + relevance + "</td>\n" +
                    "</tr>\n";
        }).collect(Collectors.toList());
        contentParts.addAll(rows);

        contentParts.add("</table>\n");

        Map<String, String> variables = new HashMap<>();
        variables.put("%search_value", query);
        variables.put("%content", String.join("", contentParts));

        return processSearchTemplate(variables);
    }

    private static HttpResponse handleFileRequest(String fileName) {
        String[] lines = Terminal.readFile(fileName);

        if (lines == null || lines.length < 2) // shouldn't it rather be lines.length != 2? instructions states however length < 2
            return new HttpResponse(HttpStatus.NotFound, "Not Found"); // Improve body

        return new HttpResponse(HttpStatus.Ok, lines[1] + System.lineSeparator());
    }

    private static HttpResponse processSearchTemplate(Map<String, String> variables) {
        TemplateProcessor processor = new TemplateProcessor("search.html");
        String body = processor.replace(variables);
        if (body == null)
            return new HttpResponse(HttpStatus.BadRequest, "Internal server error");
        return new HttpResponse(HttpStatus.Ok, body);
    }

    // Don't know if we should also include DocumentCollection in our solution, so keep it save and include it here
    private static Stream<Document> documentStream(DocumentCollection collection) {
        return StreamSupport.stream(Spliterators.spliterator(collection.iterator(), collection.numDocuments(), 0), false);
    }

    private static class ClientHandler {

        private final Socket socket;

        private ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void handle() throws IOException {
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
                System.err.println("Error occurred handling client");
                e.printStackTrace();
            }
        }

    }

}
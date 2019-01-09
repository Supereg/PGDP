import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TestItServer {

    /*
        TODO why gleichzeitig verbinden
        Es können sich mehrere Clients gleichzeitig verbinden! Allerdings können nicht mehrere Anfragen gleichzeitig
        bearbeitet werden, da hier eine Anfrage immer erst komplett abgearbeitet wird bevor #serverSocket.accept()
        erneut aufgerufen wird, also der nächste Socket "geholt" wird und dessen Anfrage "bearbeitet" wird. Damit dies
        funktionieren kann, müsste das abarbeiten der Anfrage auf einen externen Thread ausgelagert werden.
     */

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8000)) {
            //noinspection InfiniteLoopStatement
            for (;;) {
                System.out.println("Waiting for client...");

                try (Socket socket = serverSocket.accept()) {
                    ClientHandler clientHandler = new ClientHandler(socket);
                    clientHandler.handle();
                } catch (IOException e) {
                    System.err.println("Client disconnected: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Unable to establish serverSocket!");
            e.printStackTrace();
        }
    }

    private static String[] splitByFirstCharacter(String input, char character) {
        String[] commandArgument = new String[2];

        int index = input.indexOf(character);
        commandArgument[0] = index > -1? input.substring(0, index): input;
        commandArgument[1] = index > -1? input.substring(index+1): null;

        return commandArgument;
    }

    private static class ClientHandler {

        private final Socket socket;
        private final LinkedDocumentCollection collection;

        private BufferedReader in;
        private PrintWriter out;

        private ClientHandler(Socket socket) {
            this.socket = socket;

            this.collection = new LinkedDocumentCollection();
        }

        public void handle() throws IOException {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                 PrintWriter out = new PrintWriter(outputStream)) {
                this.in = in;
                this.out = out;

                String line;
                while ((line = in.readLine()) != null) {
                    if (line.toLowerCase().startsWith("exit")) {
                        System.out.println("Client disconnected.");
                        break;
                    }

                    String[] split = splitByFirstCharacter(line, ' ');
                    String command = split[0];
                    String argument = split[1];
                    try {
                        handleCommand(command.toLowerCase(), argument != null? argument.toLowerCase(): null);
                        out.flush();
                    } catch (Exception e) {
                        e.printStackTrace();

                        out.println("Error occurred handling command: " + e.getMessage());
                        out.println("Disconnecting...");
                        out.flush();
                        break;
                    }
                }

                if (line == null)
                    System.out.println("Client got disconnected!");
            } catch (IOException e) {
                System.err.println("Error occurred handling client");
                e.printStackTrace();
            } finally {
                in = null;
                out = null;
            }
        }

        private void handleCommand(String command, String arguments) {
            switch (command) {
                case "add":
                    if (arguments == null) {
                        out.println("Missing argument for command 'add'");
                        break;
                    }

                    add(arguments);
                    break;
                case "list":
                    collection.iterate(this::listDocument);
                    break;
                case "count":
                    if (arguments == null) {
                        out.println("Missing argument for command 'count'");
                        break;
                    }

                    count(arguments);
                    break;
                case "query":
                    if (arguments == null) {
                        out.println("Missing argument for command 'query'");
                        break;
                    }

                    query(arguments);
                    break;
                case "crawl":
                    DocumentCollection result = collection.crawl();

                    for (Document document: result)
                        collection.appendDocument(document);
                    break;
                case "pagerank":
                    pageRank();
                    break;
                default:
                    out.println("Unrecognized command: " + command);
            }
        }

        private void add(String argument) {
            String[] split = splitByFirstCharacter(argument, ':');
            String title = split[0];
            String content = split[1];

            Document document = new LinkedDocument(title, "de", "", new Date(), null, content, title);
            collection.appendDocument(document);
        }

        private void listDocument(Document document) {
            out.println(document.getTitle());
        }

        private void count(String word) {
            collection.iterate(document -> {
                WordCountsArray wordCounts = document.getWordCounts();
                int index = wordCounts.getIndexOfWord(word);
                int count = wordCounts.getCount(index);

                out.println(document.getTitle() +": " + (count > 0? count + "x": "gar nicht."));
            });
        }

        private void query(String queryString) {
            collection.match(queryString);

            int index = 0;
            for (Document document: collection) {
                out.println((index + 1) + ". " + document.getTitle() + "; Relevanz: "
                        + collection.getRelevance(index));
                index++;
            }
        }

        private void pageRank() {
            double[] pageRank = collection.pageRankRec(0.85);

            int index = 0;
            for (Document document: collection)
                out.println(document.getTitle() + "; PageRank: " + pageRank[index++]);
        }

    }

}
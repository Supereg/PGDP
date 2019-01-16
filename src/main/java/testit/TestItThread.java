package testit;

import suchmaschine.Date;
import suchmaschine.LinkedDocument;
import suchmaschine.WordCountsArray;

import java.io.*;
import java.net.Socket;

public class TestItThread extends Thread {

    private final SynchronizedLdcWrapper ldcWrapper;
    private final Socket socket;

    public TestItThread(SynchronizedLdcWrapper ldcWrapper, Socket socket) {
        this.ldcWrapper = ldcWrapper;
        this.socket = socket;
    }

    @Override
    public void run() {
        try (socket) {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                 PrintWriter out = new PrintWriter(outputStream)) {
                out.print("> ");
                out.flush();

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
                        handleCommand(command, argument, out);
                        out.print("> ");
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
                System.err.println("Error occurred handling client: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Error opening in/out stream: " + e.getMessage());
        }
    }

    private void handleCommand(String command, String argument, PrintWriter out) {
        switch (command.toLowerCase()) {
            case "add":
                if (argument == null) {
                    out.println("command usage: add <name:content>");
                    break;
                }

                String[] split = splitByFirstCharacter(argument, ':');
                LinkedDocument document0 = new LinkedDocument(split[0], "de", "", new Date(), null, split[1], split[0]);
                ldcWrapper.appendDocument(document0);
                break;
            case "list":
                ldcWrapper.forEach(document -> out.println(document.getTitle()));
                break;
            case "count":
                if (argument == null) {
                    out.println("command usage: count <word>");
                    break;
                }

                ldcWrapper.forEach(document -> {
                    WordCountsArray wordCounts = document.getWordCounts();
                    int index = wordCounts.getIndexOfWord(argument.toLowerCase());
                    int count = Math.max(wordCounts.getCount(index), 0);

                    out.println(document.getTitle() + ": " + count + "x");
                });
                break;
            case "query":
                if (argument == null) {
                    out.println("command usage: query <query>");
                    break;
                }

                var context = new Object() {
                    int num = 1;
                };
                ldcWrapper.query(argument,
                        (document, relevance) -> out.println(context.num++ + ". " + document.getTitle() + "; Relevanz: " + relevance));
                break;
            case "crawl":
                ldcWrapper.crawl();
                break;
            case "pagerank":
                ldcWrapper.pageRank(((document, pageRank) -> out.println(document.getTitle() + "; PageRank: " + pageRank)));
                break;
            default:
                out.println("Unrecognized command: " + command);
                out.println("Available commands: [add, count, crawl, list, query, pageRank]");
        }
    }

    private static String[] splitByFirstCharacter(String input, char character) {
        String[] commandArgument = new String[2];

        int index = input.indexOf(character);
        commandArgument[0] = index > -1? input.substring(0, index): input;
        commandArgument[1] = index > -1? input.substring(index+1): null;

        return commandArgument;
    }

}
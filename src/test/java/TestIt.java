public class TestIt {

    private static final LinkedDocumentCollection COLLECTION = new LinkedDocumentCollection();

    public static void main(String[] args) {
        while (true) {
            String input = Terminal.askString("> ");

            String[] split = splitByFirstCharacter(input, ' ');
            String command = split[0];
            String argument = split[1];

            switch (command) {
                case "exit":
                    return;
                case "add":
                    if (argument == null) {
                        System.out.println("Missing argument for command 'add'");
                        break;
                    }

                    add(argument);
                    break;
                case "list":
                    COLLECTION.iterate(TestIt::list);
                    break;
                case "count":
                    if (argument == null) {
                        System.out.println("Missing argument for command 'count'");
                        break;
                    }

                    count(argument);
                    break;
                case "query":
                    if (argument == null) {
                        System.out.println("Missing argument for command 'query'");
                        break;
                    }

                    query(argument);
                    break;
                case "crawl":
                    DocumentCollection result = COLLECTION.crawl();

                    for (Document document: result)
                        COLLECTION.appendDocument(document);
                    break;
                default:
                    System.err.println("Unrecognized command: " + command);
            }
        }
    }

    private static void add(String argument) {
        String[] split = splitByFirstCharacter(argument, ':');
        String title = split[0];
        String content = split[1];

        Document document = new LinkedDocument(title, "de", "", new Date(), null, content, title);
        COLLECTION.appendDocument(document);
    }

    private static void list(Document document) {
        System.out.println(document.getTitle());
    }

    private static void count(String word) {
        COLLECTION.iterate(document -> {
            WordCountsArray wordCounts = document.getWordCounts();
            int index = wordCounts.getIndexOfWord(word);
            int count = wordCounts.getCount(index);

            System.out.println(document.getTitle() +": " + (count > 0? count + "x": "gar nicht."));
        });
    }

    private static void query(String queryString) {
        COLLECTION.match(queryString);

        final int[] place = {1};
        COLLECTION.iterate(document -> {
            System.out.println(place[0] + ". " + document.getTitle() + "; Relevanz: "
                    + COLLECTION.getRelevance(place[0] - 1));
            place[0]++;
        });
    }

    private static String[] splitByFirstCharacter(String input, char character) {
        String[] commandArgument = new String[2];

        int index = input.indexOf(character);
        commandArgument[0] = index > -1? input.substring(0, index): input;
        commandArgument[1] = index > -1? input.substring(index+1): null;

        return commandArgument;
    }

    /**
     * Bzgl Aufgabe 7.7
     *      Gegebene Dateien sind als Json veranschaulicht
     *
     * Testfall 1:
     *      - Dateien:
     *       [
     *           {
     *                "title": "A",
     *                "content": "link:B"
     *           },
     *           {
     *               "title": "C",
     *               "content": "link:C"
     *           },
     *           {
     *               "title": "D",
     *               "content": "link:E"
     *           },
     *           {
     *               "title": "E",
     *               "content": "link:A"
     *           }
     *       ]
     *
     *      - Befehle:
     *          > add B:link:C
     *          > list
     *          B
     *          > crawl
     *          > list
     *          B
     *          C
     *          D
     *          E
     *          A
     *
     * Testfall 2:
     *      - Dateien:
     *       [
     *           {
     *                "title": "A",
     *                "content": "link:B"
     *           },
     *           {
     *               "title": "C",
     *               "content": "link:A link:D"
     *           },
     *           {
     *               "title": "D",
     *               "content": "link:B link:C"
     *           },
     *           {
     *               "title": "E",
     *               "content": "link:A"
     *           }
     *       ]
     *
     *      - Befehle:
     *          > add B:link:C
     *          > list
     *          B
     *          > crawl
     *          > list
     *          B
     *          C
     *          A
     *          D
     *
     */

}
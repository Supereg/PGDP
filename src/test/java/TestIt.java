public class TestIt {

    private static final LinkedDocumentCollection COLLECTION = new LinkedDocumentCollection();

    public static void main(String[] args) {
        while (true) {
            String input = Terminal.askString("> ");

            String[] split = splitByFirstCharacter(input, ' ');
            String command = split[0];
            String argument = split[1];

            switch (command.toLowerCase()) {
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
                case "pagerank":
                    pageRank();
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

        int index = 0;
        for (Document document: COLLECTION) {
            System.out.println((index + 1) + ". " + document.getTitle() + "; Relevanz: "
                    + COLLECTION.getRelevance(index));
            index++;
        }
    }

    private static void pageRank() {
        double[] pageRank = COLLECTION.pageRankRec(0.85);

        int index = 0;
        for (Document document: COLLECTION)
            System.out.println(document.getTitle() + "; PageRank: " + pageRank[index++]);
    }

    private static String[] splitByFirstCharacter(String input, char character) {
        String[] commandArgument = new String[2];

        int index = input.indexOf(character);
        commandArgument[0] = index > -1? input.substring(0, index): input;
        commandArgument[1] = index > -1? input.substring(index+1): null;

        return commandArgument;
    }

    /**
     * Bzgl Aufgabe 8.12
     *      Gegebene Dateien sind als Json veranschaulicht
     *
     * Testfall 1:
     *      - Dateien:
     *       [
     *           {
     *                "title": "A",
     *                "content": "hallo link:B"
     *           },
     *           {
     *               "title": "C",
     *               "content": "freund link:E"
     *           },
     *           {
     *               "title": "D",
     *               "content": "wie link:C"
     *           },
     *           {
     *               "title": "E",
     *               "content": "gehts link:A"
     *           }
     *       ]
     *
     *      - Befehle:
     *          > add B:mein link:D
     *          > crawl
     *          > pageRank
     *          B; PageRank: 0.1999999605698945
     *          D; PageRank: 0.19999992446430387
     *          C; PageRank: 0.19999993579465827
     *          E; PageRank: 0.1999999454254595
     *          A; PageRank: 0.19999995361164058
     *          > query mein
     *          1. B; Relevanz: 0.6799999842279578
     *          2. A; Relevanz: 0.07999998144465624
     *          3. E; Relevanz: 0.07999997817018381
     *          4. C; Relevanz: 0.07999997431786332
     *          5. D; Relevanz: 0.07999996978572155
     *
     *
     * Testfall 2:
     *      - Dateien:
     *       [
     *           {
     *                "title": "A",
     *                "content": "asd asd asd link:C link:E"
     *           },
     *           {
     *               "title": "C",
     *               "content": "asd link:E"
     *           },
     *           {
     *               "title": "D",
     *               "content": "asd link:D link:E"
     *           },
     *           {
     *               "title": "E",
     *               "content": "asd asd link:D"
     *           }
     *       ]
     *
     *      - Befehle:
     *          > add B:asd asd asd asd link:A link:C link:E
     *          > crawl
     *          > pageRank
     *          B; PageRank: 0.030000000000000006
     *          A; PageRank: 0.038500000000000006
     *          C; PageRank: 0.05486250000000001
     *          E; PageRank: 0.4576418518588728
     *          D; PageRank: 0.4189955610104386
     *          > query asd
     *          1. E; Relevanz: 0.7830567407435491
     *          2. D; Relevanz: 0.7675982244041755
     *          3. C; Relevanz: 0.621945
     *          4. A; Relevanz: 0.6154
     *          5. B; Relevanz: 0.612
     *
     */

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
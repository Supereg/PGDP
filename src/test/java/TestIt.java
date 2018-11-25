/**
 * Created by Andi on 2018-11-25.
 */
public class TestIt {

    private static final DocumentCollection COLLECTION = new DocumentCollection();

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
                default:
                    System.err.println("Unrecognized command: " + command);
            }
        }
    }

    private static void add(String argument) {
        String[] split = splitByFirstCharacter(argument, ':');
        String titel = split[0];
        String content = split[1];

        Document document = new Document(titel, "de", "", new Date(), null, content);
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
            System.out.println(place[0] + ". " + document.getTitle() + "; Aehnlichkeit: "
                    + COLLECTION.getQuerySimilarity(place[0] - 1));
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

}
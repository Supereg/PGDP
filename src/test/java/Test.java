import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import static org.junit.Assert.*;

public class Test {

    private static final String CONTENT = "Mehrere Sicherheitsexperten, von denen einige zu den Entdeckern der " +
            "Spectre-Sicherheitslücken gehören, haben neue Angriffsmöglichkeiten untersucht. " +
            "Wie ArsTechnica berichtet, lassen sich die bereits bekannten Lücken noch mit sieben anderen Methoden " +
            "für Angriffe ausnutzen. "+
            ""+
            "In einer ebenfalls veröffentlichten Stellungnahme behauptet Intel allerdings, dass die bereits " +
            "verfügbaren Schutzmaßnahmen auch gegen die neuen Angriffstypen helfen. "+
            ""+
            "Eine weitere Beurteilung der angeblichen neuen Spectre-Attacken ist derzeit nicht möglich, " +
            "weil ArsTechnica in dem Artikel keine Quellen nennt und auch nur wenige Details verrät, nicht " +
            "einmal die konkreten Autoren. Anscheinend wurden bisher auch keine CVE-Nummern für diese " +
            "Sicherheitslücken vergeben. "+
            ""+
            "Spectre-Varianten "+
            ""+
            "Spectre-Varianten wie NetSpectre nutzen keine neue Spectre-Sicherheitslücke, sondern " +
            "eine bereits bekannte, allerdings auf anderem Weg. NetSpectre nutzt sozusagen Spectre V1 über " +
            "eine Netzwerkverbindung aus. Dagegen helfen die bekannten Schutzfunktionen gegen Spectre V1. "+
            ""+
            "Wenn der Fall bei den neuen Spectre-Varianten ähnlich liegt, dann wären bei den meisten Computern " +
            "keine neuen schutzmaßnahmen erforderlich. Doch erst mit detaillierteren Informationen zu den sieben " +
            "neuen Lücken ist eine genauere Einschätzung möglich.";

    private static String trimmedContent;
    private static final String[] RESULT_CONTENT = ("mehrere sicherheitsexpert von den einige zu d entdeckern d " +
            "spectre sicherheitslück gehör hab neue angriffsmöglichkeit untersucht w arstechnica berichtet " +
            "lass sich d bereits bekannt lück noch mit sieb ander method für angriffe ausnutz ein ebenfalls " +
            "veröffentlicht stellungnahme behauptet intel allerdings dass d bereits verfügbar schutzmaßnahm " +
            "auch geg d neu angriffstyp helf eine weitere beurteil d angebli neu spectre attack derzeit nicht " +
            "mög weil arstechnica dem artikel keine quell nennt und auch nur wenige details verrät nicht ein " +
            "d konkret autor anschein wurd bish auch keine cve nummern für diese sicherheitslück vergeb spectre " +
            "variant spectre variant w netspectre nutz keine neue spectre sicherheitslücke sondern eine bereits " +
            "bekannte allerdings auf anderem weg netspectre nutzt sozusag spectre üb eine netzwerkverbind aus " +
            "dageg helf d bekannt schutzfunktion geg spectre wenn d fall b d neu spectre variant ähn liegt dann " +
            "wär b d meist computern keine neu schutzmaßnahm erforder doch erst mit detaillierter information zu " +
            "d sieb neu lück eine genauere einschätz mög").split(" ");

    private Date date = new Date();
    private Author author = new Author("Top", "Siegried", date, "nowhere", "email@google.de");

    private WordCountsArray wordCountsArray;

    public static void main(String[] args) { // since we NEED to specify a main method, here is it
        trimContent();

        Test test = new Test();
        // TODO main method

        test.createWordCountsArray();
        test.testNegativeWordCountsArray();
        test.cleanUp();

        test.createWordCountsArray();
        test.testSimpleAdd();
        test.cleanUp();
    }

    @BeforeClass
    public static void trimContent() {
        trimmedContent = CONTENT.trim() // trim
                .toLowerCase() // lower case
                .replace("\n", "") // remove line breaks
                .replace(".", "") // remove points
                .replace(",", "") // remove comma
                .replace("-", " ") // replace dashes with spaces
                .replaceAll("[0-9]", "") // remove digits
                .replaceAll(" [a-z] ", " ") // remove single characters
                .replaceAll("( [a-z]$)|(^[a-z] )", "") // remove single characters at start or end
                .trim();
    }

    @Before
    public void createWordCountsArray() {
        wordCountsArray = new WordCountsArray(3);
    }

    @After
    public void cleanUp() {
        wordCountsArray = null;
    }

    @org.junit.Test
    public void testNegativeWordCountsArray() {
        try {
            new WordCountsArray(-1);
        } catch (NegativeArraySizeException e) {
            fail("WordCountsArray constructor allows negative sizes");
        }
    }

    @org.junit.Test
    public void testSimpleAdd() {
        wordCountsArray.add("test1", 1);
        wordCountsArray.add("test2", 2);
        wordCountsArray.add("test3", 3);

        assertEquals("Unexpected array size after 3 adds",3, wordCountsArray.size());

        for (int i = 0; i < 3; i++) {
            int num = i+1;
            assertEquals("Unexpected word at index " + i, "test" + num, wordCountsArray.getWord(i));
            assertEquals("Unexpected count at index " + i, num, wordCountsArray.getCount(i));
        }
    }

    @org.junit.Test
    public void testArrayOverlappingAdd() {
        wordCountsArray.add("test1", 1);
        wordCountsArray.add("test2", 2);
        wordCountsArray.add("test3", 3);
        wordCountsArray.add("test4", 4);
        wordCountsArray.add("test5", 5);

        assertEquals("Unexpected size after 5 adds", 5, wordCountsArray.size());

        for (int i = 0; i < 5; i++) {
            int num = i+1;
            assertEquals("Unexpected word at index " + i, "test" + num, wordCountsArray.getWord(i));
            assertEquals("Unexpected count at index " + i, num, wordCountsArray.getCount(i));
        }
    }

    @org.junit.Test
    public void testAddWithEmptyWords() {
        wordCountsArray.add(null, 23);
        wordCountsArray.add("test1", 1);
        wordCountsArray.add("", 123);
        wordCountsArray.add("test2", 2);
        wordCountsArray.add("", 12312);
        wordCountsArray.add(null, 1239123);
        wordCountsArray.add("test3", 3);

        assertEquals("Unexpected array size after 3 adds",3, wordCountsArray.size());

        for (int i = 0; i < 3; i++) {
            int num = i+1;
            assertEquals("Unexpected word at index " + i, "test" + num, wordCountsArray.getWord(i));
            assertEquals("Unexpected count at index " + i, num, wordCountsArray.getCount(i));
        }
    }

    @org.junit.Test
    public void testAddWithNegativeCounts() {
        wordCountsArray.add("test0", -19823);
        wordCountsArray.add("test1", 1);
        wordCountsArray.add("test2", 2);
        wordCountsArray.add("test3", 3);

        assertEquals("Unexpected array size after 4 adds",4, wordCountsArray.size());

        for (int i = 0; i < 3; i++) {
            assertEquals("Unexpected word at index " + i, "test" + i, wordCountsArray.getWord(i));
            assertEquals("Unexpected count at index " + i, i, wordCountsArray.getCount(i));
        }
    }

    @org.junit.Test
    public void testArraySize0() {
        assertEquals("Empty WordCountsArray size is not zero", 0, wordCountsArray.size());
    }

    @org.junit.Test
    public void testArraySizeAdded1() {
        wordCountsArray.add("Hello", 1);
        assertEquals("WordCountsArray size with one element is not one", 1, wordCountsArray.size());
    }

    @org.junit.Test
    public void testArraySizeAdded8() {
        for (int i = 0; i < 8; i++)
            wordCountsArray.add("" + i, i);

        assertEquals("WordCoutsArray size with eigth elements is not 8", 8, wordCountsArray.size());
    }

    @org.junit.Test
    public void testGetWord() {
        wordCountsArray.add("test1", 211);
        wordCountsArray.add("test4", 1231);

        assertEquals("Unexpected word found at index 0", "test1", wordCountsArray.getWord(0));
        assertEquals("Unexpected word found at index 1", "test4", wordCountsArray.getWord(1));

        assertNull("#getWord did not handle access to empty index correctly",
                wordCountsArray.getWord(2));

        try {
            assertNull("#getWord did not handle index out of bounds correctly",
                    wordCountsArray.getWord(18723));
        } catch (IndexOutOfBoundsException e) {
            fail("#getWord did not handle index out of bounds correctly");
        }

        try {
            assertNull("#getWord did not handle negative index correctly",
                    wordCountsArray.getWord(-12389));
        } catch (IndexOutOfBoundsException e) {
            fail("#getWord did not handle negative index correctly");
        }
    }

    @org.junit.Test
    public void testGetCount() {
        wordCountsArray.add("test1", 211);
        wordCountsArray.add("test4", 1231);

        assertEquals("Unexpected count found at index 0", 211, wordCountsArray.getCount(0));
        assertEquals("Unexpected count found at index 1", 1231, wordCountsArray.getCount(1));

        assertEquals("#getCount did not handle access to empty index correctly", -1,
                wordCountsArray.getCount(2));

        try {
            assertEquals("#getCount did not handle index out of bounds correctly", -1,
                    wordCountsArray.getCount(18723));
        } catch (IndexOutOfBoundsException e) {
            fail("#getCount did not handle index out of bounds correctly");
        }

        try {
            assertEquals("#getCount did not handle negative index correctly", -1,
                    wordCountsArray.getCount(-12389));
        } catch (IndexOutOfBoundsException e) {
            fail("#getCount did not handle negative index correctly");
        }
    }

    @org.junit.Test
    public void testSetCount() {
        wordCountsArray.add("test0", 212);
        wordCountsArray.add("test1", -123);

        assertEquals("#setCount did not handle negative count correctly", 0,
                wordCountsArray.getCount(1));

        try {
            wordCountsArray.setCount(2, 1239);

            assertNotEquals("#setCount did not handle access to empty index correctly", 1239,
                    wordCountsArray.getCount(2));
        } catch (Exception e) {
            fail("#setCount did not handle access to empty index correctly");
        }

        try {
            wordCountsArray.setCount(123123, 726);
        } catch (IndexOutOfBoundsException e) {
            fail("#getCount did not handle index out of bounds correctly");
        }

        try {
            wordCountsArray.setCount(-1238, 1);
        } catch (IndexOutOfBoundsException e) {
            fail("#getCount did not handle negative index correctly");
        }
    }

    private Document construcutDocumentWithDefaults(String content) {
        String title = "article";
        String language = "de";
        String summary = "tech";
        return new Document(title, language, summary, date, author, content);
    }

    @org.junit.Test
    public void testDocumentAddContentNull() {
        Document document = construcutDocumentWithDefaults(null);

        assertNull("addContent didn't handle null content correctly", document.getWordCounts());
    }

    @org.junit.Test
    public void testDocumentAddContentEmpty() {
        Document document = construcutDocumentWithDefaults("");

        assertNull("addContent didn't handle empty content String correctly", document.getWordCounts());
    }

    @org.junit.Test
    public void testDocumentAddContentCutSuffix() {
        StringBuilder builder = new StringBuilder();
        for (String s: Document.SUFFICES)
            builder.append(s).append(" ");

        builder.deleteCharAt(builder.lastIndexOf(" ")); // remove last space

        Document document = construcutDocumentWithDefaults(builder.toString());
        WordCountsArray array = document.getWordCounts();

        if (array.size() > 0) {
            StringBuilder errorBuilder = new StringBuilder("addContent didn't find/cut suffices correctly:");

            for (int i = 0; i < array.size(); i++)
                errorBuilder.append("\n- ").append(array.getWord(i));

            fail(errorBuilder.toString());
        }
    }

    @org.junit.Test
    public void testDocumentAddContentComplex() {
        Document document = construcutDocumentWithDefaults(trimmedContent);

        WordCountsArray array = document.getWordCounts();

        assertEquals("WordCountsArray has unexpected size", RESULT_CONTENT.length, array.size());

        for (int i = 0; i < array.size(); i++) {
            assertEquals("AssertionError at " + i + ": " + RESULT_CONTENT[i] + " does not match " +
                    array.getWord(i), RESULT_CONTENT[i], array.getWord(i));
        }
    }

}
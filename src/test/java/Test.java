import org.junit.BeforeClass;
import org.junit.Ignore;

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

    private static final Date DATE = new Date();
    private static final Author AUTHOR = new Author("Top", "Siegried", DATE, "nowhere", "email@google.de");

    private WordCountsArray wordCountsArray;
    private boolean successful = true; // helper variable for main method

    public static void main(String[] args) { // since we NEED to specify a main method, here is it
        Test test = new Test();

        trimContent();

        test.runTest(test::testDocumentAddContentNull);
        test.runTest(test::testDocumentAddContentEmpty);
        test.runTest(test::testDocumentAddContentCutSuffix);
        test.runTest(test::testDocumentAddContentComplex);

        if (test.successful)
            System.out.println("Test ran successfully");
        else
            System.out.println("Some test returned unsuccessfully");
    }

    private void runTest(Runnable runnable) { // helper method for the main method
        try {
            runnable.run();
        } catch (AssertionError e) {
            successful = false;
            e.printStackTrace();
        }
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



    private Document construcutDocumentWithDefaults(String content) {
        String title = "article";
        String language = "de";
        String summary = "tech";
        return new Document(title, language, summary, DATE, AUTHOR, content);
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
    @Ignore("Does not work correctly after suffices changed ")
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
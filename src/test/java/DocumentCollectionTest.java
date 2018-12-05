import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.*;

public class DocumentCollectionTest {

    private static final double DELTA = 0.000000000000001;
    private DocumentCollection collection;

    @Before
    public void before() {
        collection = new DocumentCollection();
    }

    @After
    public void cleanup() {
        collection = null;
    }

    @Test
    public void testList() {
        Document first = createDocument("testa", "tolles buch a"); // 1
        Document second = createDocument("testb", "tolles buch b"); // 0
        Document third = createDocument("testc", "tolles buch c"); // 2
        collection.appendDocument(first);
        assertEquals(collection.getLastDocument(), collection.getFirstDocument());
        collection.prependDocument(second);
        collection.appendDocument(third);

        assertEquals(3, collection.noOfDocumentsContainingWord("buch"));
        assertEquals(1, collection.noOfDocumentsContainingWord("a"));
        assertEquals(0, collection.noOfDocumentsContainingWord(""));
        assertEquals(0, collection.noOfDocumentsContainingWord("toll"));


        assertTrue("Collection is strangely empty!", !collection.isEmpty());
        assertEquals("Didn't match size", 3, collection.numDocuments());

        assertTrue("Collection didn't contain first document", collection.contains(first));
        assertTrue("Collection didn't contain second document", collection.contains(second));
        assertTrue("Collection didn't contain third document", collection.contains(third));

        assertTrue("First element did not match", second.equals(collection.getFirstDocument()));
        assertTrue("Last element did not match", third.equals(collection.getLastDocument()));

        assertTrue(second.equals(collection.get(0)));
        assertTrue(first.equals(collection.get(1)));
        assertTrue(third.equals(collection.get(2)));

        assertEquals(0, collection.indexOf(second));
        assertEquals(1, collection.indexOf(first));
        assertEquals(2, collection.indexOf(third));

        collection.removeLastDocument();
        assertEquals("Size was illegal after last document was removed", 2, collection.numDocuments());


        collection.appendDocument(third);
        collection.remove(2);
        assertEquals("Size was illegal after document with index 2 was removed", 2, collection.numDocuments());
        assertFalse(collection.contains(third));

        collection.appendDocument(third);

        assertEquals(3, collection.numDocuments());
        collection.remove(1);
        assertEquals("Size was illegal after document with index 1 was removed", 2, collection.numDocuments());
        assertFalse(collection.contains(first));
        assertTrue(collection.contains(third));
        assertTrue(collection.contains(second));


        collection.removeFirstDocument();
        collection.prependDocument(second);
        collection.prependDocument(first);


        collection.remove(0);
        assertEquals("Size was illegal after document with index 0 was removed", 2, collection.numDocuments());
        assertFalse(collection.contains(first));
        assertTrue(collection.contains(second));
        assertTrue(collection.contains(third));

        collection.removeFirstDocument();
        assertEquals(1, collection.numDocuments());
        assertEquals(third, collection.getFirstDocument());
        assertEquals(third, collection.getLastDocument());

        collection.removeLastDocument();
        assertEquals(0, collection.numDocuments());
        assertNull(collection.getFirstDocument());
        assertNull(collection.getLastDocument());


        collection.appendDocument(third);

        collection.remove(0);
        assertEquals(0, collection.numDocuments());
        assertNull(collection.getFirstDocument());
        assertNull(collection.getLastDocument());
    }

    @Test
    @Ignore("underlying implementation changed completely")
    public void testQuery() {
        Document first = createDocument("Der Wolf und die 7 Geisslein",
                "es war einmal eine alte geiss die hatte sieben junge geisslein " +
                        "und hatte sie lieb wie eine mutter ihre kinder lieb hat");
        Document second = createDocument("Tischlein deck dich",
                "vor zeiten war ein schneider der drei söhne hatte und nur eine einzige ziege");
        Document third = createDocument("Hans im Glueck",
                "hans hatte sieben jahre bei seinem herrn gedient da sprach er zu " +
                        "ihm herr meine zeit ist herum, nun wollte ich gerne wieder heim zu " +
                        "meiner mutter gebt mir meinen lohn");

        collection.appendDocument(first);
        assertEquals(1, collection.numDocuments());
        assertEquals(first, collection.get(0));

        collection.appendDocument(second);
        assertEquals(2, collection.numDocuments());
        assertEquals(second, collection.get(1));

        assertEquals(2, count(0, "geiss"));
        assertEquals(0, count(1, "geiss"));

        collection.match("ziege");
        // TODO update
        assertEquals(0.2672612419124244, collection.getQuerySimilarity(0), DELTA);
        assertEquals(0.0, collection.getQuerySimilarity(1), DELTA);

        collection.appendDocument(third);

        assertEquals(0, count(0, "hans"));
        assertEquals(0, count(1, "hans"));
        assertEquals(1, count(2, "hans"));

        collection.match("hans");
        assertEquals(0.17407765595569785, collection.getQuerySimilarity(0), DELTA);
        assertEquals(0.0, collection.getQuerySimilarity(1), DELTA);
        assertEquals(0.0, collection.getQuerySimilarity(2), DELTA);

        collection.match("hatte");
        assertEquals(0.3651483716701107, collection.getQuerySimilarity(0), DELTA);
        assertEquals(0.2672612419124244, collection.getQuerySimilarity(1), DELTA);
        assertEquals(0.17407765595569785, collection.getQuerySimilarity(2), DELTA);
    }

    private int count(int index, String word) {
        WordCountsArray wordCounts = collection.get(index).getWordCounts();
        int wordIndex = wordCounts.getIndexOfWord(word);
        int count = wordCounts.getCount(wordIndex);

        return count > 0? count: 0;
    }

    private static Document createDocument(String titel, String content) {
        return new Document(titel, "de", "test", new Date(), null, content);
    }

    @Test
    public void testCrawl0() throws IOException {
        this.crawl("B", "link:A link:E", new FileContent[] {
                new FileContent("A", "link:B link:C"), new FileContent("C", "link:D"),
                new FileContent("D", "link:C"), new FileContent("E", "link:B")
        }, new String[] {"B", "A", "C", "D", "E"});
    }

    @Test
    public void testCrawl1() throws IOException {
        this.crawl("B", "link:C", new FileContent[] {
                new FileContent("A", "link:B"), new FileContent("C", "link:D"),
                new FileContent("D", "link:E"), new FileContent("E", "link:A")
        }, new String[] {"B", "C", "D", "E", "A"});
    }

    @Test
    public void testCrawl2() throws IOException {
        this.crawl("B", "link:C", new FileContent[] {
                new FileContent("A", "link:B"), new FileContent("C", "link:A link:D"),
                new FileContent("D", "link:B link:C"), new FileContent("E", "link:A")
        }, new String[] {"B", "C", "A", "D"});
    }

    private void crawl(String title, String content, FileContent[] contents, String[] expected) throws IOException {
        try {
            createFiles(contents);

            collection = new LinkedDocumentCollection();

            Document b = new LinkedDocument(title, "de", "", null, null, content, title);
            collection.appendDocument(b);

            assertEquals(1, collection.numDocuments());
            assertEquals(b, collection.getFirstDocument());

            LinkedDocumentCollection result = ((LinkedDocumentCollection) collection).crawl();
            assertEquals("Unexpected size of collection after crawl", expected.length, result.numDocuments());

            int index = 0;
            for (Document document: result)
                assertEquals("Unexpected title for index " + index, expected[index++], document.getTitle());
        } finally {
            deleteFiles(contents);
        }
    }

    private static void createFiles(FileContent... contents) throws IOException {
        for (FileContent content: contents) {
            File file = new File(content.getTitle());
            if (file.exists())
                if (!file.delete())
                    throw new IOException("Couldn't delete file " + content.getTitle());

            if (!file.createNewFile())
                throw new IOException("Couldn't create file " + content.getTitle());

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(content.getTitle());
                writer.newLine();
                writer.write(content.getContent());
                writer.newLine();

                writer.flush();
            }
        }
    }

    private static void deleteFiles(FileContent... files) {
        for (FileContent f: files) {
            File file = new File(f.getTitle());

            if (file.exists())
                if (!file.delete())
                    System.err.println("Couldn't delete file: " + f.getTitle());
        }
    }

    private static class FileContent {

        private final String title;
        private final String content;

        public FileContent(String title, String content) {
            this.title = title;
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

    }

}
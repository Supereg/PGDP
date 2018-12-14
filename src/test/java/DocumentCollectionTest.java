import org.junit.After;
import org.junit.Before;
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

    @Test
    public void testPageRank0() throws IOException {
        this.testPageRank(new FileContent[] {
                new FileContent("A", "link:B link:C"), new FileContent("B", "link:A link:C link:D"),
                new FileContent("C", "link:D"), new FileContent("D", "link:C")
        }, 0, new double[] {
                0.0547,
                0.0607,
                0.4485,
                0.4359
        }, 0.0001);
    }

    @Test
    public void testPageRank1() throws IOException {
        this.testPageRank(new FileContent[] {
                new FileContent("a", "es war einmal link:b link:c"),
                new FileContent("b", "link:a link:e"),
                new FileContent("c", "once upon a time link:d"),
                new FileContent("d", "erase una vez link:c"),
                new FileContent("e", "c era una volta link:b")
        }, 1, new double[] {
                0.14897680763983684,
                0.0933151432469308,
                0.34291508382082525,
                0.32147782204547976,
                0.0933151432469308
        }, 0.000001);
    }

    @Test
    public void testPageRank2() throws IOException {
        this.testPageRank(new FileContent[] {
                new FileContent("A", "hallo link:B"),
                new FileContent("B", "mein link:D"),
                new FileContent("C", "freund link:E"),
                new FileContent("D", "wie link:C"),
                new FileContent("E", "gehts link:A")
        }, 1, new double[] {
                0.1999999605698945,
                0.19999992446430387,
                0.19999993579465827,
                0.1999999454254595,
                0.19999995361164058
        }, 0.000001);
    }

    @Test
    public void testPageRank3() throws IOException {
        this.testPageRank(new FileContent[] {
                new FileContent("A", "link:C link:E"),
                new FileContent("B", "link:A link:C link:E"),
                new FileContent("C", "link:E"),
                new FileContent("D", "link:D link:E"),
                new FileContent("E", "link:D")
        }, 1, new double[] {
                0.030000000000000006, // B
                0.038500000000000006, // A
                0.05486250000000001, // C
                0.4576418518588728, // E
                0.4189955610104386 // D
        }, 0.000001);
    }

    @Test
    public void testPageRank4() throws IOException {
        this.testPageRank(new FileContent[] {
                new FileContent("A", "link:B link:C link:D link:E"),
                new FileContent("B", "link:A link:C link:D link:E"),
                new FileContent("C", "link:A link:B link:D link:E"),
                new FileContent("D", "link:A link:B link:C link:E"),
                new FileContent("E", "link:A link:B link:C link:D")
        }, 0, new double[] {
                0.19999988747982295, // A
                0.19999988747982295, // B
                0.19999988873777422, // C
                0.1999998894612573, // D
                0.19999988977428843 // E
        }, 0.000001);
    }

    private void testPageRank(FileContent[] fileContents, int add, double[] expectedPageRank, double delta) throws IOException {
        try {
            createFiles(fileContents);

            LinkedDocumentCollection collection = new LinkedDocumentCollection();

            LinkedDocument document = createLinkedDocument(fileContents[add]);
            collection.appendDocument(document);

            collection = collection.crawl();

            assertEquals(fileContents.length, collection.numDocuments());

            double[] pageRank = collection.pageRankRec(0.85);

            for (int i = 0; i < 4; i++) {
                assertEquals("Did not math for " + i, expectedPageRank[i], pageRank[i], delta);
            }
        } finally {
            deleteFiles(fileContents);
        }
    }

    @Test
    public void testRelevance0() throws IOException {
        testRelevance(new FileContent[] {
                new FileContent("a.txt", "es war einmal link:b.txt link:c.txt"),
                new FileContent("b.txt", "link:a.txt link:e.txt"),
                new FileContent("c.txt", "once upon a time link:d.txt"),
                new FileContent("d.txt", "erase una vez link:c.txt"),
                new FileContent("e.txt", "c era una volta link:b.txt")
        }, 1, new double[] {
                0.285917709527423,
                0.1371660335283301,
                0.1285911288181919,
                0.05959072305593474,
                0.03732605729877232
        }, "einmal");
    }

    @Test
    public void testRelevance1() throws IOException {
        testRelevance(new FileContent[] {
                new FileContent("A", "hallo link:B"),
                new FileContent("B", "mein link:D"),
                new FileContent("C", "freund link:E"),
                new FileContent("D", "wie link:C"),
                new FileContent("E", "gehts link:A")
        }, 0, new double[] {
                0.6799999697857215,
                0.07999998144465624,
                0.07999997817018381,
                0.07999997431786332,
                0.07999996978572155
        }, "mein");
    }

    @Test
    public void testRelevance2() throws IOException {
        testRelevance(new FileContent[] {
                new FileContent("A", "asd asd asd link:C link:E"),
                new FileContent("B", "asd asd asd asd link:A link:C link:E"),
                new FileContent("C", "asd link:E"),
                new FileContent("D", "asd link:D link:E"),
                new FileContent("E", "asd asd link:D")
        }, 1, new double[] {
                0.7830567407435491,
                0.7675982244041755,
                0.621945,
                0.6154,
                0.612
        }, "asd");
    }

    public void testRelevance(FileContent[] fileContents, int add, double[] expectedRelevance, String query) throws IOException {
        try {
            createFiles(fileContents);

            LinkedDocumentCollection collection = new LinkedDocumentCollection();
            LinkedDocument document = createLinkedDocument(fileContents[add]);
            collection.appendDocument(document);

            collection = collection.crawl();
            assertEquals("Unexpected size after crawling", expectedRelevance.length, collection.numDocuments());

            collection.match(query);

            for (int i = 0; i < expectedRelevance.length; i++)
                assertEquals("Unexpected relevance for " + i, expectedRelevance[i], collection.getRelevance(i), 0.000001);
        } finally {
            deleteFiles(fileContents);
        }
    }

    private static LinkedDocument createLinkedDocument(FileContent fileContent) {
        return createLinkedDocument(fileContent.getTitle(), fileContent.getContent());
    }

    private static LinkedDocument createLinkedDocument(String titel, String content) {
        return new LinkedDocument(titel, "de", "test", new Date(), null, content, titel);
    }

    public static void createFiles(FileContent... contents) throws IOException {
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

    public static void deleteFiles(FileContent... files) {
        for (FileContent f: files) {
            File file = new File(f.getTitle());

            if (file.exists())
                if (!file.delete())
                    System.err.println("Couldn't delete file: " + f.getTitle());
        }
    }

    public static class FileContent {

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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkedDocument extends Document {

    private static final Pattern LINK_PATTERN = Pattern.compile("link:([a-z]|[A-Z])+");

    private final String ID;
    private String[] outgoingIDs;

    private LinkedDocumentCollection incomingLinks;
    private LinkedDocumentCollection outgoingLinks;

    /**
     * Constructs a document with the given values.
     *
     * @param title       the document's title
     * @param language    the language the document is written in
     * @param summary     short summary of the document
     * @param releaseDate the release date of the document
     * @param author      the author of the document
     * @param content
     */
    public LinkedDocument(String title, String language, String summary, Date releaseDate, Author author, String content, String ID) {
        super(title, language, summary, releaseDate, author, content);
        this.ID = ID;
        this.outgoingIDs = findOutgoingIDs(content);

        this.incomingLinks = new LinkedDocumentCollection();

        this.setLinkCountZero();
    }

    public String getID() {
        return ID;
    }

    public LinkedDocumentCollection getIncomingLinks() {
        return incomingLinks;
    }

    public LinkedDocumentCollection getOutgoingLinks() {
        if (outgoingLinks == null) {
            outgoingLinks = new LinkedDocumentCollection();
            createOutgoingDocumentCollection();
        }

        return outgoingLinks;
    }

    public void addIncomingLink(LinkedDocument incomingLink) {
        if (incomingLink == null || this.equals(incomingLink))
            return;

        incomingLinks.appendDocument(incomingLink);
    }

    private String[] findOutgoingIDs(String text) {
        if (text == null || text.isEmpty())
            return new String[0];

        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ':')
                count++;
        }

        String[] result = new String[count];
        int insertIndex = 0;

        Matcher matcher = LINK_PATTERN.matcher(text);
        while (matcher.find()) {
            String part = matcher.group().replace("link:", "");
            result[insertIndex++] = part;
        }

        return result;
    }

    private void setLinkCountZero() {
        WordCountsArray array = this.getWordCounts();

        int size = array.size();
        for (int i = 0; i < size; i++) {
            String word = array.getWord(i);

            if (word.startsWith("link:"))
                array.setCount(i, 0);
        }
    }

    private void createOutgoingDocumentCollection() {
        for (String outgoingID: outgoingIDs) {
            if (outgoingID.equals(ID))
                continue;

            LinkedDocument document = createLinkedDocumentFromFile(outgoingID);
            // document can be null, however null values are handled by #appendDocument
            outgoingLinks.appendDocument(document);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o instanceof  LinkedDocument)
            return Objects.equals(ID, ((LinkedDocument) o).ID);
        else if (o instanceof Document)
            return super.equals((Document) o);

        return false;
    }

    public boolean equals(Document document) {
        return this.equals((Object) document);
    }

    public static LinkedDocument createLinkedDocumentFromFile(String fileName) {
        String[] lines = Terminal.readFile(fileName);

        if (lines == null || lines.length < 2) // shouldn't it rather be lines.length != 2? instructions states however length < 2
            return null;

        return new LinkedDocument(lines[0], "de", "file", new Date(), null, lines[1], fileName);
    }

}
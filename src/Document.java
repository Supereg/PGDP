/**
 * The class {@code Document} represents a document.
 * 
 * @author Florian Kelbert
 *
 */
public class Document {

    private String title;

    private String content;
    private String summary;

    private String language;

    private Author author;
    private Date releaseDate;

    public Document(String title, String language, String summary, Date releaseDate, Author author, String content) {
        this.title = title;

        this.content = content;
        this.summary = summary;

        this.language = language;

        this.author = author;
        this.releaseDate = releaseDate;
    }


    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getSummary() {
        return summary;
    }

    public String getLanguage() {
        return language;
    }

    public Author getAuthor() {
        return author;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getAgeAt(Date today) {
        return releaseDate.getAgeInDaysAt(today);
    }

    @Override
    public String toString() {
        return "Document{" +
                "title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", language='" + language + '\'' +
                ", author=" + author +
                ", releaseDate=" + releaseDate +
                '}';
    }

}

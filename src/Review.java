/**
 * The class {@code Review} represents a review of a {@see Document}.
 * 
 * @author Florian Kelbert, Andreas Bauer
 *
 */
public class Review {

    private String content;

    private String language;
    private int rating;

    private Date releaseDate;

    private Author author;
    private Document reviewedDocument;

    /**
     * Instantiates a new Review object.
     * If {@code rating} is not a valid value (not in range of 0 to 10) the default value '0' is used
     *
     * @param author            the author of the review
     * @param reviewedDocument  the document the review is about
     * @param language          language review is written in
     * @param releaseDate       release date of the review
     * @param rating            rating of the review. Only values from 0 to 10 are allowed!
     * @param content           the content of the review
     */
    public Review(Author author, Document reviewedDocument, String language, Date releaseDate,
                  int rating, String content) {
        this.content = content;

        this.language = language;
        this.setRating(rating); // calling setter will ensure that rating has correct value

        this.releaseDate = releaseDate;

        this.author = author;
        this.reviewedDocument = reviewedDocument;
    }

    public String getContent() {
        return content;
    }

    public String getLanguage() {
        return language;
    }

    public int getRating() {
        return rating;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public Author getAuthor() {
        return author;
    }

    public Document getReviewedDocument() {
        return reviewedDocument;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Sets the rating for the review.
     * If the rating is not in the range of 0 to 10 no change is done.
     *
     * @param rating    rating of the review. Only values from 0 to 10 are allowed!
     */
    public void setRating(int rating) {
        if (rating < 0 || rating > 10)
            return; //throw new IllegalArgumentException("'rating' must be between 0 and 10");

        this.rating = rating;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setReviewedDocument(Document reviewedDocument) {
        this.reviewedDocument = reviewedDocument;
    }

    public int getAgeAt(Date today) {
        return releaseDate.getAgeInDaysAt(today);
    }

    @Override
    public String toString() {
        return "Review{" +
                "language='" + language + '\'' +
                ", rating=" + rating +
                ", releaseDate=" + releaseDate +
                ", author=" + author +
                ", reviewedDocument=" + reviewedDocument +
                '}';
    }

}

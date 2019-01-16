package suchmaschine;

import java.util.Objects;

/**
 * The class {@code Review} represents a review of a {@link Document}.
 *
 * @see Document
 * @see Author
 * @see Date
 *
 *
 */
public class Review {

    /**
     * the maximum possible rating
     */
    public static final int MAX_RATING = 10;

    /**
     * the minimum possible rating
     */
    public static final int MIN_RATING = 0;

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
        this.setContent(content);
        this.setLanguage(language);
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

    /**
     * Sets the content of this review.
     *
     * If the specified content is <code>null</code>, then the content is set to
     * an empty {@link String}.
     *
     * @param content the new content
     */
    public void setContent(String content) {
        if (content == null) {
            this.content = "";
        } else {
            this.content = content;
        }
    }

    /**
     * Sets the language of this review.
     *
     * If the specified language is <code>null</code>, then the language is set to
     * an empty {@link String}.
     *
     * @param language the new language
     */
    public void setLanguage(String language) {
        if (language == null) {
            this.language = "";
        } else {
            this.language = language;
        }
    }

    /**
     * Sets the rating of this review.
     *
     * If the specified rating is lower than {@link Review#MIN_RATING}, then the
     * rating is set to {@link Review#MIN_RATING}. If the specified rating is
     * greater than {@link Review#MAX_RATING}, then the rating is set to
     * {@link Review#MAX_RATING}.
     *
     * @param rating the new rating
     */
    public void setRating(int rating) {
        if (rating < Review.MIN_RATING) {
            this.rating = Review.MIN_RATING;
        } else if (rating > Review.MAX_RATING) {
            this.rating = Review.MAX_RATING;
        } else {
            this.rating = rating;
        }
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

    public boolean equals(Review review) {
        return this.equals((Object) review);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return rating == review.rating &&
                Objects.equals(content, review.content) &&
                Objects.equals(language, review.language) &&
                Objects.equals(releaseDate, review.releaseDate) &&
                Objects.equals(author, review.author) &&
                Objects.equals(reviewedDocument, review.reviewedDocument);
    }

    @Override
    public String toString() {
        return "Review{" +
                ", rating=" + rating +
                ", releaseDate=" + releaseDate +
                ", author=" + author +
                ", reviewedDocument=" + reviewedDocument +
                '}';
    }

}

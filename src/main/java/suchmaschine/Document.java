package suchmaschine;

import java.util.Objects;

/**
 * The class {@code Document} represents a document.
 *
 * This class ensures that neither the title nor the language nor the
 * summary of the document is <code>null</code>.
 *
 * @see Date
 * @see Author *
 */
public class Document {

    public static final String[] SUFFICES = {
            "ab", "al", "ant", "artig", "bar", "chen", "ei", "eln", "en", "end", "ent", "er", "fach", "fikation",
            "fizieren", "faehig", "gemaeß", "gerecht", "haft", "haltig", "heit", "ie", "ieren", "ig", "in", "ion",
            "iren", "isch", "isieren", "isierung", "ismus", "ist", "itaet", "iv", "keit", "kunde", "legen", "lein",
            "lich", "ling", "logie", "los", "mal", "meter", "mut", "nis", "or", "sam", "schaft", "tum", "ung", "voll",
            "wert", "wuerdig"
    };

    /*
    Letzter Test der HA4 wollte SUFFICES mit Umlaute, der von HA5 nicht? Was ist nun richtig/gewollt? Hört auf meine
    Tests zu zerstören plzz :)
    public static final String[] SUFFICES = {
            "ab", "al", "ant", "artig", "bar", "chen", "ei", "eln", "en", "end", "ent", "er", "fach", "fikation",
            "fizieren", "fähig", "gemäß", "gerecht", "haft", "haltig", "heit", "ie", "ieren", "ig", "in", "ion",
            "iren", "isch", "isieren", "isierung", "ismus", "ist", "ität", "iv", "keit", "kunde", "legen", "lein",
            "lich", "ling", "logie", "los", "mal", "meter", "mut", "nis", "or", "sam", "schaft", "tum", "ung", "voll",
            "wert", "würdig"
    };
    */

    private String title;

    private String summary;
    private WordCountsArray wordCounts;

    private String language;

    private Author author;
    private Date releaseDate;

    /**
     * Constructs a document with the given values.
     *
     * @param title       the document's title
     * @param language    the language the document is written in
     * @param summary     short summary of the document
     * @param releaseDate the release date of the document
     * @param author      the author of the document
     */
    public Document(String title, String language, String summary, Date releaseDate, Author author, String content) {
        this.setTitle(title);
        this.setLanguage(language);
        this.setSummary(summary);

        this.author = author;
        this.releaseDate = releaseDate;

        this.addContent(content);
    }


    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    /**
     * Returns the {@link WordCountsArray} object of this object
     *
     * @return  the {@link WordCountsArray} if it exists.
     *          {@code null} when no valid content was added yet!
     */
    public WordCountsArray getWordCounts() {
        return wordCounts;
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

    /**
     * Sets the title of the document.
     *
     * If the new title is <code>null</code>, then the title is set to an empty
     * {@link String}.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        if (title == null) {
            this.title = "";
        } else {
            this.title = title;
        }
    }

    /**
     * Parses the {@code content} for words to add.
     *
     * If the {@code content} is {@code null} or empty nothing will be done
     *
     * @param content   a {@link String} with the content to scan
     */
    private void addContent(String content) {
        if (content == null || content.isEmpty()) {
            if (this.wordCounts == null)
                this.wordCounts = new WordCountsArray(0);

            return;
        }

        String[] contentParts = tokenize(content);

        if (this.wordCounts == null) {
            // we use contentParts.length as initial size so we do not have unnecessary array copies in the constructor
            // wordCounts is not necessarily completely filled since some empty words
            //      (after removing suffix) are left out
            this.wordCounts = new WordCountsArray(contentParts.length);
        }

        for (String part: contentParts) {
            String suffix = findSuffix(part);

            if (!suffix.isEmpty())
                part = cutSuffix(part, suffix);

            this.wordCounts.add(part, 1); // #add ignores empty words
        }
    }

    /**
     * Sets the language of the document.
     *
     * If the new language is <code>null</code>, then the language is set to an
     * empty {@link String}.
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
     * Sets the summary of the document.
     *
     * If the new summary is <code>null</code>, then the summary is set to
     * an empty {@link String}.
     *
     * @param summary the new summary
     */
    public void setSummary(String summary) {
        if (summary == null) {
            this.summary = "";
        } else {
            this.summary = summary;
        }
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

    public boolean equals(Document document) {
        return this.equals((Object) document);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(title, document.title) &&
                Objects.equals(summary, document.summary) &&
                Objects.equals(wordCounts, document.wordCounts) &&
                Objects.equals(language, document.language) &&
                Objects.equals(author, document.author) &&
                Objects.equals(releaseDate, document.releaseDate);
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

    private static String[] tokenize(String content) {
        // assumptions as of the exercise: content has only lower case letters and single spaces

        int wordCount = 1; // starting with one since there is one word more than spaces
        /* count spaces in the content */ // copied from solution, in order to replace Stringulina dependency
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == ' ') {
                wordCount++;
            }
        }

        String[] tokenArray = new String[wordCount];
        int nextIndex = 0;

        // we append a space so the last word is also copied to the tokenArray and we do not need to make
        // some special case for it
        content += " ";

        String currentWord = "";
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (c == ' ') {
                tokenArray[nextIndex++] = currentWord;
                currentWord = "";
            }
            else {
                // string concatenation in loop is still bad. Are we still not allowed to use StringBuilder?
                //noinspection StringConcatenationInLoop
                currentWord += c;
            }
        }

        return tokenArray;
    }

    /**
     * Checks if the last {@code n} characters of {@code w1} and {@code w2} are equal
     *
     * @param w1  the first {@code String}
     * @param w2  the second {@code Srting}
     * @param n   an {@code int}
     * @return    {@code true} if the last {@code n} characters of the Strings match.
     *            If {@code n} is bigger than the length of one of the strings {@code false} is returned
     */
    private static boolean sufficesEqual(String w1, String w2, int n) {
        if (n > w1.length() || n > w2.length())
            return false;

        int index1 = w1.length() - n;
        int index2 = w2.length() - n;

        boolean matchesSuffix = true;
        for (; index1 < w1.length() && index2 < w2.length(); index1++, index2++) {
            char c1 = w1.charAt(index1);
            char c2 = w2.charAt(index2);

            if (c1 != c2) {
                matchesSuffix = false;
                break;
            }
        }

        if (matchesSuffix) // ensure both indices are "finished"
            matchesSuffix = index1 == w1.length() && index2 == w2.length();

        return matchesSuffix;
    }

    private static String findSuffix(String word) {
        String longestSuffix = ""; // we need to find the longest one,
                                   //   since the suffix 'haltig' has also the suffix 'ig'

        for (String suffix: SUFFICES) {
            if (sufficesEqual(word, suffix, suffix.length())
                    && suffix.length() > longestSuffix.length())
                longestSuffix = suffix;
        }

        return longestSuffix;
    }

    private static String cutSuffix(String word, String suffix) {
        if (!sufficesEqual(word, suffix, suffix.length()))
            return word;
        // above we ensured that the word ends with the suffix

        String newWord = "";

        int loopEnd = word.length() - suffix.length();
        for (int i = 0; i < loopEnd; i++) {
            //noinspection StringConcatenationInLoop
            newWord += word.charAt(i);
        }

        return newWord;
    }

}
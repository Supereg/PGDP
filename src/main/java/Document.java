/**
 * The class {@code Document} represents a document.
 * 
 * @author Florian Kelbert
 *
 */
public class Document {

    public static final String[] SUFFICES = {
            "ab", "al", "ant", "artig", "bar", "chen", "ei", "eln", "en", "end", "ent", "er", "fach", "fikation",
            "fizieren", "fähig", "gemäß", "gerecht", "haft", "haltig", "heit", "ie", "ieren", "ig", "in", "ion",
            "iren", "isch", "isieren", "isierung", "isierung", "ist", "ität", "iv", "keit", "kunde", "legen", "legen",
            "lich", "ling", "logie", "los", "mal", "meter", "mut", "nis", "or", "sam", "schaft", "tum", "ung", "voll",
            "wert", "würdig"
    };

    private String title;

    private String summary;
    private WordCountsArray wordCounts;

    private String language;

    private Author author;
    private Date releaseDate;

    public Document(String title, String language, String summary, Date releaseDate, Author author, String content) {
        this.title = title;

        this.summary = summary;

        this.language = language;

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

    public void setTitle(String title) {
        this.title = title;
    }

    private void addContent(String content) {
        String[] contentParts = tokenize(content);

        for (String part: contentParts) {
            String suffix = findSuffix(part);

            if (!suffix.isEmpty())
                part = cutSuffix(part, suffix);

            // TODO WordCountsArray does currently NOT handle word which are added a second time
            // TODO exercise says we will implement this in a further chapter
            // TODO as a result i won't handle that right here ?!?
            this.wordCounts.add(part, 1); // #add ignores empty words
        }
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

    private static String[] tokenize(String content) {
        // assumptions as of the exercise: content has only lower case letters and single spaces
        int wordCount = Stringulina.countSubstring(content, " ") + 1;

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
                //System.out.println(c1 + " " +c2 + " do not match");
                break;
            }
        }

        if (matchesSuffix) {
            matchesSuffix = index1 == w1.length() && index2 == w2.length();
            // if (!matchesSuffix)
            //    System.out.println("One of the indices did not finish (" + index1 + "," + index2 + ")");
        }

        return matchesSuffix;
    }

    private static String findSuffix(String word) {
        for (String suffix: SUFFICES) {
            if (sufficesEqual(word, suffix, suffix.length()))
                return suffix;
        }

        return "";
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
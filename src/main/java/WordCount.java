import java.util.Objects;

public class WordCount {

    private String word;
    private int count;

    /**
     * Creates an instance of this class.
     *
     * This constructor calls {@link WordCount#WordCount(String, int)} with the
     * parameters set to <code>word</code> and <code>0</code>.
     *
     * @param word the represented word
     */
    public WordCount(String word) {
        this(word, 0);
    }

    /**
     * Creates an instance of this class representing the specified
     * <code>word</code> with its count set to <code>count</code>.
     *
     * If the specified word is <code>null</code>, then the word is set to an empty
     * {@link String}. If the specified count is lower than <code>0</code>, then the
     * count is set according to {@link WordCount#setCount(int)}.
     *
     * @param word  the represented word
     * @param count the count of <code>word</code>
     */
    public WordCount(String word, int count) {
        this.word = word != null? word: "";
        this.setCount(count);
    }

    public String getWord() {
        return word;
    }

    public int getCount() {
        return count;
    }

    /**
     * Sets the count of the represented word.
     *
     * If the specified count is lower than <code>0</code>, then the count is set to
     * <code>0</code>.
     *
     * @param count the new count
     */
    public void setCount(int count) {
        if (count < 0) {
            this.count = 0;
        } else {
            this.count = count;
        }
    }

    public int incrementCount() {
        return ++count;
    }

    public int incrementCount(int n) {
        if (n <= 0)
            return count;

        count += n;
        return count;
    }

    public boolean equals(WordCount wordCount) {
        if (this == wordCount) return true;
        return count == wordCount.count &&
                Objects.equals(word, wordCount.word);
    }

}
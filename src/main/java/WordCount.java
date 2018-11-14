public class WordCount {

    private String word;
    private int count;

    /**
     * Instantiates a new WordCount object
     *
     * @param word  a {@code String} with the word
     * @param count an non negative {@code int}. If {@code count} is negative the default value {@code 0} is used
     */
    public WordCount(String word, int count) {
        this.word = word;
        this.setCount(count);
    }

    public String getWord() {
        return word;
    }

    public int getCount() {
        return count;
    }

    /**
     * Sets the count for the WordCount.
     * If {@code count} is negative no change is done
     *
     * @param count an {@code int} representing the word count
     */
    public void setCount(int count) {
        if (count < 0)
            return; //throw new IllegalArgumentException("'count' cannot be negative");

        this.count = count;
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

}
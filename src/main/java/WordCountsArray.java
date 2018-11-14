public class WordCountsArray {

    private WordCount[] wordCountsArray;
    private int nextFreeIndex;

    /**
     * Constructs a WordCountsArray object
     *
     * If @{code initSize} is negative a default size of {@code 0} will be used
     *
     * @param initSize  an @{code int} with the initial array size
     */
    public WordCountsArray(int initSize) {
        if (initSize < 0)
            initSize = 0;

        this.wordCountsArray = new WordCount[initSize];
    }

    /**
     * @return  the current size of the {@link WordCountsArray} object
     */
    public int size() {
        return this.nextFreeIndex;
    }

    /**
     * Adds a {@code word} with the given {@code count} to the WordCountsArray.
     *
     * If {@code word} is {@code null} or empty nothing will be done.
     * If {@code count} is not positive the value {@code 0} will be used
     *
     * @param word  a {@link String} with the word to add
     * @param count an {@code int} representing the count of the {@code word}
     */
    public void add(String word, int count) {
        if (word == null || word.isEmpty())
            return;

        WordCount wordCount = new WordCount(word, count); // illegal count is handled by the setter

        increaseArraySizeIfNecessary();

        wordCountsArray[this.nextFreeIndex] = wordCount;
        this.nextFreeIndex++;
    }

    private void increaseArraySizeIfNecessary() {
        if (nextFreeIndex < wordCountsArray.length)
            return;

        WordCount[] oldArray = this.wordCountsArray;
        this.wordCountsArray = new WordCount[oldArray.length * 2]; // doubling the array size

        // copy old content to new array
        System.arraycopy(oldArray, 0, this.wordCountsArray, 0, oldArray.length);
    }

    private WordCount getWordCount(int index) {
        if (index < 0 || index >= wordCountsArray.length)
            return null;

        return wordCountsArray[index];
    }

    /**
     * Returns the word at the specified {@code index}
     *
     * @param index the index to look at
     * @return      the word at the position {@code index}.
     *              If the {@code index} is out of bounds {@code null} is returned.
     *              If no word exists for the given {@code index} {@code null} is returned.
     */
    public String getWord(int index) {
        WordCount wordCount = this.getWordCount(index);
        return wordCount != null? wordCount.getWord(): null;
    }

    /**
     * Returns the count at the specified {@code index}
     *
     * @param index the index to look at
     * @return      the count at the position {@code index}.
     *              If the {@code index} is out of bounds {@code -1} is returned.
     *              If no count exists for the given {@code index} {@code -1} is returned.
     */
    public int getCount(int index) {
        WordCount wordCount = this.getWordCount(index);
        return wordCount != null? wordCount.getCount(): -1;
    }

    /**
     * Sets the count for the word at the given {@code index}
     *
     * If {@code index} is out of bounds or there is no word at the give {@code index} nothing will be done.
     * If {@code count} is negative the default count of {@code 0} will be used.
     *
     * @param index index to set count at
     * @param count new count to be set to
     */
    public void setCount(int index, int count) {
        WordCount wordCount = this.getWordCount(index);

        if (wordCount != null) { // TODO specify
            wordCount.setCount(count); // illegal count is handled by the setter // TODO specify
        }
    }

}
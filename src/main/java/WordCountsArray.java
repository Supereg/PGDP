import java.util.Arrays;

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
     * If {@code word} is {@code null} or empty or {@code count} is not positive nothing will be done.
     *
     * @param word  a {@link String} with the word to add
     * @param count an {@code int} representing the count of the {@code word}
     */
    public void add(String word, int count) {
        if (word == null || word.isEmpty()|| count < 0)
            return;
        word = word.toLowerCase(); // ensure lower casing

        // check if the word already exists
        int index;
        if ((index = this.getIndexOfWord(word)) > -1) {
            int newCount = this.getCount(index) + count;
            this.setCount(index, newCount);
        }
        else { // insert new word
            WordCount wordCount = new WordCount(word, count); // illegal count is handled by the setter

            increaseArraySizeIfNecessary();

            wordCountsArray[this.nextFreeIndex] = wordCount;
            this.nextFreeIndex++;
        }
    }

    private void increaseArraySizeIfNecessary() {
        if (nextFreeIndex < wordCountsArray.length)
            return;

        WordCount[] oldArray = this.wordCountsArray;
        this.wordCountsArray = new WordCount[Math.max(1, oldArray.length) * 2]; // doubling the array size

        // copy old content to new array
        System.arraycopy(oldArray, 0, this.wordCountsArray, 0, oldArray.length);
    }

    public void sort() {
        this.doBubbleSort();
    }

    private void doBubbleSort() {
        int insertIndex = 0;
        int indexWithMinimalElement;

        while (insertIndex < nextFreeIndex) {
            indexWithMinimalElement = insertIndex;
            for (int i = insertIndex; i < nextFreeIndex; i++) {
                String word0 = wordCountsArray[i].getWord();
                String word1 = wordCountsArray[indexWithMinimalElement].getWord();

                if (word0.compareTo(word1) <= 0)
                    indexWithMinimalElement = i;
            }

            // swap elements
            WordCount temp = wordCountsArray[insertIndex];
            wordCountsArray[insertIndex] = wordCountsArray[indexWithMinimalElement];
            wordCountsArray[indexWithMinimalElement] = temp;

            insertIndex++;
        }
    }

    private void doBucketSort() {
        if (size() <= 1)
            return;

        WordCount[] result = new WordCount[size()];
        bucketPart(wordCountsArray, size(), 0, result, new int[] {0});

        wordCountsArray = result;

        for (int i = 0; i < nextFreeIndex; i++) {
            System.out.println(getWord(i));
        }
    }

    private void bucketPart(WordCount[] in, int inLength, int character, WordCount[] result, int[] resultInsertIndex) {
        WordCount[][] buckets = new WordCount[26][Math.max(1, size() / 26)]; // a-z represented by index from 0-26
        int[] freeIndices = new int[26];

        for (int i = 0; i < inLength; i++) {
            WordCount wordCount = in[i];
            String word = wordCount.getWord();

            if (character >= word.length()) {
                result[resultInsertIndex[0]++] = wordCount;
                continue;
            }

            int index = word.charAt(character) - 'a';

            WordCount[] bucket = buckets[index];
            int freeIndex = freeIndices[index];

            if (freeIndex >= bucket.length) { // increase size of bucket if needed
                WordCount[] newBucket = new WordCount[2 * bucket.length];
                System.arraycopy(bucket, 0, newBucket, 0, bucket.length);

                buckets[index] = bucket = newBucket;
            }

            bucket[freeIndex] = wordCount;
            freeIndices[index]++;
        }

        character++;

        for (int alphabet = 0; alphabet < 26; alphabet++) {
            WordCount[] bucket = buckets[alphabet];
            int freeIndex = freeIndices[alphabet];

            if (freeIndex == 0)
                continue;

            if (freeIndex == 1) // we have only one element in this bucket, we are ready
                result[resultInsertIndex[0]++] = bucket[0];
            else
                bucketPart(bucket, freeIndex, character, result, resultInsertIndex);
        }
    }

    public double computeSimilarity(WordCountsArray array) {
        if (array == null || array.size() == 0 || this.size() == 0)
            return 0;

        return scalarProduct(array) / Math.sqrt(scalarProduct(this) * array.scalarProduct(array));
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

    public int getIndexOfWord(String word) {
        if (word == null || word.isEmpty())
            return -1;

        for (int i = 0; i < nextFreeIndex; i++) {
            if (wordCountsArray[i].getWord().equals(word))
                return i;
        }

        return -1;
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

        if (wordCount != null) {
            wordCount.setCount(count); // illegal count is handled by the setter
        }
    }

    public boolean equals(WordCountsArray array) {
        return this.equals((Object) array);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordCountsArray that = (WordCountsArray) o;
        return nextFreeIndex == that.nextFreeIndex &&
                Arrays.equals(wordCountsArray, that.wordCountsArray);
    }

    private boolean wordsEqual(WordCountsArray array) {
        if (nextFreeIndex != array.nextFreeIndex) // they are not the same size
            return false;

        for (int i = 0; i < nextFreeIndex; i++) {
            String word0 = wordCountsArray[i].getWord();
            String word1 = array.wordCountsArray[i].getWord();

            if (!word0.equals(word1))
                return false;
        }

        return true;
    }

    private double scalarProduct(WordCountsArray array) {
        if (!wordsEqual(array))
            return 0;

        double scalarProduct = 0;

        for (int i = 0; i < nextFreeIndex; i++) {
            WordCount wordCount0 = wordCountsArray[i];
            WordCount wordCount1 = array.wordCountsArray[i];

            assert wordCount0.getWord().equals(wordCount1.getWord());

            scalarProduct += wordCount0.getCount() * wordCount1.getCount();
        }

        return scalarProduct;
    }

}
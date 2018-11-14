public class WordCountsArray {

    private WordCount[] wordCountsArray;
    private int nextFreeIndex;

    public WordCountsArray(int initSize) {
        this.wordCountsArray = new WordCount[initSize];
    }

    public void add(String word, int count) {
        if (word == null || word.isEmpty())
            return; // TODO specify

        WordCount wordCount = new WordCount(word, count); // illegal count is handled by the setter // TODO specify

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

    public int size() {
        return this.nextFreeIndex;
    }

    private WordCount getWordCount(int index) {
        if (index >= wordCountsArray.length)
            return null;

        return wordCountsArray[index];
    }

    public String getWord(int index) {
        WordCount wordCount = this.getWordCount(index);
        return wordCount != null? wordCount.getWord(): "";
    }

    public int getCount(int index) {
        WordCount wordCount = this.getWordCount(index);
        return wordCount != null? wordCount.getCount(): -1;
    }

    public void setCount(int index, int count) {
        WordCount wordCount = this.getWordCount(index);

        if (wordCount != null) { // TODO specify
            wordCount.setCount(count); // illegal count is handled by the setter // TODO specify
        }
    }

}
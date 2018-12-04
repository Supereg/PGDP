import java.util.function.Consumer;

public class DocumentCollection {

    private DocumentCollectionCell start;
    private DocumentCollectionCell end;

    public DocumentCollection() {}

    public boolean isEmpty() {
        return start == null;
    }

    public boolean contains(Document document) {
        return indexOf(document) != -1;
    }

    public Document getFirstDocument() {
        return start != null? start.getDocument(): null;
    }

    public Document getLastDocument() {
        return end != null? end.getDocument(): null;
    }

    public Document get(int index) {
        if (index < 0 || index >= numDocuments()) // list is empty or illegal index
            return null;

        return start.get(index, 0);
    }

    public void iterate(Consumer<Document> consumer) {
        for (DocumentCollectionCell cell = start; cell != null; cell = cell.getNext()) {
            consumer.accept(cell.getDocument());
        }
    }

    public int indexOf(Document document) {
        return start != null? start.indexOf(document, 0): -1;
    }

    public void prependDocument(Document document) {
        if (document == null)
            return;

        DocumentCollectionCell previousStart = start;
        start = new DocumentCollectionCell(document, start);
        if (end == null)
            end = start;

        if (previousStart != null)
            previousStart.setPrevious(start);
    }

    public void appendDocument(Document document) {
        if (document == null)
            return;

        DocumentCollectionCell cell = new DocumentCollectionCell(document, null, end);

        if (isEmpty())
            start = end = cell;
        else {
            end.setNext(cell);
            end = cell;
        }
    }

    public void removeFirstDocument() {
        remove(0);
    }

    public void removeLastDocument() {
        if (end == null)
            return;

        start = start.removeLastDocument();
        if (start != null)
            start.setPrevious(null);

        end = start != null? start.end(): null;
    }

    public boolean remove(int index) {
        if (index < 0 || index >= numDocuments()) // list is empty or illegal index
            return false;

        start = start.removeDocumentAt(index, 0);
        if (start != null)
            start.setPrevious(null);

        end = start != null? start.end(): null;

        return true;
    }

    public int numDocuments() {
        return isEmpty()? 0: start.namDocumentsRecursive();
    }

    public double getQuerySimilarity(int index) {
        return start != null && index >= 0 && index < numDocuments()? start.getQuerySimilarityAt(index, 0): Double.NaN;
    }

    public void match(String searchQuery) {
        Document query = new Document("search-query", "qe", "query", null, null, searchQuery);

        this.prependDocument(query);
        this.addZeroWordsToDocuments();

        this.removeFirstDocument();
        query.getWordCounts().sort();

        for (DocumentCollectionCell cell = start; cell != null; cell = cell.getNext()) {
            WordCountsArray documentWordCounts = cell.getDocument().getWordCounts();
            documentWordCounts.sort();

            cell.calculateSimilarityWithQuery(query);
        }

        this.sortBySimilarityDesc();
    }

    private WordCountsArray allWords() {
        WordCountsArray allWords = new WordCountsArray(averageWordAmount());

        iterate(document -> {
            WordCountsArray documentWordCounts = document.getWordCounts();

            int size = documentWordCounts.size();
            for (int i = 0; i < size; i++) {
                allWords.add(documentWordCounts.getWord(i), 0);
            }
        });

        return allWords;
    }

    private void addZeroWordsToDocuments() {
        WordCountsArray allWords = this.allWords();
        int size = allWords.size();

        iterate(document -> {
            WordCountsArray documentWordCounts = document.getWordCounts();

            for (int i = 0; i < size; i++)
                documentWordCounts.add(allWords.getWord(i), 0);
        });
    }

    private int averageWordAmount() {
        if (start == null)
            return 0;

        return start.sumWordAmount() / numDocuments();
    }

    @SuppressWarnings("Duplicates")
    private void sortBySimilarityDesc() {
        int num = numDocuments();
        if (num <= 1)
            return;

        DocumentCollectionCell[] cells = new DocumentCollectionCell[num];

        int index = 0;
        for (DocumentCollectionCell cell = start; cell != null; cell = cell.getNext()) {
            cells[index++] = cell;
        }

        for (int groupSize = 1; groupSize <= cells.length; groupSize *= 2) {
            DocumentCollectionCell[] cellsCopy = new DocumentCollectionCell[cells.length];
            int insertIndex = 0;

            for (int startOfTwoGroups = 0; startOfTwoGroups < cells.length; startOfTwoGroups += (2* groupSize)) {
                int groupA = startOfTwoGroups;
                int groupB = startOfTwoGroups + groupSize;

                int groupAEnd = Math.min(cells.length, groupB);
                int groupBEnd = Math.min(cells.length, startOfTwoGroups + groupSize*2);

                int endInsertIndex = Math.min(startOfTwoGroups + (2* groupSize), cells.length);

                for (; insertIndex < endInsertIndex; insertIndex++) {
                    if (groupA < groupAEnd && groupB < groupBEnd) {
                        cellsCopy[insertIndex] = cells[groupA].getQuerySimilarity() >= cells[groupB].getQuerySimilarity()
                                ? cells[groupA++]
                                : cells[groupB++];
                    }
                    else if (groupA >= groupAEnd && groupB < groupBEnd)
                        cellsCopy[insertIndex] = cells[groupB++];
                    else if (groupB >= groupBEnd && groupA < groupAEnd)
                        cellsCopy[insertIndex] = cells[groupA++];
                    else {
                        System.err.println("Index " + insertIndex + "/" + endInsertIndex
                                + " is unassigned for groupSize: " + groupSize + " and startOfTwoGroups: "
                                + startOfTwoGroups + " (A: " + groupA + " B: " + groupB + ")");
                    }
                }
            }

            cells = cellsCopy;
        }

        DocumentCollectionCell dummy = new DocumentCollectionCell(null, null);
        DocumentCollectionCell current = dummy;

        for (DocumentCollectionCell cell: cells) {
            current.setNext(cell);
            current = cell;
        }

        start = dummy.getNext();
        end = current;
        end.setNext(null);
    }

}
package suchmaschine;

public class DocumentCollectionCell {

    private final Document document;
    private DocumentCollectionCell next;
    private DocumentCollectionCell previous;

    private double searchQuerySimilarity;
    private double relevance;

    public DocumentCollectionCell(Document document) {
        this.document = document;
    }

    public DocumentCollectionCell(Document document, DocumentCollectionCell next) {
        this.document = document;
        this.next = next;
    }

    public DocumentCollectionCell(Document document, DocumentCollectionCell next, DocumentCollectionCell previous) {
        this.document = document;
        this.next = next;
        this.previous = previous;
    }

    public Document getDocument() {
        return document;
    }

    public DocumentCollectionCell getNext() {
        return next;
    }

    public DocumentCollectionCell getPrevious() {
        return previous;
    }

    public double getQuerySimilarity() {
        return searchQuerySimilarity;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setNext(DocumentCollectionCell next) {
        this.next = next;
    }

    public void setPrevious(DocumentCollectionCell previous) {
        this.previous = previous;
    }

    public int namDocumentsRecursive() {
        return 1 + (next != null? next.namDocumentsRecursive(): 0);
    }

    public Document get(int index, int currentIndex) {
        return index == currentIndex? document: next.get(index, ++currentIndex);
    }

    public int indexOf(Document document, int currentIndex) {
        if (this.document.equals(document))
            return currentIndex;
        else
            return next != null? next.indexOf(document, ++currentIndex): -1;
    }

    public DocumentCollectionCell removeLastDocument() {
        if (next == null)
            return null;
        else {
            next = next.removeLastDocument();
            if (next != null)
                next.setPrevious(this);

            return this;
        }
    }

    public DocumentCollectionCell removeDocumentAt(int index, int currentIndex) {
        if (index == currentIndex)
            return next;
        else {
            next = next.removeDocumentAt(index, ++currentIndex);
            if (next != null)
                next.setPrevious(this);

            return this;
        }
    }

    public DocumentCollectionCell end() {
        return next == null? this: next.end();
    }

    public int sumWordAmount() {
        return document.getWordCounts().size() + (next != null? next.sumWordAmount(): 0);
    }

    public void calculateSimilarityWithQuery(Document query, DocumentCollection documentCollection) {
        searchQuerySimilarity = document.getWordCounts().computeSimilarity(query.getWordCounts(), documentCollection);
    }

    public double getQuerySimilarityAt(int index, int currentIndex) {
        if (index == currentIndex)
            return searchQuerySimilarity;
        else
            return next != null? next.getQuerySimilarityAt(index, ++currentIndex): Double.NaN;
    }

    public void calculateRelevance(double weightingFactor, double pageRank) {
        this.relevance = weightingFactor * searchQuerySimilarity + (1 - weightingFactor) * pageRank;
    }

    public double getRelevanceAt(int index, int currentIndex) {
        if (index == currentIndex)
            return relevance;
        else
            return next != null? next.getRelevanceAt(index, ++currentIndex): Double.NaN;
    }

}
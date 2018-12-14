import java.util.Comparator;

public class LinkedDocumentCollection extends DocumentCollection {

    private double[] pageRankCache;
    private int[] currentCachedPageRankDepth;

    @Override
    public void prependDocument(Document document) {
        if (!(document instanceof LinkedDocument)) // catches null pointer
            return;

        if (!this.contains(document))
            super.prependDocument(document);
    }

    @Override
    public void appendDocument(Document document) {
        if (!(document instanceof LinkedDocument)) // catches null pointer
            return;

        if (!this.contains(document))
            super.appendDocument(document);
    }

    public void calculateIncomingLinks() {
        for (Document document: this) {
            LinkedDocument linkedDocument = (LinkedDocument) document;

            LinkedDocumentCollection outgoingLinks = linkedDocument.getOutgoingLinks();
            for (Document outgoing: outgoingLinks) {
                LinkedDocument outgoingDocument = (LinkedDocument) outgoing;

                outgoingDocument.addIncomingLink(linkedDocument);
            }
        }
    }

    @Override
    public void match(String searchQuery) {
        this.match0(new LinkedDocument("query", "qe", "q", null, null, searchQuery, "id"));
        this.sortByRelevance(0.85, 0.6);
    }

    public LinkedDocumentCollection crawl() {
        LinkedDocumentCollection resultCollection = new LinkedDocumentCollection();
        this.crawl(resultCollection);

        return resultCollection;
    }

    private void crawl(LinkedDocumentCollection resultCollection) {
        for (Document document: this) {
            LinkedDocument linkedDocument = (LinkedDocument) document;

            if (resultCollection.contains(linkedDocument))
                continue;

            resultCollection.appendDocument(linkedDocument);
            linkedDocument.getOutgoingLinks(resultCollection).crawl(resultCollection);
        }
    }

    public double[] pageRankRec(double d) {
        long start = System.currentTimeMillis();

        int size = numDocuments();
        int[][] linkMatrix = new int[size][size];

        // we could use the iterator (foreach)
        for (int i = 0; i < size; i++) { // calculate the linkMatrix
            LinkedDocument document0 = (LinkedDocument) this.get(i);

            LinkedDocumentCollection outgoingLinksOfDocument0 = document0.getOutgoingLinks();

            for (int j = 0; j < size; j++) {
                if (i == j)
                    continue;

                LinkedDocument document1 = (LinkedDocument) this.get(j);

                if (outgoingLinksOfDocument0.isEmpty() || outgoingLinksOfDocument0.contains(document1))
                    linkMatrix[j][i] = 1;
            }
        }

        pageRankCache = new double[size];
        currentCachedPageRankDepth = new int[size];

        double[] pageRank = new double[size];
        for (int i = 0; i < size; i++) { // biggest was 87
            pageRank[i] = pageRankRec(linkMatrix, i, d, 90);
            /* Algorithm to find the appropriate recDepth
            int recDepth = 1;

            double lastPageRank;
            int z = 5;
            while (z >= 0) {
                while (true) {
                    pageRankCache = new double[size];
                    currentCachedPageRankDepth = new int[size];

                    lastPageRank = pageRank[i];
                    pageRank[i] = pageRankRec(linkMatrix, i, d, recDepth);
                    recDepth += 1;

                    if (!(Math.abs(lastPageRank - pageRank[i]) >= 1E-7))
                        break;
                    else
                        z = 5;
                }
                z--;

            }

            System.out.println(get(i).getTitle() + " took depth: " + (recDepth - 5 -1)); //*/
        }

        pageRankCache = null; // reset
        currentCachedPageRankDepth = null;

        long stop = System.currentTimeMillis();
        if (stop - start > 1000*60) // debug option
            System.err.println("PageRank took longer than 1 minute");

        return pageRank;
    }

    /**
     * Calculate the page rank for a certain element {@code i}
     *
     * @param linkMatrix        matrix representing links between documents
     * @param i                 element to calculate page rank for
     * @param dampingFactor     damping factor
     * @param recursionDepth    recursion depth
     * @return                  pageRank of element {@code i}
     */
    public double pageRankRec(int[][] linkMatrix, int i, double dampingFactor, int recursionDepth) {
        int savedPageRankDepth = currentCachedPageRankDepth[i];
        // check if he already calculated a page rank with same or higher recursionDepth
        if (savedPageRankDepth >= recursionDepth)
            return pageRankCache[i]; // yes, than take it

        double pageRank = (1 - dampingFactor) / linkMatrix.length;
        try {
            if (recursionDepth <= 1) // exit condition
                return pageRank;

            double sum = 0;
            for (int j = 0; j < linkMatrix.length; j++) {
                boolean isJLinkedToI = linkMatrix[i][j] == 1;
                if (!isJLinkedToI || i == j)
                    continue;

                int numberOfLinks = 0;
                for (int[] cArrayPart : linkMatrix)
                    numberOfLinks += cArrayPart[j];

                sum += pageRankRec(linkMatrix, j, dampingFactor, recursionDepth - 1) / numberOfLinks;
            }

            pageRank += dampingFactor * sum;

            return pageRank;
        } finally {
            // since we did not take an already processed page rank, we need to save our calculated one
            pageRankCache[i] = pageRank;
            currentCachedPageRankDepth[i] = recursionDepth;
        }
    }

    private double[] sortByRelevance(double dampingFactor, double weightingFactor) {
        double[] pageRank = pageRankRec(dampingFactor);
        double[] relevance = new double[numDocuments()];

        int index = 0;
        for (DocumentCollectionCell cell = start; cell != null; cell = cell.getNext(), index++) {
            cell.calculateRelevance(weightingFactor, pageRank[index]);
        }

        mergeSortListBy(Comparator.comparingDouble(DocumentCollectionCell::getRelevance));

        index = 0;
        for (DocumentCollectionCell cell = start; cell != null; cell = cell.getNext(), index++) {
            relevance[index] = cell.getRelevance();
        }

        return relevance;
    }

}
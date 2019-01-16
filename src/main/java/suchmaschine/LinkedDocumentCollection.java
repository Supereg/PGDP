package suchmaschine;

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


        double[] pageRank = new double[size];
        for (int i = 0; i < size; i++) { // biggest was 87
            pageRankCache = new double[size];
            currentCachedPageRankDepth = new int[size];

            pageRank[i] = pageRankRec(linkMatrix, i, d, 200);

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

            pageRankCache = null; // reset
            currentCachedPageRankDepth = null;
        }

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
        double pageRank = (1 - dampingFactor) / linkMatrix.length;
        try {
            if (recursionDepth <= 1) // exit condition
                return pageRank;

            int nextRecursionDepth = recursionDepth - 1;

            double sum = 0;
            for (int j = 0; j < linkMatrix.length; j++) {
                boolean isJLinkedToI = linkMatrix[i][j] == 1;
                if (!isJLinkedToI || i == j)
                    continue;

                int numberOfLinks = 0;
                for (int[] cArrayPart : linkMatrix)
                    numberOfLinks += cArrayPart[j];

                double pageRankJ;

                if (currentCachedPageRankDepth[j] >= nextRecursionDepth)
                    pageRankJ = pageRankCache[j];
                else
                    pageRankJ = pageRankRec(linkMatrix, j, dampingFactor, nextRecursionDepth);

                sum += pageRankJ / numberOfLinks;
            }

            pageRank += dampingFactor * sum;

            return pageRank;
        } finally {
            if (currentCachedPageRankDepth[i] < recursionDepth) {
                pageRankCache[i] = pageRank;
                currentCachedPageRankDepth[i] = recursionDepth;
            }
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

    public double[] pageRank(double dampingFactor) {
        int size = numDocuments();

        double[][] A = new double[size][size];
        for (int i = 0; i < size; i++) { // fill A
            LinkedDocument document0 = (LinkedDocument) get(i);

            LinkedDocumentCollection outgoingLinks = document0.getOutgoingLinks();
            int linkAmount = outgoingLinks.isEmpty()? size - 1: outgoingLinks.numDocuments();

            for (int j = 0; j < size; j++) {
                if (i == j)
                    continue;
                LinkedDocument document1 = (LinkedDocument) get(j);

                if (outgoingLinks.isEmpty() || outgoingLinks.contains(document1))
                    A[j][i] = 1D / linkAmount;
            }
        }

        double[][] M = new double[size][size];
        double pageRankBase = (1 - dampingFactor) / size;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                M[i][j] = dampingFactor * A[i][j] + pageRankBase;


        // int iterations = 0;
        double[] result = multiply(M, extractFirstColumn(M));
        for(;;) {
            M = multiply(M, M);
            double[] lastResult = result;
            result = multiply(M, extractFirstColumn(M));

            // iterations++;

            if (isPreciseEnough(result, lastResult))
                break;
        }

        // System.out.println("Took " + iterations + " iterations!"); // debug - highest was 7

        return result;
    }

    @SuppressWarnings("Duplicates")
    private static double[] multiply(double[][] matrix, double[] vector) {
        if (matrix.length == 0 || matrix[0].length != vector.length)
            throw new IllegalArgumentException("Illegal size");

        double[] resultVector = new double[matrix.length];

        int columnLength = matrix[0].length;
        for (int i = 0; i < matrix.length; i++) {
            double iSum = 0;

            for (int j = 0; j < columnLength; j++) {
                iSum += matrix[i][j] * vector[j];
            }

            resultVector[i] = iSum;
        }

        return resultVector;
    }

    private static double[][] multiply(double[][] matrix0, double[][] matrix1) {
        if (matrix0.length == 0 || matrix1.length == 0 || matrix0[0].length == 0 || matrix1[0].length == 0)
            throw new IllegalArgumentException("matrix cannot be empty");
        if (matrix0.length != matrix1[0].length || matrix0[0].length != matrix1.length)
            throw new IllegalArgumentException("illegal matrix size");

        double[][] result = new double[matrix0.length][matrix1[0].length];

        for (int i = 0; i < matrix0.length; i++) {
            for (int j = 0; j < matrix1[i].length; j++) {
                double value = 0;

                for (int k = 0; k < matrix1.length; k++) {
                    value += matrix0[i][k] * matrix1[k][j];
                }

                result[i][j] = value;
            }
        }

        return result;
    }

    private static boolean isPreciseEnough(double[] pageRank, double[] previousPageRank) {
        for (int i = 0; i < pageRank.length; i++) {
            double v = pageRank[i];
            double previous = previousPageRank[i];

            if (!(Math.abs(v - previous) <= 1E-7))
                return false;
        }

        return true;
    }

    private static double[] extractFirstColumn(double[][] matrix) {
        double[] v = new double[matrix.length];
        for (int i = 0; i < v.length; i++)
            v[i] = matrix[i][0];

        return v;
    }

}
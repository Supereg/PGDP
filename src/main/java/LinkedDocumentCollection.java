public class LinkedDocumentCollection extends DocumentCollection {

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
        int size = numDocuments();
        int[][] linkMatrix = new int[size][size];

        for (int i = 0; i < size; i++) {
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
        for (int i = 0; i < size; i++)
            pageRank[i] = 1D / size;

        for (int i = 0; i < size; i++) {
            int recDepth = 1;

            double lastPageRank;
            do {
                lastPageRank = pageRank[i];
                pageRank[i] = pageRankRec(linkMatrix, i, d, recDepth++);
            } while (Math.abs(lastPageRank - pageRank[i]) > 1E-6);
        }

        return pageRank;
    }

    /**
     *
     * @param C         matrix representing links between documents
     * @param i         element to calculate page rank for
     * @param d         damping factor
     * @param recDepth  recursion depth
     * @return
     */
    public double pageRankRec(int[][] C, int i, double d, int recDepth) {
        double pageRank;

        pageRank = (1 - d) / C.length;

        if (--recDepth == 0)
            return pageRank;

        double sum = 0;

        for (int j = 0; j < C.length; j++) {
            int linked = C[i][j];
            if (linked == 0)
                continue;

            double numerator = pageRankRec(C, j, d, recDepth);
            int denominator = 0;

            for (int[] cArrayPart : C)
                denominator += cArrayPart[j];

            sum += numerator / denominator;
        }

        pageRank += d * sum;

        return pageRank;
    }

}
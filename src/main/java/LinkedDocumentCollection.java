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
            linkedDocument.getOutgoingLinks().crawl(resultCollection);
        }
    }

}
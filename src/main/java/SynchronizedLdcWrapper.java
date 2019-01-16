import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SynchronizedLdcWrapper {

    private LinkedDocumentCollection ldc;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    private final double pageRankDampingFactor = 0.85;
    private final double weightingFactor = 0.6;

    public SynchronizedLdcWrapper() {
        this.ldc = new LinkedDocumentCollection();
    }

    public void appendDocument(LinkedDocument document) {
        readWriteLock.writeLock().lock();
        try {
            ldc.appendDocument(document);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public void forEach(Consumer<Document> callback) {
        readWriteLock.readLock().lock();
        try {
            ldc.iterate(callback);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public void query(String query, BiConsumer<Document, Double> callback) {
        List<Entry<Document, Double>> result = new ArrayList<>();
        readWriteLock.writeLock().lock();
        try {
            ldc.match(query);

            for (Document document: ldc) {
                int index = ldc.indexOf(document);
                double relevance = ldc.getRelevance(index);

                // we need to save this first into a arrayList, since our DocumentCollection doesn't return an array
                // with the relevance of every document. And callback should not be executed when still in locked region
                result.add(new AbstractMap.SimpleEntry<>(document, relevance));
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }

        // passing results to callback
        result.forEach(entry -> callback.accept(entry.getKey(), entry.getValue()));
    }

    public void pageRank(BiConsumer<Document, Double> callback) {
        List<Entry<Document, Double>> result = new ArrayList<>();
        readWriteLock.writeLock().lock();
        try {
            double[] pageRank = ldc.pageRank(pageRankDampingFactor);

            int index = 0;
            for (Document document: ldc) // same as above
                result.add(new AbstractMap.SimpleEntry<>(document, pageRank[index++]));
        } finally {
            readWriteLock.writeLock().unlock();
        }

        result.forEach(entry -> callback.accept(entry.getKey(), entry.getValue()));
    }

    public void crawl() {
        readWriteLock.writeLock().lock();
        try {
            ldc = ldc.crawl();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

}
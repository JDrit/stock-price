package net.batchik.jd;

import com.google.cloud.firestore.*;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class StockUpdater implements Runnable {
    private static final int BATCH_SIZE = 30;
    private static final String STOCK_COLLECTION = "stocks";

    private final Firestore db;
    private final StockPriceFetcher stockPriceFetcher;

    public StockUpdater(@Nonnull final Firestore db, @Nonnull final StockPriceFetcher stockPriceFetcher) {
        this.db = db;
        this.stockPriceFetcher = stockPriceFetcher;
    }

    @Override
    public void run() {
        try {
            final QuerySnapshot snapshot = db.collection(STOCK_COLLECTION).get().get();
            final List<List<QueryDocumentSnapshot>> documents = Lists.partition(snapshot.getDocuments(), BATCH_SIZE);

            for (final List<QueryDocumentSnapshot> docs: documents) {
                final List<String> ids = docs.stream().map(DocumentSnapshot::getId).collect(Collectors.toList());
                final Collection<StockPrice> stockPrices = stockPriceFetcher.getStockPrices(ids);

                final WriteBatch batch = db.batch();

                for (final StockPrice stockPrice : stockPrices) {
                    final DocumentReference ref = db.collection(STOCK_COLLECTION).document(stockPrice.getSymbol());
                    batch.update(ref, "price", stockPrice.getPrice());
                    batch.update(ref, "volume", stockPrice.getVolume());
                    batch.update(ref, "timestamp", stockPrice.getTimestamp());
                }

                batch.commit().get();

            }

        } catch (final Exception ex) {
            System.err.println("failed to query stocks");
            ex.printStackTrace();
        }

    }
}

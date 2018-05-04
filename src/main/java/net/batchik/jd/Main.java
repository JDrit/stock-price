package net.batchik.jd;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(final String[] args) throws Exception {
        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        // Use the application default credentials
        final InputStream serviceAccount = new FileInputStream("../stock-price-29e2b-48780bc103fe.json");
        final GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

        final FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();
        FirebaseApp.initializeApp(options);

        final Firestore db = FirestoreClient.getFirestore();

        final String apiKey = Files.readAllLines(Paths.get("../alphavantage.txt")).get(0);

        final StockPriceFetcher stockPriceFetcher = new AlphaVantageFetcher(apiKey);

        final Runnable updater = new StockUpdater(db, stockPriceFetcher);
        final Future<?> future = executorService.scheduleAtFixedRate(updater, 0, 30, TimeUnit.SECONDS);


        future.get();




    }
}

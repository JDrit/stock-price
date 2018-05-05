package net.batchik.jd;

import com.google.gson.JsonArray;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.client.ConnectionBackoffStrategy;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultBackoffStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AlphaVantageFetcher implements StockPriceFetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlphaVantageFetcher.class);
    private static final ConnectionReuseStrategy REUSE_STRATEGY = DefaultConnectionReuseStrategy.INSTANCE;
    private static final ConnectionBackoffStrategy BACKOFF_STRATEGY = new DefaultBackoffStrategy();
    private static final ConnectionKeepAliveStrategy KEEP_ALIVE_STRATEGY = new LongKeepAliveStrategy(40, TimeUnit.SECONDS);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


    private final String apiKey;

    private final CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionReuseStrategy(REUSE_STRATEGY)
            .setConnectionBackoffStrategy(BACKOFF_STRATEGY)
            .setConnectionTimeToLive(30, TimeUnit.SECONDS)
            .setKeepAliveStrategy(KEEP_ALIVE_STRATEGY)
            .build();


    public AlphaVantageFetcher(@Nonnull final String apiKey) {
        this.apiKey = apiKey;
    }

    @Nonnull
    @Override
    public Collection<StockPrice> getStockPrices(@Nonnull final Collection<String> symbols) throws IOException {
        final StringBuilder symbolBuilder = new StringBuilder();
        final String function = "BATCH_STOCK_QUOTES";

        for (final String symbol : symbols) {
            symbolBuilder.append(symbol).append(',');
        }

        final String uri = String.format("https://www.alphavantage.co/query?function=%s&symbols=%s&apikey=%s",
                function, symbolBuilder.toString(), apiKey);
        final HttpGet request = new HttpGet(uri);

        try(final CloseableHttpResponse response = httpClient.execute(request)) {
            final String body = EntityUtils.toString(response.getEntity());
            final JSONObject result = new JSONObject(body);
            final JSONArray array = result.getJSONArray("Stock Quotes");
            final int size = array.length();
            final List<StockPrice> prices = new ArrayList<>(size);

            for (int i = 0 ; i < size ; i++) {
                try {
                    final JSONObject stock = array.getJSONObject(i);
                    final StockPrice price = new StockPrice(
                            stock.getString("1. symbol"),
                            stock.getDouble("2. price"),
                            stock.getLong("3. volume"),
                            DATE_FORMAT.parse(stock.getString("4. timestamp")));
                    prices.add(price);
                } catch (final ParseException ex) {
                    LOGGER.warn("failed to parse date", ex);
                } catch (final JSONException ex) {
                    LOGGER.warn("failed to parse json", ex);
                }
            }

            return prices;
        }
    }
}

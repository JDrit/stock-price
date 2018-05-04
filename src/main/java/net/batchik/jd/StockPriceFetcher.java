package net.batchik.jd;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;

public interface StockPriceFetcher {

    /**
     * Fetches the current stock prices for the given stocks
     * @param symbols the stocks to look up
     * @return the price info about the stock
     * @throws IOException due to any network issue during the lookup
     */
    @Nonnull
    Collection<StockPrice> getStockPrices(@Nonnull final Collection<String> symbols) throws IOException;
}

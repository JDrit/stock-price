package net.batchik.jd;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.Objects;

public class StockPrice {

    private final String symbol;
    private final double price;
    private final long volume;
    private final Date timestamp;

    public StockPrice(@Nonnull final String symbol, final double price, final long volume, @Nonnull final Date timestamp) {
        this.symbol = symbol;
        this.price = price;
        this.volume = volume;
        this.timestamp = timestamp;
    }

    @Nonnull
    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public long getVolume() {
        return volume;
    }

    @Nonnull
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("symbol", symbol)
                .add("price", price)
                .add("volumn", volume)
                .add("timestamp", timestamp)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, price, volume, timestamp);
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof StockPrice
                && ((StockPrice) other).symbol.equals(this.symbol)
                && ((StockPrice) other).price == this.price
                && ((StockPrice) other).volume == this.volume
                && ((StockPrice) other).timestamp == this.timestamp;
    }


}

package net.batchik.jd;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.protocol.HttpContext;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class LongKeepAliveStrategy implements ConnectionKeepAliveStrategy {
    private final long keepAlive;

    public LongKeepAliveStrategy(final long keepAlive, @Nonnull final TimeUnit timeUnit) {
        this.keepAlive = timeUnit.toMillis(keepAlive);
    }

    /**
     * Returns the duration of time which this connection can be safely kept
     * idle. If the connection is left idle for longer than this period of time,
     * it MUST not reused. A value of 0 or less may be returned to indicate that
     * there is no suitable suggestion.
     * <p>
     * When coupled with a {@link ConnectionReuseStrategy}, if
     * {@link ConnectionReuseStrategy#keepAlive(
     *HttpResponse, HttpContext)} returns true, this allows you to control
     * how long the reuse will last. If keepAlive returns false, this should
     * have no meaningful impact
     *
     * @param response The last response received over the connection.
     * @param context  the context in which the connection is being used.
     * @return the duration in ms for which it is safe to keep the connection
     * idle, or &lt;=0 if no suggested duration.
     */
    @Override
    public long getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
        return keepAlive;
    }
}


package moe.protasis.sephirah.exception;

import lombok.Getter;
import org.joda.time.DateTime;

public class RateLimitedException extends APIException {
    @Getter
    private final DateTime retryAfter;

    public RateLimitedException() {
        super(429, 56, "rate limited");
        retryAfter = null;
    }

    public RateLimitedException(DateTime retryAfter) {
        super(429, 56, "rate limited");
        this.retryAfter = retryAfter;
    }
}

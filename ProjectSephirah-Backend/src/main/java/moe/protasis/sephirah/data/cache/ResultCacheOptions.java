package moe.protasis.sephirah.data.cache;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;

public record ResultCacheOptions<T>(
        T entity,
        ReadableDuration cacheDuration
) {
    public boolean ShouldCache() {
        return cacheDuration.isLongerThan(Duration.ZERO);
    }

    public DateTime GetExpiryTime() {
        return DateTime.now().plus(cacheDuration);
    }
}

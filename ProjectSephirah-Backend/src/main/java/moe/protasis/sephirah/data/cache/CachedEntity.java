package moe.protasis.sephirah.data.cache;

import lombok.*;
import moe.protasis.sephirah.provider.IProxyProvider;
import moe.protasis.sephirah.util.RandomStringGenerator;
import moe.protasis.sephirah.util.UniqueIdGenerator;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("entity_cache")
@Builder
@With
@AllArgsConstructor
@NoArgsConstructor
public class CachedEntity<T> {
    @Id
    private String id;
    private String path;
    private T entity;
    private DateTime created;
    @Indexed(expireAfter = "0s")
    private DateTime expire;
    private String entityType;

    public CachedEntity(T entity, String path, Duration expire) {
        id = RandomStringGenerator.GenerateRandomSnowflake();
        this.path = path;
        this.expire = DateTime.now().plus(expire);
        this.entity = entity;
        this.created = DateTime.now();
        this.entityType = entity.getClass().getName();
    }

    public static String JoinPath(String... parts) {
        return String.join("/", parts);
    }
}


package moe.protasis.sephirah.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import moe.protasis.sephirah.data.cache.CachedEntity;
import moe.protasis.sephirah.data.manga.ChapterDetails;
import moe.protasis.sephirah.data.manga.MangaDetails;
import moe.protasis.sephirah.provider.manga.IProxyMangaProvider;
import moe.protasis.sephirah.repository.CachedEntityRepo;
import moe.protasis.sephirah.repository.CachedEntityService;
import moe.protasis.sephirah.util.IJsonSerializable;
import moe.protasis.sephirah.util.JsonWrapper;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.ISODateTimeFormat;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MangaProviderService {
    @Getter
    private static MangaProviderService instance;
    @Autowired
    private OkHttpClient client;
    @Autowired
    private CachedEntityService cacheService;
    @Autowired
    private CachedEntityRepo cachedEntityRepo;
    @Getter
    private final Map<String, IProxyMangaProvider> mangaProviders = new HashMap<>();

    public MangaProviderService() {
        instance = this;

        // register providers
        var reflections = new Reflections("moe.protasis.sephirah.provider.manga.impl");
        var providerClasses = reflections.getSubTypesOf(moe.protasis.sephirah.provider.manga.IProxyMangaProvider.class);

        for (var clazz : providerClasses) {
            try {
                var provider = clazz.getDeclaredConstructor().newInstance();
                mangaProviders.put(provider.GetId(), provider);
            } catch (Exception e) {
                log.error("Failed to register provider: " + clazz.getName(), e);
            }
        }

        log.info("Registered {} manga providers", mangaProviders.size());
    }

    @Nullable
    public IProxyMangaProvider GetMangaProvider(String id) {
        return mangaProviders.get(id);
    }

    public CacheQueryResult<MangaDetails> GetMangaDetails(IProxyMangaProvider provider, String id, String lang) {
        var path = CachedEntity.JoinPath(provider.GetId(), "manga", id, lang);
        var ret = cacheService.GetEntity(path, MangaDetails.class);
        if (ret != null) {
            return new CacheQueryResult<>(ret.getEntity(), true, ret.getExpire());
        }

        var data = provider.GetMangaDetails(client, id, lang);
        if (data != null) {
            var entity = new CachedEntity<>(data, path, Duration.standardDays(1));
            cachedEntityRepo.save(entity);
            return new CacheQueryResult<>(entity.getEntity(), false, entity.getExpire());
        }

        return null;
    }

    public CacheQueryResult<ChapterDetails> GetChapterDetails(IProxyMangaProvider provider, String mangaId, String chapterId, String lang) {
        var dataPath = CachedEntity.JoinPath(provider.GetId(), "manga", mangaId, "chapter", chapterId, lang);
        var imagePath = CachedEntity.JoinPath(provider.GetId(), "manga", mangaId, "chapter", chapterId, lang, "images");

        var ret = cacheService.GetEntity(dataPath, ChapterDetails.class);
        var images = cacheService.GetEntity(imagePath, List.class);
        if (ret != null) {
            // get images
            if (images != null) {
                return new CacheQueryResult<>(ret.getEntity().toBuilder().images(images.getEntity()).build(), true, ret.getExpire());
            }
        }

        var data = provider.GetChapterDetails(client, mangaId, chapterId, lang);
        if (data != null) {
            var dataEntity = new CachedEntity<>(data, dataPath, Duration.standardDays(1));
            var imageEntity = new CachedEntity<>(data.getImages(), imagePath, Duration.standardHours(1));
            if (ret == null) cachedEntityRepo.save(dataEntity);
            if (images == null) cachedEntityRepo.save(imageEntity);

            // use the shorter expire out of the two
            return new CacheQueryResult<>(data, false, dataEntity.getExpire().isBefore(imageEntity.getExpire()) ? dataEntity.getExpire() : imageEntity.getExpire());
        }

        return null;
    }

    public record CacheQueryResult<T>(
            T entity,
            boolean cacheHit,
            DateTime expire
    ) implements IJsonSerializable {
        @Override
        public void SerializeInternal(JsonWrapper json) {
            json.Set("cache_hit", cacheHit);
            json.Set("expire", ISODateTimeFormat.dateTime().print(expire));
        }
    }
}

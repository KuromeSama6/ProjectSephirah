package moe.protasis.sephirah.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import moe.protasis.sephirah.data.cache.CachedEntity;
import moe.protasis.sephirah.data.manga.ChapterDetails;
import moe.protasis.sephirah.data.manga.ChapterImages;
import moe.protasis.sephirah.data.manga.MangaDetails;
import moe.protasis.sephirah.provider.IProxyProvider;
import moe.protasis.sephirah.provider.manga.IProxyMangaProvider;
import moe.protasis.sephirah.repository.CachedEntityRepo;
import moe.protasis.sephirah.repository.CachedEntityService;
import moe.protasis.sephirah.util.IJsonSerializable;
import moe.protasis.sephirah.util.JsonWrapper;
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
public class ProviderService {
    @Getter
    private static ProviderService instance;
    @Autowired
    private OkHttpClient client;
    @Autowired
    private CachedEntityService cacheService;
    @Autowired
    private CachedEntityRepo cachedEntityRepo;
    @Getter
    private final Map<String, IProxyProvider> mangaProviders = new HashMap<>();

    public ProviderService() {
        instance = this;

        // register providers
        var reflections = new Reflections("moe.protasis.sephirah.provider");
        var providerClasses = reflections.getSubTypesOf(IProxyProvider.class);

        for (var clazz : providerClasses) {
            if (clazz.isInterface()) continue;

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
    public <T extends IProxyProvider> T GetMangaProvider(String id, Class<T> clazz) {
        var ret = mangaProviders.get(id);
        if (!clazz.isInstance(ret)) {
            return null;
        }

        return clazz.cast(ret);
    }

    public CacheQueryResult<MangaDetails> GetMangaDetails(IProxyMangaProvider provider, String id, String lang) {
        var path = CachedEntity.JoinPath(provider.GetId(), "manga", id, lang);
        var ret = cacheService.GetEntity(path, MangaDetails.class);
        if (ret != null) {
            return new CacheQueryResult<>(ret.getEntity(), true, ret.getExpire());
        }

        var data = provider.GetMangaDetails(client, id, lang);
        if (data != null) {
            var entity = new CachedEntity<>(data.entity(), path, data.cacheDuration());
            cachedEntityRepo.save(entity);
            return new CacheQueryResult<>(entity.getEntity(), false, entity.getExpire());
        }

        return null;
    }

    public CacheQueryResult<ChapterImages> GetChapterImages(IProxyMangaProvider provider, String mangaId, String chapterId, String lang) {
        var path = CachedEntity.JoinPath(provider.GetId(), "manga", mangaId, "chapter", chapterId, lang, "images");
        var ret = cacheService.GetEntity(path, ChapterImages.class);
        if (ret != null) {
            return new CacheQueryResult<>(ret.getEntity(), true, ret.getExpire());
        }

        var data = provider.GetChapterImages(client, mangaId, chapterId, lang);
        if (data != null) {
            var entity = new CachedEntity<>(data.entity(), path, data.cacheDuration());
            cachedEntityRepo.save(entity);
            return new CacheQueryResult<>(entity.getEntity(), false, entity.getExpire());
        }

        return null;
    }

    public CacheQueryResult<ChapterDetails> GetChapterDetails(IProxyMangaProvider provider, String mangaId, String chapterId, String lang) {
        var dataPath = CachedEntity.JoinPath(provider.GetId(), "manga", mangaId, "chapter", chapterId, lang);

        var ret = cacheService.GetEntity(dataPath, ChapterDetails.class);
        if (ret != null) {
            return new CacheQueryResult<>(ret.getEntity(), true, ret.getExpire());
        }

        var data = provider.GetChapterDetails(client, mangaId, chapterId, lang);
        if (data != null) {
            var dataEntity = new CachedEntity<>(data.entity(), dataPath, data.cacheDuration());
            cachedEntityRepo.save(dataEntity);

            // use the shorter expire out of the two
            return new CacheQueryResult<>(data.entity(), false, dataEntity.getExpire());
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

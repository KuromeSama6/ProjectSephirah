package moe.protasis.sephirah.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import moe.protasis.sephirah.provider.manga.IProxyMangaProvider;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class MangaProviderService {
    @Getter
    private final Map<String, IProxyMangaProvider> mangaProviders = new HashMap<>();

    public MangaProviderService() {
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
}

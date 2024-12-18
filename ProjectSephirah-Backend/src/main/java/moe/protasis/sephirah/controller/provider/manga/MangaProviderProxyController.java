package moe.protasis.sephirah.controller.provider.manga;

import lombok.extern.slf4j.Slf4j;
import moe.protasis.sephirah.exception.NotFoundException;
import moe.protasis.sephirah.provider.manga.IProxyMangaProvider;
import moe.protasis.sephirah.repository.CachedEntityService;
import moe.protasis.sephirah.service.ProviderService;
import moe.protasis.sephirah.util.JsonWrapper;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/provider/{providerId}/proxy", consumes = "*/*", produces = "application/json")
public class MangaProviderProxyController {
    @Autowired
    private OkHttpClient client;
    @Autowired
    private ProviderService providerService;
    @Autowired
    private CachedEntityService cacheService;

    @GetMapping("/status")
    public JsonWrapper GetStatus(@PathVariable String providerId) {
        var provider = providerService.GetMangaProvider(providerId, IProxyMangaProvider.class);
        if (provider == null)
            throw new NotFoundException("provider %s not found".formatted(providerId));

        provider.VerifyStatus(client);
        return new JsonWrapper();
    }

    @GetMapping("/search")
    public JsonWrapper SearchManga(@RequestParam String kw, @RequestParam String lang, IProxyMangaProvider provider) {
        var ret = provider.Search(client, kw, lang);

        return new JsonWrapper()
                .Set("provider", provider.GetId())
                .Set("results", ret);
    }

    @GetMapping("/manga/{id}")
    public JsonWrapper GetMangaDetails(@PathVariable String id, IProxyMangaProvider provider, @RequestParam String lang) {
        var ret = providerService.GetMangaDetails(provider, id, lang);

        if (ret == null)
            throw new NotFoundException();

        return new JsonWrapper()
                .Set("provider", provider.GetId())
                .SetObject("details", ret.entity())
                .Set("cache", ret);
    }

    @GetMapping("/manga/{mangaId}/{chapterId}")
    public JsonWrapper GetChapterDetails(
            IProxyMangaProvider provider,
            @PathVariable String mangaId,
            @PathVariable String chapterId,
            @RequestParam String lang
    ) {
        var ret = providerService.GetChapterDetails(provider, mangaId, chapterId, lang);
        if (ret == null)
            throw new NotFoundException();

        return new JsonWrapper()
                .Set("provider", provider.GetId())
                .SetObject("details", ret.entity())
                .Set("cache", ret )
                .Set("image_cache_length", provider.GetImageCacheLength());
    }

    @GetMapping("/manga/{mangaId}/{chapterId}/images")
    public JsonWrapper GetChapterImages(
            IProxyMangaProvider provider,
            @PathVariable String mangaId,
            @PathVariable String chapterId,
            @RequestParam String lang
    ) {
        var images = providerService.GetChapterImages(provider, mangaId, chapterId, lang);
        if (images == null)
            throw new NotFoundException();

        return new JsonWrapper()
                .Set("provider", provider.GetId())
                .SetObject("images", images.entity())
                .Set("cache", images)
                .Set("image_cache_length", provider.GetImageCacheLength());
    }
}

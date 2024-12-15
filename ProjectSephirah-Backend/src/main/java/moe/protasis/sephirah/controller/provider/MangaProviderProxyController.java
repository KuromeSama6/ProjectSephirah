package moe.protasis.sephirah.controller.provider;

import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import moe.protasis.sephirah.exception.NotFoundException;
import moe.protasis.sephirah.provider.manga.IProxyMangaProvider;
import moe.protasis.sephirah.service.MangaProviderService;
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
    private MangaProviderService providerService;

    @GetMapping("/status")
    public JsonWrapper GetStatus(@PathVariable String providerId) {
        var provider = providerService.GetMangaProvider(providerId);
        if (provider == null)
            throw new NotFoundException("provider %s not found".formatted(providerId));

        provider.VerifyStatus(client);
        return new JsonWrapper();
    }

    @GetMapping("/search")
    public JsonWrapper SearchManga(@RequestParam String kw, IProxyMangaProvider provider) {
        var ret = provider.Search(client, kw);

        return new JsonWrapper()
                .Set("provider", provider.GetId())
                .Set("results", ret);
    }

    @GetMapping("/manga/{id}")
    public JsonWrapper GetMangaDetails(@PathVariable String id, IProxyMangaProvider provider, @PathParam("lang") String language) {
        var ret = provider.GetMangaDetails(client, id, language);

        return new JsonWrapper()
                .Set("provider", provider.GetId())
                .SetObject("details", ret);
    }

    @GetMapping("/manga/{mangaId}/{chapterId}")
    public JsonWrapper GetChapterDetails(
            IProxyMangaProvider provider,
            @PathVariable String mangaId,
            @PathVariable String chapterId,
            @PathParam("lang") String language
    ) {
        var details = provider.GetChapterDetails(client, mangaId, chapterId, language);
        return new JsonWrapper()
                .Set("provider", provider.GetId())
                .SetObject("details", details)
                .Set("image_cache_length", provider.GetImageCacheLength());
    }

    @GetMapping("/manga/{mangaId}/{chapterId}/images")
    public JsonWrapper GetChapterImages(
            IProxyMangaProvider provider,
            @PathVariable String mangaId,
            @PathVariable String chapterId,
            @PathParam("lang") String language
    ) {
        var images = provider.GetChapterImages(client, mangaId, chapterId, language);
        return new JsonWrapper()
                .Set("provider", provider.GetId())
                .Set("images", images)
                .Set("image_cache_length", provider.GetImageCacheLength());
    }
}

package moe.protasis.sephirah.controller.provider.manga;

import jakarta.websocket.server.PathParam;
import moe.protasis.sephirah.exception.provider.ProviderConnectionException;
import moe.protasis.sephirah.exception.provider.ProviderRequestException;
import moe.protasis.sephirah.service.MangaProviderService;
import moe.protasis.sephirah.util.JsonWrapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping(value = "/api/provider", consumes = "application/json", produces = "application/json")
public class MangaProviderHelperController {
    @Autowired
    private MangaProviderService providerService;
    @Autowired
    private OkHttpClient client;

    @GetMapping(value = "/manhuagui/proxy/image", consumes = "*/*", produces = "image/png")
    private ResponseEntity<byte[]> ProxyGetManHuaGuiImage(@PathParam("uri") String uri) {
        var req = new Request.Builder()
                .get()
                .header("Referer", "https://m.manhuagui.com")
                .url("https://eu1.hamreus.com" + uri)
                .build();

        try (var res = client.newCall(req).execute()) {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "image/png");

            // most manga updates weekly, so 6 days
            headers.setCacheControl(CacheControl.maxAge(6, TimeUnit.DAYS).cachePublic().immutable());

            return new ResponseEntity<>(res.body().bytes(), headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/mangadex/proxy/image", consumes = "*/*", produces = "image/png")
    private ResponseEntity<byte[]> ProxyMangadexImage(@PathParam("uri") String uri) {
        var req = new Request.Builder()
                .get()
                .header("Referer", "https://mangadex.org")
                .header("Origin", "https://mangadex.org")
                .url("https://mangadex.org" + uri)
                .build();

        try (var res = client.newCall(req).execute()) {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "image/png");

            headers.setCacheControl(CacheControl.maxAge(14, TimeUnit.DAYS).cachePublic().immutable());

            return new ResponseEntity<>(res.body().bytes(), headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}

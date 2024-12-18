package moe.protasis.sephirah.provider.manga.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import moe.protasis.sephirah.data.cache.ResultCacheOptions;
import moe.protasis.sephirah.data.manga.*;
import moe.protasis.sephirah.exception.provider.ProviderNotAvailableException;
import moe.protasis.sephirah.exception.provider.ProviderRequestException;
import moe.protasis.sephirah.provider.auth.IAuthenticationProvider;
import moe.protasis.sephirah.provider.manga.IProxyMangaProvider;
import moe.protasis.sephirah.provider.manga.lib.jm.JMMagicConstants;
import moe.protasis.sephirah.provider.manga.lib.jm.JMUtil;
import moe.protasis.sephirah.service.ProviderService;
import moe.protasis.sephirah.util.JsonWrapper;
import moe.protasis.sephirah.util.ProviderUtil;
import okhttp3.*;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
public class MangaProvider_18Comic implements IProxyMangaProvider {
    private static final String IMAGE_CDN_URL = "https://cdn-msp2.18comic.vip/media";
    private final AuthenticationProvider authProvider = new AuthenticationProvider();

    @Override
    public String GetId() {
        return "18comic";
    }

    @Override
    public IAuthenticationProvider GetAuthenticationProvider() {
        return authProvider;
    }

    @Override
    public Request.Builder GetRequestBuilder(String path) {
        return new Request.Builder()
                .header("tokenparam", "")
                .get()
                .url("https://www.cdnxxx-proxy.vip" + path);
    }

    private Request.Builder GetRequestBuilder(String path, JMUtil.JMTokenAndParam tokens) {
        var ret = GetRequestBuilder(path);
        ret.header("token", tokens.token());
        ret.header("tokenparam", tokens.tokenParam());
        ret.header("version", JMMagicConstants.API_UPDATE_VERSION);

        return ret;
    }

    @Override
    public void VerifyStatus(OkHttpClient client) {
        var req = GetRequestBuilder("").build();

        var res = ProviderUtil.SendProviderRequestString(client, req);
        var code = res.response().code();
        if (code != 200) {
            throw new ProviderNotAvailableException("provider is not available");
        }

    }

    @Override
    public MangaInfo[] Search(OkHttpClient client, String kw, String language) {
        var token = JMUtil.GetTokenAndParam();
        var req = GetRequestBuilder("/search?search_query=%s".formatted(URLEncoder.encode(kw, StandardCharsets.UTF_8)), token);

        var res = ProviderUtil.SendProviderRequestString(client, req.build());
        var data = GetRequestData(res, token);

        List<MangaInfo> ret = new ArrayList<>();
        for (var doc : data.GetObjectList("content")) {
            var id = doc.GetString("id");
            var manga = MangaInfo.builder()
                    .id(id)
                    .author(doc.GetString("author"))
                    .title(doc.GetString("name"))
                    .coverUrl(IMAGE_CDN_URL + "/albums/%s_3x4.jpg".formatted(id));
            ret.add(manga.build());
        }

        return ret.toArray(new MangaInfo[0]);
    }

    @Override
    public ResultCacheOptions<MangaDetails> GetMangaDetails(OkHttpClient client, String id, String language) {
        var token = JMUtil.GetTokenAndParam();
        var req = GetRequestBuilder("/album?comicName=&id=%s".formatted(id), token)
                .get();
        var res = ProviderUtil.SendProviderRequestString(client, req.build());
        var data = GetRequestData(res, token);

        var tags = data.GetList("tags", String.class);
        var ret = MangaDetails.builder()
                .id(Integer.toString(data.GetInt("id"))) // id returned by details api is an int
                .title(data.GetString("name"))
                .author(String.join(", ", data.GetList("author", String.class)))
                .description(data.GetString("description"))
                .coverUrl(IMAGE_CDN_URL + "/albums/%s_3x4.jpg".formatted(data.GetInt("id")))
                .contentRating(tags.contains("非H") ? MangaContentRating.ECCHI : MangaContentRating.EROTICA)
                .status(MangaStatus.SINGLE)
                .latestUpdate(new DateTime(Long.parseLong(data.GetString("addtime")) * 1000).toString())
                .latestChapter("")
                .build();
        ret.setChapters(MangaChapterData.Single(ret));

        return new ResultCacheOptions<>(ret, Duration.standardHours(1));
    }

    @Override
    public ResultCacheOptions<ChapterDetails> GetChapterDetails(OkHttpClient client, String mangaId, String chapterId, String language) {
        var token = JMUtil.GetTokenAndParam();
        var req = GetRequestBuilder("/chapter?id=%s".formatted(mangaId), token)
                .get();
        var res = ProviderUtil.SendProviderRequestString(client, req.build());
        var data = GetRequestData(res, token);

        var ret = ChapterDetails.builder()
                .id(Integer.toString(data.GetInt("id")))
                .title(data.GetString("name"))
                .date(new DateTime(Long.parseLong(data.GetString("addtime")) * 1000).toString())
                .images(ChapterImages.From(
                        data.GetList("images", String.class).stream()
                                .map(c -> "/photos/%s/%s".formatted(mangaId, c))
                                .toList()
                )) // images are fetched separately
                .manga(ProviderService.getInstance().GetMangaDetails(this, mangaId, language).entity())
                .build();

        return new ResultCacheOptions<>(ret, Duration.standardHours(1));
//        return new ResultCacheOptions<>(ret, Duration.ZERO);
    }

    @Override
    public ResultCacheOptions<ChapterImages> GetChapterImages(OkHttpClient client, String manga, String chapterId, String language) {
        var scrambleToken = JMUtil.GetTokenAndParam(JMMagicConstants.APP_TOKEN_SECRET_2);
        // scramble data
        var scrambleReq = GetRequestBuilder("/chapter_view_template?id=%s&mode=vertical&page=0&app_img_shunt=1".formatted(manga), scrambleToken).get();
        var scrambleRes = ProviderUtil.SendProviderRequestString(client, scrambleReq.build()); // HTML doc, somehow this endpoint does not return encrypted JSON
        // extract scramble id: var scramble_id = 220980;
        Pattern pattern = Pattern.compile("var scramble_id = (\\d+);");
        var matcher = pattern.matcher(scrambleRes.body());
        if (!matcher.find())
            throw new ProviderRequestException("failed to extract scramble id");

        var idStr = matcher.group(0);
        var scrambleId = idStr.substring(idStr.indexOf("= ") + 2, idStr.length() - 1);

        JsonWrapper extraData = new JsonWrapper();
        extraData.Set("scramble_id", Integer.parseInt(scrambleId));

        var ret = ChapterImages.builder()
                .links(null)
                .extraData(extraData);

        return new ResultCacheOptions<>(ret.build(), Duration.standardDays(7));
    }

    private static long GetTimeStamp() {
        return DateTime.now().getMillis() / 1000;
    }

    private static JsonWrapper GetRequestData(ProviderUtil.ProviderRequestResponseString res, JMUtil.JMTokenAndParam token) {
        try {
            return new JsonWrapper(GetRequestDataRaw(res, token));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new ProviderRequestException("failed to decrypt response data");
        }
    }

    private static String GetRequestDataRaw(ProviderUtil.ProviderRequestResponseString res, JMUtil.JMTokenAndParam token) {
        var json = res.AsJson();
        var data = json.GetString("data");
        if (data == null) {
            throw new ProviderRequestException("failed to get response data: remote responded with: %s".formatted(json.GetString("errorMsg")));
        }

        try {
            return JMUtil.DecryptApiResponse(data, token.timestamp());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ProviderRequestException("failed to decrypt response data");
        }
    }

    private class AuthenticationProvider implements IAuthenticationProvider {
        @Override
        public String Authenticate(OkHttpClient client, Map<String, String> credentials) {
            var token = JMUtil.GetTokenAndParam();
//            log.info("authenticating with %s %s".formatted(credentials.get("username"), credentials.get("password")));

            var formBody = new FormBody.Builder()
                    .add("username", credentials.get("username"))
                    .add("password", credentials.get("password"))
                    .build();

            var req = GetRequestBuilder("/login", token)
                    .post(formBody);
            var res = ProviderUtil.SendProviderRequestString(client, req.build());
            var data = GetRequestData(res, token);

//            log.info(data.toString());

            return data.GetString("s");
        }
    }
}

package moe.protasis.sephirah.provider.manga.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import moe.protasis.sephirah.data.cache.CachedEntity;
import moe.protasis.sephirah.data.manga.*;
import moe.protasis.sephirah.exception.FeatureNotImplementedException;
import moe.protasis.sephirah.exception.NotFoundException;
import moe.protasis.sephirah.exception.provider.ProviderNotAvailableException;
import moe.protasis.sephirah.exception.provider.ProviderRequestException;
import moe.protasis.sephirah.provider.manga.IProxyMangaProvider;
import moe.protasis.sephirah.service.MangaProviderService;
import moe.protasis.sephirah.util.JsonWrapper;
import moe.protasis.sephirah.util.LZString;
import moe.protasis.sephirah.util.ProviderUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.graalvm.polyglot.Context;
import org.joda.time.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class MangaProvider_ManHuaGui implements IProxyMangaProvider {
    private static final String IMAGE_CDN_BASE_URL = "https://eu1.hamreus.com";

    @Override
    public String GetId() {
        return "manhuagui";
    }

    @Override
    public int GetImageCacheLength() {
        return 86400 * 6;
    }

    @Override
    public Request.Builder GetRequestBuilder(String path) {
        return new Request.Builder()
                .get()
                .header("User-Agent", "Mozilla/5.0")
                .url("https://m.manhuagui.com" + path);
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
        var req = GetRequestBuilder("/s/%s.html".formatted(URLEncoder.encode(kw.replace(" ", ""), StandardCharsets.UTF_8)))
                .build();
        var res = ProviderUtil.SendProviderRequestString(client, req);

        // parse document
        Document doc = Jsoup.parse(res.body());
        // get element list
        var list = doc.getElementById("detail");
        if (list == null || list.childrenSize() == 0)
            return new MangaInfo[0];

        // extract manga info
        return list.childNodes().stream()
                .filter(c -> c instanceof Element)
                .map(c -> ParseSingleMangaInfo((Element)c))
                .toArray(MangaInfo[]::new);
    }

    @Override
    public MangaDetails GetMangaDetails(OkHttpClient client, String id, String language) {
        var req = GetRequestBuilder("/comic/%s/".formatted(id))
                .build();
        var res = ProviderUtil.SendProviderRequestString(client, req);
        var ret = MangaDetails.builder();
        ret.id(id);

        if (!res.response().isSuccessful()) {
            throw new ProviderRequestException(60, 502, "provider request failed", new JsonWrapper()
                    .Set("code", res.response().code())
            );
        }

        Document doc = Jsoup.parse(res.body());
        ret.coverUrl("http:" + doc.selectFirst(".thumb").selectFirst("img").attr("src"));
        ret.title(doc.selectFirst(".main-bar").selectFirst("h1").text());

        {
            // status
            String statusText = doc.selectFirst(".thumb").selectFirst("i").text();
            ret.status(statusText.equals("完结") ? MangaStatus.FINISHED : MangaStatus.UPDATING);
        }

        var contentList = doc.selectFirst(".cont-list").select("dl");
        ret.latestChapter(contentList.get(0).selectFirst("dd").text());
        ret.latestUpdate(contentList.get(1).selectFirst("dd").text());
        ret.author(contentList.get(2).selectFirst("a").text());
        ret.description(doc.selectFirst("#bookIntro").selectFirst("p").text());

        {
            // manhuagui has only one group
            var data = MangaChapterData.builder();
            var group = ChapterGroup.builder()
                    .id("default")
                    .name("");

            // parse chapters
            List<ChapterInfo> chapters = new ArrayList<>();
            var li = doc.selectFirst("#chapterList").selectFirst("ul").children();
            int index = li.size() - 1;
            for (var ele : li) {
                var chapter = ChapterInfo.builder()
                        .index(index)
                        .title(ele.selectFirst("b").text());

                var url = ele.selectFirst("a").attr("href");
                chapter.id(url.substring(url.lastIndexOf("/") + 1).replace(".html", ""));

                chapters.add(chapter.build());
                --index;
            }

            Collections.reverse(chapters);
            group.chapters(chapters);
            group.count(chapters.size());

            var g = group.build();
            data.groups(Collections.singletonList(g));
            ret.chapters(data.build());
        }

        return ret.build();
    }

    @Override
    public ChapterDetails GetChapterDetails(OkHttpClient client, String mangaId, String chapterId, String language) {
        var details = MangaProviderService.getInstance().GetMangaDetails(this, mangaId, language);
        if (details == null) {
            throw new NotFoundException();
        }
        var mangaDetails = details.entity();

        var req = GetRequestBuilder("/comic/%s/%s.html".formatted(mangaId, chapterId))
                .build();
        var res = ProviderUtil.SendProviderRequestString(client, req);
        if (!res.response().isSuccessful()) {
            throw new ProviderRequestException(60, 502, "provider request failed", new JsonWrapper()
                    .Set("code", res.response().code())
            );
        }

        Document doc = Jsoup.parse(res.body());
        var ret = ChapterDetails.builder();
        ret.id(chapterId);

        var javascriptText = doc.select("script").stream()
                .filter(c -> !c.hasAttr("src"))
                .findFirst().orElseThrow(() -> new ProviderRequestException("unable to parse manhuagui javascript")).html();
        var chapterData = DecryptChapterData(javascriptText);

        var chapter = mangaDetails.getChapters().GetChapter(chapterId);
        if (chapter == null)
            throw new NotFoundException();
        ret.index(chapter.getIndex());
        ret.manga(mangaDetails);
        ret.title(chapter.getTitle());
        ret.nextChapter(String.valueOf(chapterData.GetInt("nextId")));
        ret.prevChapter(String.valueOf(chapterData.GetInt("prevId")));

        // images
        ret.images(chapterData.GetList("images", String.class));

        return ret.build();
    }

    @Override
    public List<String> GetChapterImages(OkHttpClient client, String manga, String chapterId, String language) {
        throw new FeatureNotImplementedException();
    }

    private JsonWrapper DecryptChapterData(String javascriptText) {
        // '<what we want>'['\x73\x70\x6c\x69\x63']
        Pattern pattern = Pattern.compile("'([^']*?)'\\s*\\['\\\\x73\\\\x70\\\\x6c\\\\x69\\\\x63']");
        Matcher matcher = pattern.matcher(javascriptText);

        if (!matcher.find())
            throw new ProviderRequestException("error parsing lzstring content; no match");
        var content = matcher.group(1);

        //||第01话|webp|whbkdnzka|7072|ps4|jpg|E6||9B|8B|0026|0025||0021|0028|0024|0029|0030|0023|0031||0022|0032|0033|0034|0027|preInit|0019|0035|bookId|32476|bookName|污秽不堪的你最可爱了|chapterId|439477|chapterTitle|0020|nextId|prevId|images|0014|0015|0016|0017|0018|445463|0036|0041|0038|21aStanKeYuz3YRdziFiew|1735175879|sl|block_cc|status|37|count|B0|96|B4|B53|0037|E9|9F|A1|0043|0039|0040||reader|0042|E5|0044|SMH|0046|0047|0048|0049|0045
        var decompressed = LZString.decompressFromBase64(content);
        var data = CallDecodeJavascript(javascriptText, content, decompressed);
//        log.info(data.toString());

        return data;
    }

    private JsonWrapper CallDecodeJavascript(String javascript, String contentOriginal, String contentDecompressed) {
        /**
         * Great thanks to https://wzdlc1996.github.io/artic/other/mangaspider/ for figuring out the decryption process!
         */
        var js = javascript.replace(contentOriginal, contentDecompressed)
                .replace("window[\"\\x65\\x76\\x61\\x6c\"]", "")
                .replace("['\\x73\\x70\\x6c\\x69\\x63']('\\x7c')", ".split('|')");

        try (Context ctx = Context.create();) {
            var value = ctx.eval("js", js);
            return new JsonWrapper(value.asString().replace("SMH.reader(", "").replace(").preInit();", ""));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static MangaInfo ParseSingleMangaInfo(Element ele) {
        var ret = MangaInfo.builder();

        {
            // id
            // /comic/32476/
            var idStr = ele.selectFirst("a").attr("href");
            ret.id(idStr.replace("comic", "").replace("/", ""));
        }

        ret.title(ele.selectFirst("h3").text());
        ret.author(ele.selectFirst("dl").selectFirst("dd").text());
        ret.coverUrl("http:" + ele.selectFirst(".thumb").selectFirst("img").attr("data-src"));

        return ret.build();
    }

}

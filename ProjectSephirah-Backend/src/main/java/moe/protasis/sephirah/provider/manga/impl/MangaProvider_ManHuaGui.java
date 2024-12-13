package moe.protasis.sephirah.provider.manga.impl;

import lombok.extern.slf4j.Slf4j;
import moe.protasis.sephirah.data.manga.*;
import moe.protasis.sephirah.exception.FeatureNotImplementedException;
import moe.protasis.sephirah.exception.provider.ProviderNotAvailableException;
import moe.protasis.sephirah.provider.manga.IProxyMangaProvider;
import moe.protasis.sephirah.util.ProviderUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class MangaProvider_ManHuaGui implements IProxyMangaProvider {
    @Override
    public String GetId() {
        return "manhuagui";
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
    public MangaInfo[] Search(OkHttpClient client, String kw) {
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

            data.groups(Collections.singletonList(group.build()));
            ret.chapters(data.build());
        }

        return ret.build();
    }

    @Override
    public ChapterInfo GetChapterDetails(OkHttpClient client, String mangaId, String chapterId, String language) {
        throw new FeatureNotImplementedException();
    }

    @Override
    public List<String> GetChapterImages(OkHttpClient client, String manga, String chapterId, String language) {
        throw new FeatureNotImplementedException();
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

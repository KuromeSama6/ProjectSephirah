package moe.protasis.sephirah.provider.manga.impl;

import lombok.extern.slf4j.Slf4j;
import moe.protasis.sephirah.data.manga.*;
import moe.protasis.sephirah.exception.FeatureNotImplementedException;
import moe.protasis.sephirah.exception.provider.ProviderNotAvailableException;
import moe.protasis.sephirah.exception.provider.ProviderRequestException;
import moe.protasis.sephirah.provider.manga.IProxyMangaProvider;
import moe.protasis.sephirah.provider.manga.mangadex.MangadexEntity;
import moe.protasis.sephirah.util.JsonWrapper;
import moe.protasis.sephirah.util.ProviderUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class MangaProvider_Mangadex implements IProxyMangaProvider {
    @Override
    public String GetId() {
        return "mangadex";
    }

    @Override
    public Request.Builder GetRequestBuilder(String path) {
        return new Request.Builder()
                .get()
                .header("Referer", "https://mangadex.org/")
                .header("Origin", "https://mangadex.org/")
                .url("https://api.mangadex.org" + path);
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
    public MangaInfo[] Search(OkHttpClient client, String kw, String lang) {
        var langauge = GetMappedLanguageCode(lang);
        var req = GetRequestBuilder("/manga?title=%s&limit=20&contentRating[]=safe&contentRating[]=suggestive&contentRating[]=erotica&includes[]=cover_art&availableTranslatedLanguage[]=%s&hasAvailableChapters=true&order[relevance]=desc".formatted(
                URLEncoder.encode(kw, StandardCharsets.UTF_8),
                langauge
        )).build();
//        log.info(req.url().toString());

        var res = ProviderUtil.SendProviderRequestString(client, req);

        if (res.response().code() != 200) {
            throw new ProviderRequestException("request failed");
        }

        var data = res.AsJson();
//        log.info(data.toString());

        var entities = data.GetObjectList("data").stream()
                .map(MangadexEntity::new)
                .toList();

        List<MangaInfo> ret = new ArrayList<>();
        for (var entity : entities) {
            ret.add(GetMangaInfo(entity, langauge));
        }

        return ret.toArray(new MangaInfo[0]);
    }

    @Override
    public MangaDetails GetMangaDetails(OkHttpClient client, String id, String lang) {
        var language = GetMappedLanguageCode(lang);
        var req = GetRequestBuilder("/manga/%s?contentRating[]=safe&contentRating[]=suggestive&contentRating[]=erotica&includes[]=artist&includes[]=author&includes[]=cover_art".formatted(id))
                .build();
        var res = ProviderUtil.SendProviderRequestString(client, req);
        var entity = new MangadexEntity(res.AsJson().GetObject("data"));
        var attr = entity.getAttributes();

        var ret = MangaDetails.builder()
                .id(entity.getId())
                .title(GetLocalizedTitle(language, entity))
                .coverUrl(GetMangaCoverUrl(entity))
                .description(attr.GetString("description." + language))
                .status(GetMappedStatus(attr.GetString("status")))
                .latestUpdate(attr.GetString("updatedAt"))
                .latestChapter(attr.GetString("lastChapter"))
                .contentRating(GetMappedContentRating(attr.GetString("contentRating")))
                .chapters(GetMangaChapters(client, id, lang));

        return ret.build();
    }

    @Override
    public ChapterDetails GetChapterDetails(OkHttpClient client, String mangaId, String chapterId, String language) {
        var req = GetRequestBuilder("/chapter/%s?includes[]=manga&includes[]=scanlation_group".formatted(chapterId))
                .build();
        var res = ProviderUtil.SendProviderRequestString(client, req);
        var entity = new MangadexEntity(res.AsJson().GetObject("data"));
        var attr = entity.getAttributes();

//        log.info(attr.toString());
        var ret = ChapterDetails.builder()
                .id(entity.getId())
                .date(attr.GetString("updatedAt"))
                .title(GetChapterTitle(entity))
                .manga(GetMangaInfo(entity.GetRelationship("manga"), language));

        // get images
        {
            var imagesReq = GetRequestBuilder("/at-home/server/%s".formatted(chapterId))
                    .build();
            var imagesRes = ProviderUtil.SendProviderRequestString(client, imagesReq);
            var imagesData = imagesRes.AsJson();

            List<String> images = imagesData.GetList("chapter.data", String.class).stream()
                    .map(c -> "%s/data/%s/%s".formatted(
                            imagesData.GetString("baseUrl"),
                            imagesData.GetString("chapter.hash"),
                            c
                    ))
                    .toList();
            ret.images(images);
        }

        // get prev and next
        {
            var chapters = GetMangaChapters(client, mangaId, language);
            var group = chapters.getGroups().stream()
                    .filter(c -> c.getChapters().stream().anyMatch(c2 -> c2.getId().equals(chapterId)))
                    .findFirst().orElse(null);
            if (group != null) {
                int index = group.getChapters().stream()
                        .filter(c -> c.getId().equals(chapterId))
                        .findFirst().map(ChapterInfo::getIndex).orElse(-1);

                if (index > 0) {
                    ret.prevChapter(group.getChapters().get(index - 1).getId());
                }
                if (index < group.getChapters().size() - 1) {
                    ret.nextChapter(group.getChapters().get(index + 1).getId());
                }
            }
        }

        return ret.build();
    }

    @Override
    public List<String> GetChapterImages(OkHttpClient client, String manga, String chapterId, String language) {
        throw new FeatureNotImplementedException();
    }

    private MangaInfo GetMangaInfo(MangadexEntity entity, String lang) {
        return MangaInfo.builder()
                .id(entity.getId())
                .title(GetLocalizedTitle(lang, entity))
                .coverUrl(GetMangaCoverUrl(entity))
                .build();
    }

    private MangaChapterData GetMangaChapters(OkHttpClient client, String id, String language) {
        var req = GetRequestBuilder("/manga/%s/feed?limit=500&offset=0&includes[]=scanlation_group&includes[]=user&order[volume]=asc&order[chapter]=asc&contentRating[]=safe&contentRating[]=suggestive&contentRating[]=pornographic&contentRating[]=erotica&translatedLanguage[]=%s".formatted(id, language))
                .build();
        var res = ProviderUtil.SendProviderRequestString(client, req);
        var data = res.AsJson().GetObjectList("data");

        Map<String, ChapterGroup> groups = new HashMap<>();

        for (var obj : data) {
            var entity = new MangadexEntity(obj);
            var attr = entity.getAttributes();

            var scanlationGroup = entity.GetRelationship("scanlation_group");
            var groupId = scanlationGroup == null ? "default" : scanlationGroup.getId();
            if (!groups.containsKey(groupId)) {
                var group = ChapterGroup.builder()
                        .id(groupId)
                        .chapters(new ArrayList<>())
                        .name(scanlationGroup == null ? "Default" : scanlationGroup.getAttributes().GetString("name"))
                        .count(0);
                groups.put(groupId, group.build());
            }

            var group = groups.get(groupId);

            // chapter
            var chapter = ChapterInfo.builder()
                    .id(entity.getId())
                    .index(group.getCount())
                    .title(GetChapterTitle(entity))
                    .date(attr.GetString("createdAt"));

            group.setCount(group.getCount() + 1);
            group.getChapters().add(chapter.build());
        }

        return MangaChapterData.builder()
                .groups(new ArrayList<>(groups.values()))
                .build();
    }

    private static String GetChapterTitle(MangadexEntity entity) {
        var attr = entity.getAttributes();
        var extraTitle = attr.GetString("title");
        return attr.GetString("chapter") + (extraTitle != null && !extraTitle.isEmpty() ? (" " + extraTitle) : "");
    }

    private static String GetLocalizedTitle(String language, MangadexEntity entity) {
        var titles = entity.getAttributes().GetObject("title");
        if (titles.Has(language)) {
            return titles.GetString(language);
        }

        var altTitles = entity.getAttributes().GetObjectList("altTitles");
        var candidate = altTitles.stream()
                .filter(c -> c.Has(language))
                .findFirst().orElse(null);
        if (candidate != null) {
            return candidate.GetString(language);
        }

        return titles.GetString("en");
    }

    private static String GetMangaCoverUrl(MangadexEntity entity) {
        var rel = entity.GetRelationship("cover_art");
        if (rel == null) return null;

        var fileName = rel.getAttributes().GetString("fileName");
        return "/covers/%s/%s.256.jpg".formatted(entity.getId(), fileName);
    }

    private static String GetMappedLanguageCode(String code) {
        return switch (code) {
            case "en" -> "en";
            case "jp" -> "ja";
            default -> code;
        };
    }

    private static MangaStatus GetMappedStatus(String status) {
        return switch (status) {
            case "ongoing" -> MangaStatus.UPDATING;
            case "completed" -> MangaStatus.FINISHED;
            case "hiatus" -> MangaStatus.PAUSED;
            case "cancelled" -> MangaStatus.TERMINATED;
            default -> MangaStatus.UNKNOWN;
        };
    }

    private static MangaContentRating GetMappedContentRating(String rating) {
        return switch (rating) {
            case "erotica" -> MangaContentRating.EROTICA;
            case "pornographic" -> MangaContentRating.ECCHI;
            default -> MangaContentRating.GENERAL;
        };
    }
}

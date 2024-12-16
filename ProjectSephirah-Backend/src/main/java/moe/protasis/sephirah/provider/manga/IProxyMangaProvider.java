package moe.protasis.sephirah.provider.manga;

import moe.protasis.sephirah.data.manga.ChapterDetails;
import moe.protasis.sephirah.data.manga.MangaDetails;
import moe.protasis.sephirah.data.manga.MangaInfo;
import moe.protasis.sephirah.provider.IProxyProvider;
import okhttp3.OkHttpClient;

import java.util.List;

public interface IProxyMangaProvider extends IProxyProvider {
    default int GetImageCacheLength() {
        return 0;
    }

    MangaInfo[] Search(OkHttpClient client, String kw, String language);
    MangaDetails GetMangaDetails(OkHttpClient client, String id, String language);
    ChapterDetails GetChapterDetails(OkHttpClient client, String mangaId, String chapterId, String language);
    List<String> GetChapterImages(OkHttpClient client, String manga, String chapterId, String language);
}

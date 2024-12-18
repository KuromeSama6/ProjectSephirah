package moe.protasis.sephirah.provider.manga;

import moe.protasis.sephirah.data.cache.ResultCacheOptions;
import moe.protasis.sephirah.data.manga.ChapterDetails;
import moe.protasis.sephirah.data.manga.ChapterImages;
import moe.protasis.sephirah.data.manga.MangaDetails;
import moe.protasis.sephirah.data.manga.MangaInfo;
import moe.protasis.sephirah.exception.FeatureNotImplementedException;
import moe.protasis.sephirah.provider.IProxyProvider;
import okhttp3.OkHttpClient;

public interface IProxyMangaProvider extends IProxyProvider {
    default int GetImageCacheLength() {
        return 0;
    }

    MangaInfo[] Search(OkHttpClient client, String kw, String language);
    ResultCacheOptions<MangaDetails> GetMangaDetails(OkHttpClient client, String id, String language);
    ResultCacheOptions<ChapterDetails> GetChapterDetails(OkHttpClient client, String mangaId, String chapterId, String language);
    default ResultCacheOptions<ChapterImages> GetChapterImages(OkHttpClient client, String manga, String chapterId, String language) {
        throw new FeatureNotImplementedException();
    }
}

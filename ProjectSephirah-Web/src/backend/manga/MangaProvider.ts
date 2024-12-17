import { Provider } from "../provider/Provider.ts";
import { ChapterDetails, ChapterInfo, MangaContentRating, MangaDetails, MangaInfo, MangaStatus } from "./Manga.ts";
import { APIUtil } from "../api/APIUtil.ts";
import { SupportedLanguage } from "../common/Language.ts";
import axios from "axios";

export interface MangaProvider extends Provider {
    // Manga Source Data
    readonly info: MangaProviderInfo;

    // Methods
    Search(kw: string, language: SupportedLanguage): Promise<MangaInfo[] | null>;

    GetMangaDetails(id: string, language: SupportedLanguage): Promise<MangaDetails | null>;

    GetChapterDetails(mangaId: string, chapterId: string, language: SupportedLanguage): Promise<ChapterDetails | null>;
}

export interface MangaProviderInfo {
    name: string;
    website: string;
    isHentaiDedicated: boolean;
    primaryLanguage: SupportedLanguage;
    supportedSearchLanguages: Array<SupportedLanguage>;
}

export abstract class ProxiedMangaProvider implements MangaProvider {
    abstract readonly id: string;
    abstract readonly info: MangaProviderInfo;

    async Search(kw: string, language: SupportedLanguage): Promise<MangaInfo[] | null> {
        const res = await APIUtil.SendRequest("get", `/api/provider/${this.id}/proxy/search`, {}, {
            kw: kw,
            lang: language,
        });
        if (!res.success) return null;

        const ret = res.data!.results as MangaInfo[];
        for (let mangaInfo of ret) {
            mangaInfo.provider = this;
        }
        return ret;
    }

    async CheckAvailability(): Promise<boolean> {
        const ret = await APIUtil.SendRequest("get", `/api/provider/${this.id}/proxy/status`);
        return ret.success;
    }

    async GetMangaDetails(id: string, language: SupportedLanguage): Promise<MangaDetails | null> {
        const res = await APIUtil.SendRequest("get", `/api/provider/${this.id}/proxy/manga/${id}?lang=${language}`);
        if (!res.success) return null;

        const data = res.data?.details;
        return {
            ...data,
            provider: this,
            latestUpdate: new Date(data.latestUpdate),
            chapters: () => data.chapters,
            status: MangaStatus[data.status],
            contentRating: MangaContentRating[data.contentRating],
        };
    }

    async GetChapterDetails(mangaId: string, chapterId: string, language: SupportedLanguage): Promise<ChapterDetails | null> {
        const res = await APIUtil.SendRequest("get", `/api/provider/${this.id}/proxy/manga/${mangaId}/${chapterId}?lang=${language}`);
        if (!res.success) return null;
        const data = res.data.details;

        // test
        // console.log(res.data.image_cache_length);

        return {
            ...data,
            provider: this,
            imageCacheLength: res.data.image_cache_length,
            images: () => data.images,
        };
    }

    async GetChapterImages(manga: string, chapter: string, language: SupportedLanguage): Promise<string[] | null> {
        const res = await APIUtil.SendRequest("get", `/api/provider/${this.id}/proxy/manga/${manga}/${chapter}/images?lang=${language}`);
        if (!res.success) return null;

        return res.data.images;
    }

}
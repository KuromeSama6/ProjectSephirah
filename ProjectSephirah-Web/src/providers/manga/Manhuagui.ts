import { MangaProvider, ProxiedMangaProvider } from "../../backend/manga/MangaProvider.ts";
import { ChapterDetails, MangaInfo } from "../../backend/manga/Manga.ts";
import RequestUtil from "../../backend/util/RequestUtil.ts";
import axios, { HttpStatusCode } from "axios";
import { APIUtil } from "../../backend/api/APIUtil.ts";
import { SupportedLanguage } from "../../backend/common/Language.ts";
import { SephirahAPI } from "../../backend/api/SephirahAPI.ts";

export default class MangaProviderManhuagui extends ProxiedMangaProvider {
    readonly id = "manhuagui";
    readonly info = {
        name: "看漫画",
        website: "https://www.manhuagui.com/",
        isHentaiDedicated: false,
        primaryLanguage: "cn" as SupportedLanguage,
        supportedSearchLanguages: ["cn"] as SupportedLanguage[],
    };

    async GetChapterDetails(mangaId: string, chapterId: string, language: SupportedLanguage): Promise<ChapterDetails | null> {
        const data = await super.GetChapterDetails(mangaId, chapterId, language);
        const images = await data?.images();
        if (images == null) return null;

        return {
            ...data!,
            provider: this,
            images: () => ({
                links: images.links.map((c: string) => SephirahAPI.BASE_URL + `/api/provider/manhuagui/proxy/image?uri=${encodeURIComponent(c)}`),
                extraData: {},
            }),
        };
    }
}
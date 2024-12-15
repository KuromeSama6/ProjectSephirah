import { MangaProvider } from "../../backend/manga/MangaProvider.ts";
import { ChapterDetails, MangaDetails, MangaInfo } from "../../backend/manga/Manga.ts";
import RequestUtil from "../../backend/util/RequestUtil.ts";
import { SupportedLanguage } from "../../backend/common/Language.ts";

export default class MangaProviderMangadex implements MangaProvider {
    readonly id = "mangadex";
    readonly info = {
        name: "Mangadex",
        website: "https://mangadex.org",
        hentaiDedicated: false,
        primaryLanguage: "en" as SupportedLanguage,
        supportedSearchLanguages: ["en", "jp"] as SupportedLanguage[],
    };

    async Search(kw: string, language: SupportedLanguage): Promise<MangaInfo[]> {
        return [];
    }

    async CheckAvailability(): Promise<boolean> {
        return await RequestUtil.CheckUrlAvailability("https://mangadex.org/");
    }

    async GetChapterDetails(mangaId: string, chapterId: string, language: SupportedLanguage): Promise<ChapterDetails | null> {
        return null;
    }

    async GetMangaDetails(id: string, language: SupportedLanguage): Promise<MangaDetails | null> {
        return null;
    }
}
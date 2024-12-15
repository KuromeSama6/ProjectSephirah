import { MangaProvider } from "../../backend/manga/MangaProvider.ts";
import { ChapterDetails, MangaDetails, MangaInfo } from "../../backend/manga/Manga.ts";
import RequestUtil from "../../backend/util/RequestUtil.ts";
import { SupportedLanguage } from "../../backend/common/Language.ts";

export default class MangaProviderNHentai implements MangaProvider {
    readonly id = "nhentai";
    readonly info = {
        name: "nhentai",
        website: "https://nhentai.net",
        hentaiDedicated: true,
        primaryLanguage: "en" as SupportedLanguage,
        supportedSearchLanguages: ["en", "jp"] as SupportedLanguage[],
    };

    async CheckAvailability(): Promise<boolean> {
        return await RequestUtil.CheckUrlAvailability("https://nhentai.net/");
    }

    async Search(query: string): Promise<MangaInfo[]> {
        return [];
    }

    async GetChapterDetails(mangaId: string, chapterId: string, language: SupportedLanguage): Promise<ChapterDetails | null> {
        return null;
    }

    async GetMangaDetails(id: string, language: SupportedLanguage): Promise<MangaDetails | null> {
        return null;
    }
}
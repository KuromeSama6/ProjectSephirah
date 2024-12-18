import { MangaProvider } from "../../backend/manga/MangaProvider.ts";
import { ChapterDetails, MangaDetails, MangaInfo } from "../../backend/manga/Manga.ts";
import RequestUtil from "../../backend/util/RequestUtil.ts";
import { SupportedLanguage } from "../../backend/common/Language.ts";

export default class MangaProviderManganato implements MangaProvider {
    readonly id = "manganato";
    readonly info = {
        name: "Manganato (WIP)",
        website: "https://manganato.com",
        isHentaiDedicated: false,
        primaryLanguage: "en" as SupportedLanguage,
        supportedSearchLanguages: ["en"] as SupportedLanguage[],
    };

    async CheckAvailability(): Promise<boolean> {
        return await RequestUtil.CheckUrlAvailability("https://manganato.com/");
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
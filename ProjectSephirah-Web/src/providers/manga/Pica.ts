import { MangaProvider } from "../../backend/manga/MangaProvider.ts";
import { ChapterDetails, MangaDetails, MangaInfo } from "../../backend/manga/Manga.ts";
import RequestUtil from "../../backend/util/RequestUtil.ts";
import { SupportedLanguage } from "../../backend/common/Language.ts";

export default class MangaProviderPica implements MangaProvider {
    readonly id = "pica";
    readonly info = {
        name: "哔咔",
        website: "https://manhuapica.com/",
        hentaiDedicated: true,
        primaryLanguage: "cn" as SupportedLanguage,
        supportedSearchLanguages: ["cn"] as SupportedLanguage[],
    };

    async Search(kw: string, language: SupportedLanguage): Promise<MangaInfo[]> {
        return [];
    }

    async CheckAvailability(): Promise<boolean> {
        return await RequestUtil.CheckUrlAvailability("https://manhuapica.com/");
    }

    async GetChapterDetails(mangaId: string, chapterId: string, language: SupportedLanguage): Promise<ChapterDetails | null> {
        return null;
    }

    async GetMangaDetails(id: string, language: SupportedLanguage): Promise<MangaDetails | null> {
        return null;
    }
}
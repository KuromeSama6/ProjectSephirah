import { MangaProvider } from "../../backend/manga/MangaProvider.ts";
import { MangaInfo } from "../../backend/manga/Manga.ts";
import RequestUtil from "../../backend/util/RequestUtil.ts";

export default class MangaProviderMangadex implements MangaProvider {
    readonly id = "mangadex";
    readonly info = {
        name: "Mangadex",
        website: "https://mangadex.org",
        hentaiDedicated: false,
        primaryLanguage: "en",
        supportedSearchLanguages: ["en", "jp"],
    };

    async Search(kw: string, language: SupportedLanguage): Promise<MangaInfo[]> {
        return [];
    }

    async CheckAvailability(): Promise<boolean> {
        return await RequestUtil.CheckUrlAvailability("https://mangadex.org/");
    }
}
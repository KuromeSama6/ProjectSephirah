import { MangaProvider } from "../../backend/manga/MangaProvider.ts";
import { MangaInfo } from "../../backend/manga/Manga.ts";
import RequestUtil from "../../backend/util/RequestUtil.ts";

export default class MangaProviderNHentai implements MangaProvider {
    readonly id = "nhentai";
    readonly info = {
        name: "nhentai",
        website: "https://nhentai.net",
        hentaiDedicated: true,
        primaryLanguage: "intl",
        supportedSearchLanguages: ["en", "jp"],
    };

    async CheckAvailability(): Promise<boolean> {
        return await RequestUtil.CheckUrlAvailability("https://nhentai.net/");
    }

    async Search(query: string): Promise<MangaInfo[]> {
        return [];
    }
}
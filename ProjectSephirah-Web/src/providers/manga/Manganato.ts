import { MangaProvider } from "../../backend/manga/MangaProvider.ts";
import { MangaInfo } from "../../backend/manga/Manga.ts";
import RequestUtil from "../../backend/util/RequestUtil.ts";

export default class MangaProviderManganato implements MangaProvider {
    readonly id = "manganato";
    readonly info = {
        name: "Manganato",
        website: "https://manganato.com",
        hentaiDedicated: false,
        primaryLanguage: "en",
        supportedSearchLanguages: ["en"],
    };

    async CheckAvailability(): Promise<boolean> {
        return await RequestUtil.CheckUrlAvailability("https://manganato.com/");
    }

    async Search(query: string): Promise<MangaInfo[]> {
        return [];
    }
}
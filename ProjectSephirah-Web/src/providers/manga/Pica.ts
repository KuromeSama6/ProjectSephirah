import { MangaProvider } from "../../backend/manga/MangaProvider.ts";
import { MangaInfo } from "../../backend/manga/Manga.ts";
import RequestUtil from "../../backend/util/RequestUtil.ts";

export default class MangaProviderPica implements MangaProvider {
    readonly id = "pica";
    readonly info = {
        name: "哔咔",
        website: "https://manhuapica.com/",
        hentaiDedicated: true,
        primaryLanguage: "cn",
        supportedSearchLanguages: ["cn"],
    };

    async Search(kw: string, language: SupportedLanguage): Promise<MangaInfo[]> {
        return [];
    }

    async CheckAvailability(): Promise<boolean> {
        return await RequestUtil.CheckUrlAvailability("https://manhuapica.com/");
    }
}
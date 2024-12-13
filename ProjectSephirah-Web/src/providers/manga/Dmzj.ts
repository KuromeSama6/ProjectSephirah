import { MangaProvider } from "../../backend/manga/MangaProvider.ts";
import { MangaInfo } from "../../backend/manga/Manga.ts";
import RequestUtil from "../../backend/util/RequestUtil.ts";

export default class MangaProviderDmzj implements MangaProvider {
    readonly id = "dmzj";
    readonly info = {
        name: "动漫之家",
        website: "https://www.idmzj.com/",
        hentaiDedicated: false,
        primaryLanguage: "cn",
        supportedSearchLanguages: ["cn"],
    };

    async Search(kw: string, language: SupportedLanguage): Promise<MangaInfo[]> {
        return [];
    }

    async CheckAvailability(): Promise<boolean> {
        return await RequestUtil.CheckUrlAvailability("https://www.idmzj.com/");
    }
}
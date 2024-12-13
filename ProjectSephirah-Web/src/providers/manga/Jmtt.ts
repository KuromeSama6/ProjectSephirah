import { MangaProvider } from "../../backend/manga/MangaProvider.ts";
import { MangaInfo } from "../../backend/manga/Manga.ts";
import RequestUtil from "../../backend/util/RequestUtil.ts";
import { SupportedLanguage } from "../../backend/common/Language.ts";

export default class MangaProviderJmtt implements MangaProvider {
    readonly id = "jmtt";
    readonly info = {
        name: "禁漫",
        website: "https://18comic.vip/",
        hentaiDedicated: true,
        primaryLanguage: "cn",
        supportedSearchLanguages: ["cn"],
    };

    async Search(kw: string, language: SupportedLanguage): Promise<MangaInfo[]> {
        return [];
    }

    async CheckAvailability(): Promise<boolean> {
        return await RequestUtil.CheckUrlAvailability("https://18comic.vip/");
    }
}
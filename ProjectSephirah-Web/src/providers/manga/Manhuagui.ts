import { MangaProvider, ProxiedMangaProvider } from "../../backend/manga/MangaProvider.ts";
import { MangaInfo } from "../../backend/manga/Manga.ts";
import RequestUtil from "../../backend/util/RequestUtil.ts";
import axios, { HttpStatusCode } from "axios";
import { APIUtil } from "../../backend/api/APIUtil.ts";
import { SupportedLanguage } from "../../backend/common/Language.ts";

export default class MangaProviderManhuagui extends ProxiedMangaProvider {
    readonly id = "manhuagui";
    readonly info = {
        name: "看漫画",
        website: "https://www.manhuagui.com/",
        hentaiDedicated: false,
        primaryLanguage: "cn" as SupportedLanguage,
        supportedSearchLanguages: ["cn"] as SupportedLanguage[],
    };
}
import { MangaProvider } from "../../backend/manga/MangaProvider.ts";
import { ChapterDetails, MangaChapters, MangaDetails, MangaInfo, MangaStatus } from "../../backend/manga/Manga.ts";
import RequestUtil from "../../backend/util/RequestUtil.ts";
import { SupportedLanguage } from "../../backend/common/Language.ts";
import axios from "axios";

const mangaStatusMapping: Record<string, MangaStatus> = {
    "连载中": MangaStatus.UPDATING,
    "已完结": MangaStatus.FINISHED,
};

export default class MangaProviderDmzj implements MangaProvider {
    readonly id = "dmzj";
    readonly info = {
        name: "动漫之家",
        website: "https://www.idmzj.com/",
        isHentaiDedicated: false,
        primaryLanguage: "cn" as SupportedLanguage,
        supportedSearchLanguages: ["cn"] as SupportedLanguage[],
    };

    async CheckAvailability(): Promise<boolean> {
        return await RequestUtil.CheckUrlAvailability("https://www.idmzj.com/");
    }

    async Search(kw: string, language: SupportedLanguage): Promise<MangaInfo[]> {
        const res = await axios.get(`https://www.idmzj.com/api/v1/comic1/search`, {
            params: {
                keyword: kw,
            },
        });
        const docs = res.data.data.comic_list;

        const ret: MangaInfo[] = [];
        for (const doc of docs) {
            ret.push({
                provider: this,
                id: doc.comic_py,
                title: doc.name,
                coverUrl: doc.cover,
                author: doc.authors,
            });
        }

        return ret;
    }

    async GetMangaDetails(id: string, language: SupportedLanguage): Promise<MangaDetails | null> {
        const res = await axios.get(`https://www.idmzj.com/api/v1/comic1/comic/detail`, {
            params: {
                comic_py: id,
            },
        });
        const data = res.data.data.comicInfo;
        const canRead = data.canRead;

        return {
            provider: this,
            id: data.comicPy,
            title: data.title,
            coverUrl: data.cover,
            author: data.authorInfo.authorName,
            status: canRead ? mangaStatusMapping[data.status] || MangaStatus.UNKNOWN : MangaStatus.DMCA_TAKEDOWN,
            description: data.description,
            latestUpdate: new Date(data.lastUpdateTime * 1000),
            latestChapter: data.lastUpdateChapterName,
            chapters: () => ({ groups: [] }),
        };
    }

    async GetChapterDetails(mangaId: string, chapterId: string, language: SupportedLanguage): Promise<ChapterDetails | null> {
        const res = await axios.get(`https://www.idmzj.com/api/v1/comic1/chapter/detail`, {
            params: {
                comid_id: mangaId,
                chapter_id: chapterId,
            },
        });
        return null;
    }

}
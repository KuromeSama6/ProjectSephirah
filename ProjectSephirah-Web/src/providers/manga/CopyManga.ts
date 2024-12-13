import { MangaProvider } from "../../backend/manga/MangaProvider.ts";
import { ChapterDetails, ChapterGroup, MangaChapters, MangaDetails, MangaInfo, MangaStatus } from "../../backend/manga/Manga.ts";
import axios, { HttpStatusCode } from "axios";
import { SupportedLanguage } from "../../backend/common/Language.ts";
import { APIUtil } from "../../backend/api/APIUtil.ts";

export default class MangaProviderCopyManga implements MangaProvider {
    readonly id = "copymanga";
    readonly info = {
        name: "拷贝漫画",
        website: "https://mangacopy.com",
        hentaiDedicated: false,
        primaryLanguage: "cn" as SupportedLanguage,
        supportedSearchLanguages: ["cn"] as SupportedLanguage[],
    };
    readonly baseUrl = "https://api.mangacopy.com/api/v3";

    async Search(kw: string, language: SupportedLanguage): Promise<MangaInfo[]> {
        const res = (
            await axios.get(this.baseUrl + "/search/comic", {
                headers: {
                    platform: 1,
                },
                params: {
                    platform: 1,
                    q: kw,
                    limit: 20,
                    offset: 0,
                    q_type: "",
                    _update: true,
                },
            })
        );

        const list: CopyMangaSearchResult[] = res.data.results.list;

        return list.map((c): MangaInfo => ({
            provider: this,
            id: c.path_word,
            title: c.name,
            author: c.author.map(c => c.name).join(", "),
            coverUrl: c.cover,
        }));
    }

    async CheckAvailability(): Promise<boolean> {
        try {
            const ret = await axios.get("https://api.mangacopy.com/api/v3/h5/homeIndex");
            return ret.status == HttpStatusCode.Ok;

        } catch (error: any) {
            return false;
        }
    }

    async GetMangaDetails(id: string, language: SupportedLanguage): Promise<MangaDetails | null> {
        const ret = await axios.get(`https://api.mangacopy.com/api/v3/comic2/${id}`, {
            headers: {
                platform: 1,
            },
            params: {
                platform: 1,
                _update: true,
            },
        });

        const results = ret.data.results;
        const data = results.comic;

        return {
            provider: this,
            id: data.path_word,
            title: data.name,
            coverUrl: data.cover,
            author: data.author.map((c: CopyMangaAuthor) => c.name).join(", "),
            description: data.brief,
            status: results.is_banned ? MangaStatus.DMCA_TAKEDOWN : data.status.value,
            latestUpdate: new Date(data.datetime_updated),
            latestChapter: data.last_chapter.name,
            chapters: () => this.GetChaptersInternal(id, Object.values(ret.data.results.groups)),
        };
    }

    async GetChaptersInternal(id: string, groups: CopyMangaGroup[]): Promise<MangaChapters> {
        const ret: ChapterGroup[] = [];
        for (const group of groups) {
            const res = await axios.get(
                `https://api.mangacopy.com/api/v3/comic/${id}/group/${group.path_word}/chapters`,
                {
                    headers: {
                        platform: 1,
                    },
                    params: {
                        limit: 500,
                        offset: 0,
                        _update: true,
                    },
                },
            );

            const results = res.data.results;
            const list = results.list;

            const chapterGroup: ChapterGroup = {
                id: group.path_word,
                name: group.name,
                count: group.count,
                chapters: [],
            };

            for (const chapter of list) {
                chapterGroup.chapters.push({
                    provider: this,
                    id: chapter.uuid,
                    index: chapter.index,
                    date: new Date(chapter.datetime_created),
                    title: chapter.name,
                });
            }

            ret.push(chapterGroup);
        }

        // console.log(ret);

        return {
            groups: ret,
        };
    }

    async GetChapterDetails(mangaId: string, chapterId: string, language: SupportedLanguage): Promise<ChapterDetails | null> {
        const res = await axios.get(
            `https://api.mangacopy.com/api/v3/comic/${mangaId}/chapter2/${chapterId}`,
            {
                headers: {
                    platform: 1,
                },
                params: {
                    platform: 1,
                    _update: true,
                },
            },
        );

        const data = res.data.results;

        return {
            provider: this,
            manga: {
                provider: this,
                id: data.comic.path_word,
                title: data.comic.name,
                author: "",
                coverUrl: "",
            },
            id: data.chapter.uuid,
            date: new Date(data.chapter.datetime_created),
            index: data.chapter.index,
            title: data.chapter.name,
            nextChapter: data.chapter.next,
            prevChapter: data.chapter.prev,

            images: () => this.GetChapterImages(mangaId, chapterId, language),
        };
    }

    async GetChapterImages(manga: string, chapter: string, language: SupportedLanguage): Promise<string[] | null> {
        const res = await APIUtil.SendRequest("get", `/api/provider/${this.id}/proxy/manga/${manga}/${chapter}/images?lang=${language}`);
        if (!res.success) return null;

        return res.data.images;
    }

}

interface CopyMangaSearchResult {
    name: string;
    alias: string;
    path_word: string;
    cover: string;
    ban: number;
    img_type: number;
    author: CopyMangaAuthor[];
    popular: number;
}

interface CopyMangaAuthor {
    name: string;
    alias?: string;
    path_word: string;
}

interface CopyMangaGroup {
    count: number;
    name: string;
    path_word: string;
}
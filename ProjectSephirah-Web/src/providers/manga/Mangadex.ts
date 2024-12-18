import { MangaProvider, ProxiedMangaProvider } from "../../backend/manga/MangaProvider.ts";
import { ChapterDetails, ChapterGroup, ChapterImages, ChapterInfo, MangaChapters, MangaContentRating, MangaDetails, MangaInfo, MangaStatus } from "../../backend/manga/Manga.ts";
import RequestUtil from "../../backend/util/RequestUtil.ts";
import { SupportedLanguage } from "../../backend/common/Language.ts";
import axios from "axios";
import { SephirahAPI } from "../../backend/api/SephirahAPI.ts";
import QueryString from "qs";
import { APIUtil } from "../../backend/api/APIUtil.ts";

const languageCodeMapping: Record<string, string> = {
    "en": "en",
    "jp": "ja",
};

const mangaStatusMapping: Record<string, MangaStatus> = {
    // completed, ongoing, cancelled, hiatus
    completed: MangaStatus.FINISHED,
    ongoing: MangaStatus.UPDATING,
    cancelled: MangaStatus.TERMINATED,
    hiatus: MangaStatus.PAUSED,
};

const mangaContentRatingMapping: Record<string, MangaContentRating> = {
    "safe": MangaContentRating.GENERAL,
    "suggestive": MangaContentRating.GENERAL,
    "erotica": MangaContentRating.EROTICA,
    "ecchi": MangaContentRating.ECCHI,
};

export class MangaProviderMangadexProxied extends ProxiedMangaProvider {
    readonly id = "mangadex";
    readonly info = {
        name: "Mangadex",
        website: "https://mangadex.org",
        isHentaiDedicated: false,
        primaryLanguage: "en" as SupportedLanguage,
        supportedSearchLanguages: ["en", "jp"] as SupportedLanguage[],
    };

    async Search(kw: string, language: SupportedLanguage): Promise<MangaInfo[] | null> {
        const ret = await super.Search(kw, language);
        for (let mangaInfo of ret!) {
            mangaInfo.coverUrl = `${SephirahAPI.BASE_URL}/api/provider/mangadex/proxy/image?uri=${mangaInfo.coverUrl}`;
        }
        return ret;
    }

    async GetMangaDetails(id: string, language: SupportedLanguage): Promise<MangaDetails | null> {
        const ret = await super.GetMangaDetails(id, language);

        return {
            ...ret!,
            coverUrl: `${SephirahAPI.BASE_URL}/api/provider/mangadex/proxy/image?uri=${ret!.coverUrl}`,
        };
    }
}

export default class MangaProviderMangadex implements MangaProvider {
    readonly id = "mangadex";
    readonly info = {
        name: "Mangadex",
        website: "https://mangadex.org",
        isHentaiDedicated: false,
        primaryLanguage: "en" as SupportedLanguage,
        supportedSearchLanguages: ["en", "jp"] as SupportedLanguage[],
    };

    async CheckAvailability(): Promise<boolean> {
        return await RequestUtil.CheckUrlAvailability("https://mangadex.org/");
    }

    async Search(kw: string, searchLanguage: SupportedLanguage): Promise<MangaInfo[]> {
        const language = languageCodeMapping[searchLanguage] || searchLanguage;

        const res = await axios.get(`https://api.mangadex.org/manga`, {
            params: {
                title: kw,
                limit: 20,
                contentRating: ["safe", "suggestive", "erotica"],
                includes: ["cover_art"],
                availableTranslatedLanguage: [language],
                hasAvailableChapters: true,
                order: {
                    relevance: "desc",
                },
            },
        });

        const data = res.data;
        const ret: MangaInfo[] = [];

        for (const entry of data.data) {
            // process data
            const attributes: MDMangaAttributes = entry.attributes;
            const info: Partial<MangaInfo> = {
                id: entry.id,
                provider: this,
                author: "",
                contentRating: mangaContentRatingMapping[attributes.contentRating],
            };

            info.title = GetMangaTitle(attributes, language);
            info.coverUrl = GetCoverUrl(entry.id, entry.relationships);

            // console.log(info);
            ret.push(info as MangaInfo);
        }

        return ret;
    }

    async GetMangaDetails(id: string, searchLanguage: SupportedLanguage): Promise<MangaDetails | null> {
        const language = languageCodeMapping[searchLanguage] || searchLanguage;
        const res = await axios.get(`https://api.mangadex.org/manga/${id}`, {
            params: {
                contentRating: ["safe", "suggestive", "erotica"],
                includes: ["artist", "author", "cover_art"],
            },
        });
        const data = res.data.data;
        const attributes: MDMangaAttributes = data.attributes;

        const ret: Partial<MangaDetails> = {
            provider: this,
            id: data.id,
            title: GetMangaTitle(attributes, language),
            description: attributes.description[language],
            status: mangaStatusMapping[attributes.status],
            latestUpdate: new Date(attributes.updatedAt),
            latestChapter: attributes.lastChapter,
            coverUrl: GetCoverUrl(data.id, data.relationships),
            contentRating: mangaContentRatingMapping[attributes.contentRating],

            chapters: () => this.GetChaptersFeedInternal(id, searchLanguage),
        };

        return ret as MangaDetails;
    }

    async GetChaptersInfoInternal(mangaId: string, language: SupportedLanguage): Promise<MangaChapters> {
        const res = await axios.get(`https://api.mangadex.org/manga/${mangaId}/aggregate`, {
            params: {
                translatedLanguage: [languageCodeMapping[language]],
                groups: [],
            },
        });

        const ret: ChapterGroup = {
            id: "default",
            name: "Default",
            count: 0,
            chapters: [],
        };

        for (const volume of Object.values(res.data.volumes) as any[]) {
            ret.count += volume.count;
            for (const index in volume.chapters) {
                const chapter = volume.chapters[index];
                ret.chapters.push({
                    provider: this,
                    index: parseInt(index),
                    id: chapter.id,
                });
            }
        }

        // test
        {
            const id = ret.chapters[0].id;
            console.log(id);
            const atHome = await axios.get(`https://api.mangadex.org/chapter`, {
                params: {
                    manga: mangaId,
                    includes: ["scanlation_group"],
                },
            });
            console.log(atHome.data);
        }

        return {
            groups: [ret],
        };
    }

    async GetChaptersFeedInternal(mangaId: string, language: SupportedLanguage): Promise<MangaChapters> {
        const res = await axios.get(`https://api.mangadex.org/manga/${mangaId}/feed`, {
            params: {
                limit: 500,
                offset: 0,
                includes: ["scanlation_group", "user"],
                order: {
                    volume: "asc",
                    chapter: "asc",
                },
                contentRating: ["safe", "suggestive", "pornographic", "erotica"],
                translatedLanguage: [languageCodeMapping[language]],
            },
        });
        const data = res.data;
        // console.log(data);

        // process
        const groups: Record<string, ChapterGroup> = {};
        for (const chapter of data.data as MDEntity<MDChapterAttributes>[]) {
            // get scanlation group
            const scanlationGroup = GetRelationship<MDScanlationGroup>(chapter, "scanlation_group")!;

            const groupId = scanlationGroup ? scanlationGroup.id : "default";
            if (!groups[groupId]) {
                groups[groupId] = {
                    id: groupId,
                    name: scanlationGroup ? scanlationGroup.attributes.name! : "Default",
                    count: 0,
                    chapters: [],
                };
            }

            // process chapter
            const group = groups[groupId];
            group.count++;
            const ret: ChapterInfo = {
                provider: this,
                id: chapter.id,
                index: parseInt(chapter.attributes.chapter) - 1,
                title: chapter.attributes.title,
                date: new Date(chapter.attributes.createdAt),
            };

            group.chapters.push(ret);
        }

        return {
            groups: Object.values(groups).sort((a, b) => b.count - a.count),
        };
    }

    async GetChapterDetails(mangaId: string, chapterId: string, language: SupportedLanguage): Promise<ChapterDetails | null> {
        const res = await axios.get(`https://api.mangadex.org/chapter/${chapterId}`, {
            params: {
                includes: ["manga", "scanlation_group"],
            },
        });
        const data: MDEntity<MDChapterAttributes> = res.data.data;
        // console.log(data);
        const manga = GetRelationship(data, "manga")!;

        const ret: Partial<ChapterDetails> = {
            provider: this,
            id: chapterId,
            index: parseInt(data.attributes.chapter) - 1,
            title: data.attributes.title,
            date: new Date(data.attributes.updatedAt),
            manga: {
                provider: this,
                id: manga.id,
                title: GetMangaTitle(manga.attributes, language),
                coverUrl: "",
                contentRating: mangaContentRatingMapping[manga.attributes.contentRating],
                author: "",
            },

            images: () => this.GetChapterImagesInternal(chapterId),
            imageCacheLength: 0,
        };

        // get prev and next
        const mangaChapters = await this.GetChaptersFeedInternal(manga.id, language);
        const group = mangaChapters.groups.find(c => c.chapters.find(c => c.id === chapterId));
        const index = group!.chapters.findIndex(c => c.id === chapterId);
        if (index > 0) {
            ret.prevChapter = group!.chapters[index - 1].id;
        }
        if (index < group!.chapters.length - 1) {
            ret.nextChapter = group!.chapters[index + 1].id;
        }

        return ret as ChapterDetails;
    }

    async GetChapterImagesInternal(chapterId: string): Promise<ChapterImages> {
        const res = await axios.get(`https://api.mangadex.org/at-home/server/${chapterId}`);
        const data = res.data;

        return {
            links: data.chapter.data.map((c: string) => `${data.baseUrl}/data/${data.chapter.hash}/${c}`),
            extraData: {},
        };
    }

}

type MultilanguageString = Record<string, string>;

interface MDEntity<T = any> {
    id: string;
    type: string;
    attributes: T;
    relationships: MDRelationship[];
}

interface MDMangaAttributes {
    title: MultilanguageString;
    altTitles: MultilanguageString[];
    description: MultilanguageString;
    isLocked: boolean;
    originalLanguage: string;
    lastVolume: string;
    lastChapter: string;
    publicationDemographic: any;
    status: string;
    year: number;
    contentRating: string;
    state: string;
    chapterNumbersResetOnNewVolume: boolean;
    createdAt: string;
    updatedAt: string;
    version: number;
    availableTranslatedLanguages: string[];
    latestUploadedChapter: string;
}

interface MDChapterAttributes {
    volume: string;
    chapter: string;
    title: string;
    translatedLanguage: string;
    externalUrl: any;
    publishAt: string;
    readableAt: string;
    createdAt: string;
    updatedAt: string;
    pages: number;
    version: number;
}

interface MDScanlationGroup {
    username?: string;
    roles?: string[];
    version: number;
    name?: string;
    altNames?: any[];
    locked?: boolean;
    website?: string;
    ircServer?: string;
    ircChannel?: string;
    discord?: string;
    contactEmail: any;
    description?: string;
    twitter?: string;
    mangaUpdates: any;
    focusedLanguages?: string[];
    official?: boolean;
    verified?: boolean;
    inactive?: boolean;
    publishDelay: any;
    createdAt?: string;
    updatedAt?: string;
}

interface MDRelationship<T = any> {
    id: string;
    type: string;
    attributes: T;
}

function GetRelationship<T = any>(entity: MDEntity, type: string): MDRelationship<T> | null {
    return entity.relationships.find(c => c.type === type) || null;
}

function GetMangaTitle(attributes: MDMangaAttributes, language: string) {
    // title
    if (attributes.title[language]) {
        return attributes.title[language];
    } else if (attributes.altTitles.find(c => c[language])) {
        return (attributes.altTitles.find(c => c[language])!)[language];
    } else {
        return attributes.title["en"];
    }
}

function GetCoverUrl(id: string, relationships: MDRelationship[]): string {
    const coverArtRelationship = relationships.find(c => c.type === "cover_art");
    const fileName = coverArtRelationship!.attributes.fileName;
    return `https://mangadex.org/covers/${id}/${fileName}.256.jpg`;
}

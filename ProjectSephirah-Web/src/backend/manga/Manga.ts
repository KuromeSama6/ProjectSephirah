import { MangaProvider } from "./MangaProvider.ts";

export type LazyGet<T> = () => Promise<T> | T;

export interface MangaInfo {
    provider: MangaProvider;
    id: string;
    title: string;
    author: string;
    coverUrl: string;
    contentRating?: MangaContentRating;
}

export interface ChapterInfo {
    provider: MangaProvider;
    id: string;
    index?: number;
    title?: string;
    date?: Date;
}

export interface ChapterDetails extends ChapterInfo {
    manga: MangaInfo;
    nextChapter?: string;
    prevChapter?: string;
    images: LazyGet<string[] | null>;
    imageCacheLength: number;
}

export interface ChapterGroup {
    id: string;
    name: string;
    count: number;
    chapters: ChapterInfo[];
}

export interface MangaChapters {
    groups: ChapterGroup[];
}

export interface MangaDetails extends MangaInfo {
    description: string;
    status: MangaStatus;

    latestUpdate: Date;
    latestChapter: ChapterInfo | string;

    chapters: LazyGet<MangaChapters>;
}

export enum MangaStatus {
    UPDATING,
    FINISHED,
    SINGLE,
    TERMINATED,
    DMCA_TAKEDOWN,
    UNKNOWN,
    PAUSED
}

export enum MangaContentRating {
    GENERAL,
    EROTICA,
    ECCHI
}
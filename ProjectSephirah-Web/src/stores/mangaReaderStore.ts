import { defineStore } from "pinia";
import { ChapterDetails, MangaDetails } from "../backend/manga/Manga.ts";

interface State {
    current: {
        chapter?: ChapterDetails;
    };
}

export const useMangaReaderStore = defineStore("mangaReader", {
    state: (): State => ({
        current: {
            chapter: undefined,
        },
    }),
});
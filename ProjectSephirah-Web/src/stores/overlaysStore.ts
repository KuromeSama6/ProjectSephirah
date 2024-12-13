import { defineStore } from "pinia";
import { ChapterInfo, MangaDetails } from "../backend/manga/Manga.ts";

interface State {
    visibility: {};
    uiBlocked: boolean;
}

export const useOverlaysStore = defineStore("overlaysStore", {
    state: (): State => ({
        visibility: {},
        uiBlocked: false,
    }),
    actions: {},
});

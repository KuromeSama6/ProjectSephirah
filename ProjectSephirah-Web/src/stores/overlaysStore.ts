import { defineStore } from "pinia";
import { ChapterInfo, MangaDetails } from "../backend/manga/Manga.ts";

interface State {
    visibility: {
        sidebarMenu: boolean;
    };
    uiBlocked: boolean;
}

export const useOverlaysStore = defineStore("overlaysStore", {
    state: (): State => ({
        visibility: {
            sidebarMenu: false,
        },
        uiBlocked: false,
    }),
    actions: {},
});

import { SupportedLanguage } from "../backend/common/Language.ts";
import { defineStore } from "pinia";

export interface GlobalSettings {
    searchLangauge: SupportedLanguage;
    chapterSelectSortAscending: boolean;
}

const defaultSettings: GlobalSettings = {
    searchLangauge: "en",
    chapterSelectSortAscending: false,
};

export const useSettingsStore = defineStore("settings", {
    state: (): GlobalSettings => {
        const config = localStorage.getItem("globalSettings");
        if (config) {
            return JSON.parse(config);
        } else {
            return defaultSettings;
        }
    },

    actions: {
        Save() {
            localStorage.setItem("globalSettings", JSON.stringify(this.$state));
        },

        Reset() {
            Object.assign(this.$state, defaultSettings);
            this.Save();
        },
    },

});
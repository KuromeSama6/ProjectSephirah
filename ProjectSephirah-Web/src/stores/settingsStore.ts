import { SupportedLanguage } from "../backend/common/Language.ts";
import { defineStore } from "pinia";

export enum ProviderContentType {
    ALL = "All",
    SAFE_ONLY = "Safe Only",
    H_ONLY = "Hentai Only",
}

interface State {
    legalTermsAccepted: boolean;
    search: {
        language: SupportedLanguage;
        providerContentFilter: ProviderContentType;
    };
    chapterSelectSortAscending: boolean;
}

const defaultSettings: State = {
    legalTermsAccepted: false,
    search: {
        language: "en",
        providerContentFilter: ProviderContentType.SAFE_ONLY,
    },
    chapterSelectSortAscending: false,
};

export const useSettingsStore = defineStore("settings", {
    state: (): State => {
        const config = localStorage.getItem("globalSettings");
        if (config) {
            return {
                ...defaultSettings,
                ...JSON.parse(config),
            };
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
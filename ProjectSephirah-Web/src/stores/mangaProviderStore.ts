import { defineStore } from "pinia";
import { MangaProvider } from "../backend/manga/MangaProvider.ts";
import MangaProviderCopyManga from "../providers/manga/CopyManga.ts";
import MangaProviderNHentai from "../providers/manga/NHentai.ts";
import MangaProviderMangadex from "../providers/manga/Mangadex.ts";
import MangaProviderManganato from "../providers/manga/Manganato.ts";
import MangaProviderDmzj from "../providers/manga/Dmzj.ts";
import MangaProviderManhuagui from "../providers/manga/Manhuagui.ts";
import MangaProviderPica from "../providers/manga/Pica.ts";
import MangaProviderJmtt from "../providers/manga/Jmtt.ts";
import { ProviderStatus } from "../backend/provider/Provider.ts";

interface State {
    providers: Map<string, MangaProvider>;
    providerStatus: Map<string, ProviderStatus>;
    areProvidersChecked: boolean;
}

const providerList: MangaProvider[] = [
    new MangaProviderCopyManga(),
    new MangaProviderNHentai(),
    new MangaProviderMangadex(),
    new MangaProviderManganato(),
    new MangaProviderDmzj(),
    new MangaProviderManhuagui(),
    new MangaProviderPica(),
    new MangaProviderJmtt(),
];


export const useMangaProviderStore = defineStore("MangaProviderStore", {
    state: (): State => {
        const ret = {
            providers: new Map(),
            providerStatus: new Map(),
            areProvidersChecked: false,
        };

        providerList.forEach((provider) => {
            ret.providers.set(provider.id, provider);
            ret.providerStatus.set(provider.id, ProviderStatus.Unknown);
        });

        return ret;
    },
    actions: {
        ProviderAvailable(provider: MangaProvider): boolean {
            return this.providerStatus.get(provider.id) !== ProviderStatus.Unavailable;
        },

        async CheckProviders() {
            for (let provider of this.providers.values()) {
                var suc = await this.CheckSingleProvider(provider);
                console.log(`provider ${provider.id} is ${suc ? "available" : "unavailable"}`);
                this.providerStatus.set(provider.id, suc ? ProviderStatus.Available : ProviderStatus.Unavailable);
            }

            this.areProvidersChecked = true;
        },

        async CheckSingleProvider(provider: MangaProvider): Promise<boolean> {
            return await provider.CheckAvailability();
        },

        GetProvider(providerId: string): MangaProvider {
            return this.providers.get(providerId)!;
        },
    },
});

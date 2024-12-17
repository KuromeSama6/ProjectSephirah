import { defineStore } from "pinia";
import Cookies from "js-cookie";
import { Provider } from "../backend/provider/Provider.ts";

interface State {
    providers: Record<string, string>;
}

const defaultState: State = {
    providers: {},
};

export const useAuthenticationStore = defineStore("authentications", {
    state: (): State => {
        const config = Cookies.get("providerCredentials");
        if (config) {
            return {
                ...defaultState,
                ...JSON.parse(config),
            };
        } else {
            return defaultState;
        }
    },

    actions: {
        Save() {
            Cookies.set("providerCredentials", JSON.stringify(this.$state), { expires: 365 });
        },

        IsAuthenticated(provider: Provider): boolean {
            return !!this.providers[provider.id];
        },

        GetToken(provider: Provider): string {
            return this.providers[provider.id];
        },

        GetTokenByProviderId(provider: string): string {
            return this.providers[provider];
        },
    },
});
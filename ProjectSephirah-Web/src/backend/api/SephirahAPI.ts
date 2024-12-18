import { APIResponse, AuthenticationKeyPair } from "./APIResponse.ts";
import { APIUtil } from "./APIUtil.ts";
import { Provider } from "../provider/Provider.ts";
import { CryptoUtil } from "../util/Util.ts";

export namespace SephirahAPI {
    export const BASE_URL: string = import.meta.env.VITE_API_BASE_URL;

    export async function GetAuthenticationKeyPair(): Promise<AuthenticationKeyPair | null> {
        var res = await APIUtil.SendRequest<AuthenticationKeyPair>("get", "/api/provider/auth/proxy/get_key_pair");
        if (!res.success) return null;

        return res.data!;
    }

    export async function ProxiedAuthenticate(provider: string, keyPair: AuthenticationKeyPair, credentials: Record<string, any>): Promise<APIResponse> {
        // encrypt fields
        const encrypted: Record<string, any> = {};
        for (const key in credentials) {
            encrypted[key] = CryptoUtil.RsaEncrypt(credentials[key], keyPair.publicKey);
        }

        return await APIUtil.SendRequest("POST", `/api/provider/${provider}/proxy/auth/authenticate`, {
            keyPairId: keyPair.id,
            credentials: encrypted,
        });
    }

}
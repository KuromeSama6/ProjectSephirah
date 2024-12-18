import { MangaProvider } from "../../backend/manga/MangaProvider.ts";
import { ChapterDetails, ChapterInfo, MangaChapters, MangaDetails, MangaInfo, MangaStatus } from "../../backend/manga/Manga.ts";
import { SupportedLanguage } from "../../backend/common/Language.ts";
import axios, { AxiosHeaders, RawAxiosRequestHeaders } from "axios";
import { AuthenticationProvider, AuthenticationResponse, ProviderAuthenticationMode, ProviderTokenValidationStatus } from "../../backend/provider/Provider.ts";
import { RandomUtil } from "../../backend/util/Util.ts";
import CryptoJS from "crypto-js";
import { useAuthenticationStore } from "../../stores/authenticationStore.ts";
import { useSingletonsStore } from "../../stores/singletonsStore.ts";

export default class MangaProviderPica implements MangaProvider {
    readonly id = "pica";
    readonly info = {
        name: "哔咔漫画",
        website: "https://manhuapica.com/",
        isHentaiDedicated: true,
        primaryLanguage: "cn" as SupportedLanguage,
        supportedSearchLanguages: ["cn"] as SupportedLanguage[],
    };

    async CheckAvailability(): Promise<boolean> {
        return true;
    }

    async Search(kw: string, language: SupportedLanguage): Promise<MangaInfo[] | null> {
        var res = await axios.post("https://api.go2778.com/comics/advanced-search?page=1&s=dd", {
            sort: "dd",
            keyword: kw,
        }, {
            headers: PicaAPI.GetRequestHeaders("comics/advanced-search?page=1&s=dd", "POST"),
        });

        const data = res.data.data.comics;
        if (data.total == 0) return [];

        const ret: MangaInfo[] = [];

        for (var doc of data.docs) {
            ret.push({
                provider: this,
                id: doc._id,
                title: doc.title,
                author: doc.author,
                coverUrl: "https://s3.picacomic.com/static/" + doc.thumb.path,
            });
        }

        return ret;
    }

    async GetMangaDetails(id: string, language: SupportedLanguage): Promise<MangaDetails | null> {
        const res = await axios.get(`https://api.go2778.com/comics/${id}`, {
            headers: PicaAPI.GetRequestHeaders(`comics/${id}`, "GET"),
        });
        const data = res.data.data.comic;
        // console.log(data);

        return {
            provider: this,
            id: data._id,
            title: data.title,
            author: data.author,
            coverUrl: "https://s3.picacomic.com/static/" + data.thumb.path,
            description: data.description,
            status: MangaStatus.UNKNOWN,
            latestUpdate: new Date(data.updated_at),
            latestChapter: "",
            chapters: () => this.GetChaptersInternal(id),
        };
    }

    async GetChaptersInternal(id: string): Promise<MangaChapters> {
        const res = await axios.get(`https://api.go2778.com/comics/${id}/eps?page=1`, {
            headers: PicaAPI.GetRequestHeaders(`comics/${id}/eps?page=1`, "GET"),
        });
        const data = res.data.data.eps;
        const docs = data.docs;

        const chapters: ChapterInfo[] = [];
        for (const doc of docs) {
            chapters.push({
                provider: this,
                id: doc.order.toString(),
                title: doc.title,
                date: new Date(doc.updated_at),
                index: doc.order - 1,
            });
        }

        return {
            groups: [{
                id: "default",
                name: "Default",
                count: chapters.length,
                chapters: chapters,
            }],
        };
    }

    async GetChapterDetails(mangaId: string, chapterId: string, language: SupportedLanguage): Promise<ChapterDetails | null> {
        const chapters = await this.GetChaptersInternal(mangaId);
        const chapterInfo = chapters.groups[0].chapters.find(c => c.id == chapterId);

        const res = await axios.get(`https://api.go2778.com/comics/${mangaId}/order/${chapterId}/pages?page=1`, {
            headers: PicaAPI.GetRequestHeaders(`comics/${mangaId}/order/${chapterId}/pages?page=1`, "GET"),
        });
        const data = res.data.data.pages.docs;

        //https://img.picacomic.com/iLzmLuVPU4sYyUaI-QY2JaZ5Oy8sfhCBYrrMcR9JfVo/rs:fill:300:400:0/g:sm/aHR0cHM6Ly9zdG9yYWdlLWIucGljYWNvbWljLmNvbS9zdGF0aWMvNDAxMzE3N2YtZTBkNC00ZGVmLTgzZWUtZGI4OWNlOGFkMTc5LmpwZw.jpg

        // console.log(data);

        return {
            ...chapterInfo!,
            manga: (await this.GetMangaDetails(mangaId, language))!,
            images: () => ({
                links: data.map((c: any) => `https://img.picacomic.com/${c.media.path.replace("tobeimg/", "")}`),
                extraData: {},
            }),
            imageCacheLength: 0,
        };
    }

    readonly auth = authenticationProvider;
}

const authenticationProvider: AuthenticationProvider = {
    isProxiedRequest: false,
    authenticationMode: ProviderAuthenticationMode.ALL,

    GetCredentialsObject(): any {
        return {
            username: "",
            password: "",
        };
    },
    async Authenticate(credential: any): Promise<AuthenticationResponse> {
        const headers = PicaAPI.GetRequestHeaders("auth/sign-in", "POST");
        // console.log(headers);

        try {
            var res = await axios.post("https://api.go2778.com/auth/sign-in", {
                email: credential.username,
                password: credential.password,

            }, {
                headers: headers,
            });

            if (res.data.data.token) {
                return {
                    success: true,
                    token: res.data.data.token,
                };
            } else {
                return {
                    success: false,
                    message: "invalid API call",
                };
            }

        } catch (err: any) {
            console.log(err);
            return {
                success: false,
                message: err.response ? err.response.data.message : "Network error",
            };
        }
    },
    async Deauthenticate(): Promise<void> {
        // Picacomic does not have a logout endpoint
    },
    async ValidateToken(): Promise<ProviderTokenValidationStatus> {
        const toast = useSingletonsStore().toastSingleton;
        const token = useAuthenticationStore().GetTokenByProviderId("pica");
        if (!token) return ProviderTokenValidationStatus.INVALID;

        try {
            const res = await axios.get("https://api.go2778.com/announcements?pages=1", {
                headers: PicaAPI.GetRequestHeaders("announcements?pages=1", "GET"),
            });
            return res.data.data ? ProviderTokenValidationStatus.VALID : ProviderTokenValidationStatus.INVALID;

        } catch (err: any) {
            console.log(err);
            return ProviderTokenValidationStatus.REQUEST_FAILED;
        }
    },

};

export namespace PicaAPI {
    export const appleVersion: string = DeRabbit("U2FsdGVkX18QFwKERvHee/ffgOaC6ubPw+FwzpV6Evpm5ICFfhaxLim4SxhB+emgtRrHjvtOJs9tOuruZKdv2BPSEBw9ih7EfcVr2Srmoxc=");
    export const appleKillFlag: string = DeRabbit("U2FsdGVkX1/kZqW9m/2nul1sQl3H9FgxcBFF1fI6tzUtXz4NxMTK3cK3y2JSBrz6");
    export const baseUrl = "https://api.picacomic.com/";
    export const nonce = RandomUtil.GenerateRandomString(32).toLowerCase();

    /*
    Usage pulled from https://manhuapica.com/wp-content/themes/pic-pwa-pic/comic/assets/js/putils.js?202406108=2.1555755
    ---
    var setTime = getTimeOnece();
    var pathname = "auth/sign-in";
    var mothod = "POST";
    ...
    postHeader(setTime, pathname, mothod)
    ...
    function postHeader(setTime, pathname, mothod)
    ...
    var header = [
            {'name': 'app-channel', 'value': 1},
            {'name': 'app-uuid', 'value': 'webUUID'},
            {'name': "accept", 'value': 'application/vnd.picacomic.com.v1+json'},
            {'name': "app-platform", 'value': 'android'},
            {'name': "Content-Type", 'value': 'application/json; charset=UTF-8'},
            {'name': "time", 'value': setTime},
            {'name': "nonce", 'value': getNonce()},
            {'name': "image-quality", 'value': getImageQuality()},
            //  {'signature':signature},
        ]
     */
    export function GetRequestHeaders(path: string, method: "GET" | "POST"): RawAxiosRequestHeaders | AxiosHeaders {
        const time = GetTimeNonce();

        return {
            "Content-Type": "application/json; charset=UTF-8",
            Authorization: useAuthenticationStore().GetTokenByProviderId("pica"),
            Accept: "application/vnd.picacomic.com.v1+json",
            nonce: nonce,
            "app-channel": 1,
            "app-uuid": "webUUID",
            "app-platform": "android",
            "image-quality": "medium",
            time: time,
            signature: GetSignature(path, time, method),
        };
    }

    /*
    Originally named 'getTimeOnece' in the source code.
     */
    export function GetTimeNonce() {
        return (new Date().getTime() / 1000).toFixed(0);
    }

    export function GetSignature(url: string, time: string, method: "GET" | "POST") {
        var raw = url.replace(baseUrl, "") + time + nonce + method + appleKillFlag;
        raw = raw.toLowerCase();

        return CryptoJS.HmacSHA256(raw, appleVersion).toString(CryptoJS.enc.Hex);
    }

    export function DeRabbit(dataText: string) {
        return CryptoJS.TripleDES.decrypt(dataText, "Please don't hack the api, thanks").toString(CryptoJS.enc.Utf8);
    }
}
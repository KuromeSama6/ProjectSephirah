import { ProxiedMangaProvider } from "../../backend/manga/MangaProvider.ts";
import { SupportedLanguage } from "../../backend/common/Language.ts";
import { AuthenticationProvider, AuthenticationResponse, ProviderAuthenticationMode, ProviderTokenValidationStatus } from "../../backend/provider/Provider.ts";
import { SephirahAPI } from "../../backend/api/SephirahAPI.ts";
import { ChapterDetails, ChapterImages, ImagePostProcessData } from "../../backend/manga/Manga.ts";
import CryptoJS from "crypto-js";
import { Jimp } from "jimp";
import { Buffer } from "buffer";

export default class MangaProviderJmtt extends ProxiedMangaProvider {
    readonly id = "18comic";
    readonly info = {
        name: "禁漫天堂",
        website: "https://18comic.vip/",
        isHentaiDedicated: true,
        primaryLanguage: "cn" as SupportedLanguage,
        supportedSearchLanguages: ["cn"] as SupportedLanguage[],
    };
    readonly IMAGE_CDN_URL = "https://cdn-msp.18comic.vip/media";

    readonly auth = auth;

    async GetChapterDetails(mangaId: string, chapterId: string, language: SupportedLanguage): Promise<ChapterDetails | null> {
        const ret = await super.GetChapterDetails(mangaId, chapterId, language);
        if (!ret) return ret;

        const images = (await ret.images())!;
        // console.log(images.extraData.scrambleId);

        return {
            ...ret,
            images: () => this.GetChapterImagesInternal(mangaId, chapterId, language, images),
        };
    }

    async GetChapterImagesInternal(mangaId: string, chapterId: string, language: SupportedLanguage, original: ChapterImages): Promise<ChapterImages | null> {
        // get scramble id
        const res = await super.GetChapterImages(mangaId, chapterId, language);
        if (res == null) return null;

        // console.log(res);

        return {
            links: original.links.map((link) => this.IMAGE_CDN_URL + link),
            extraData: (await res.extraData)!.extraData,
            async PostProcess(data: ImagePostProcessData) {
                return DescrambleImage(mangaId, original.extraData.scrambleId, data);
            },
        };
    }
}

const auth: AuthenticationProvider = {
    authenticationMode: ProviderAuthenticationMode.NONE,
    GetCredentialsObject(): any {
        return {
            username: "",
            password: "",
        };
    },

    async Authenticate(credential: any): Promise<AuthenticationResponse> {
        // get key pair
        const keyPair = await SephirahAPI.GetAuthenticationKeyPair();
        if (keyPair == null) {
            return {
                success: false,
                message: "failed to get key pair",
            };
        }

        const res = await SephirahAPI.ProxiedAuthenticate("18comic", keyPair, {
            username: credential.username,
            password: credential.password,
        });
        console.log(res);

        if (res.success) {
            return {
                success: true,
                token: res.data!.token,
            };
        } else {
            if (res.processed) {
                return {
                    success: false,
                    message: res.message,
                };
            } else {
                return {
                    success: false,
                    message: "network error",
                };
            }
        }
    },

    async Deauthenticate(): Promise<void> {

    },

    async ValidateToken(): Promise<ProviderTokenValidationStatus> {
        return ProviderTokenValidationStatus.REQUEST_FAILED;
    },
};

async function DescrambleImage(mangaId: string, scrambleId: number, data: ImagePostProcessData): Promise<string> {
    const fileName = data.url.split("/").slice(-1)[0].split(".")[0];
    const count = GetSegmentCount(scrambleId, parseInt(mangaId), fileName);
    return await DesegmentImage(data.imageBase64, count, data.originalSize.width, data.originalSize.height);
}

function GetSegmentCount(scrambleId: number, aid: number, filename: string): number {
    if (aid < scrambleId) {
        return 0;
    } else if (aid < 268850) {
        return 10;
    } else {
        const x = aid < 421926 ? 10 : 8;
        const s = `${aid}${filename}`;
        // console.log(s);
        const hash = CryptoJS.MD5(s).toString();
        let num = hash.charCodeAt(hash.length - 1);
        num %= x;
        num = num * 2 + 2;
        return num;
    }
}

async function DesegmentImage(
    imgDataBase64: string,
    segmentCount: number,
    originalWidth: number,
    originalHeight: number,
): Promise<string> {
    // console.log(`segments: ${segmentCount}`);
    if (segmentCount == 0) {
        return imgDataBase64; // No descrambling needed
    }

    // Decode Base64 image
    const imgBuffer = Buffer.from(imgDataBase64.split(",")[1], "base64");
    const img = await Jimp.read(imgBuffer);

    const width = img.width;
    const height = img.height;

    const ret = new Jimp({
        width,
        height,
        color: 0x00000000,
    });

    // process
    /*
    over = h % num
        for i in range(num):
            move = math.floor(h / num)
            y_src = h - (move * (i + 1)) - over
            y_dst = move * i

            if i == 0:
                move += over
            else:
                y_dst += over

            img_decode.paste(
                img_src.crop((
                    0, y_src,
                    w, y_src + move
                )),
                (
                    0, y_dst,
                    w, y_dst + move
                )
            )
     */
    const over = height % segmentCount;
    for (let i = 0; i < segmentCount; i++) {
        let move = Math.floor(height / segmentCount);
        let y_src = height - (move * (i + 1)) - over;
        let y_dst = move * i;

        if (i == 0) {
            move += over;
        } else {
            y_dst += over;
        }

        const cropped = img.clone().crop({
            x: 0,
            y: y_src,
            w: width,
            h: y_src + move,
        });
        ret.composite(cropped, 0, y_dst);
    }

    // save image to base64
    // const ret = await img.getBase64("image/png");
    // console.log(imgDataBase64);
    return URL.createObjectURL(new Blob([await ret.getBuffer("image/png")], { type: "image/png" }));
}
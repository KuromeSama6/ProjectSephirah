import { JSEncrypt } from "jsencrypt";

export namespace RandomUtil {
    export function GenerateRandomString(length: number): string {
        const characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        let result = "";

        for (let i = 0; i < length; i++) {
            const randomIndex = Math.floor(Math.random() * characters.length);
            result += characters.charAt(randomIndex);
        }

        return result;
    }
}

export namespace CryptoUtil {
    export function RsaEncrypt(data: string, publicKey: string): string {
        const encryptor = new JSEncrypt();
        encryptor.setPublicKey(publicKey);
        return encryptor.encrypt(data) as string;
    }
}
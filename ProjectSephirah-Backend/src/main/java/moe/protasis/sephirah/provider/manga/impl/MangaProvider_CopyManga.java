package moe.protasis.sephirah.provider.manga.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import moe.protasis.sephirah.data.manga.ChapterDetails;
import moe.protasis.sephirah.data.manga.MangaDetails;
import moe.protasis.sephirah.data.manga.MangaInfo;
import moe.protasis.sephirah.exception.APIException;
import moe.protasis.sephirah.exception.FeatureNotImplementedException;
import moe.protasis.sephirah.exception.provider.ProviderNotAvailableException;
import moe.protasis.sephirah.exception.provider.ProviderRequestException;
import moe.protasis.sephirah.provider.manga.IProxyMangaProvider;
import moe.protasis.sephirah.util.JsonWrapper;
import moe.protasis.sephirah.util.ProviderUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jsoup.Jsoup;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MangaProvider_CopyManga implements IProxyMangaProvider {
    private static final String AES_KEY_HEX = Utf8ToHex("xxxmanga.woo.key");
    private static final SecretKey COPYMANGA_AES_KEY = new SecretKeySpec(HexStringToByteArray(AES_KEY_HEX), "AES");

    @Override
    public String GetId() {
        return "copymanga";
    }

    @Override
    public Request.Builder GetRequestBuilder(String path) {
        return new Request.Builder()
                .get()
                .header("User-Agent", "Mozilla/5.0")
                .url("https://www.mangacopy.com" + path);
    }

    @Override
    public void VerifyStatus(OkHttpClient client) {

    }

    @Override
    public MangaInfo[] Search(OkHttpClient client, String kw) {
        throw new FeatureNotImplementedException();
    }

    @Override
    public MangaDetails GetMangaDetails(OkHttpClient client, String id, String language) {
        throw new FeatureNotImplementedException();
    }

    @Override
    public ChapterDetails GetChapterDetails(OkHttpClient client, String mangaId, String chapterId, String language) {
        throw new FeatureNotImplementedException();
    }

    /**
     * Huge thanks to <a href="https://blog.skyju.cc/post/copymanga-chapter-reverse-engineering/">juzeon</a> for figuring out how to decrypt the response!
     */
    @Override
    public List<String> GetChapterImages(OkHttpClient client, String mangaId, String chapterId, String language) {
        var req = GetRequestBuilder("/comic/%s/chapter/%s".formatted(mangaId, chapterId))
                .build();
        var res = ProviderUtil.SendProviderRequestString(client, req);
        if (!res.response().isSuccessful()) {
            throw new ProviderRequestException(60, 502, "provider request failed", new JsonWrapper()
                    .Set("code", res.response().code())
            );
        }

        if (!res.response().isSuccessful())
            throw new ProviderNotAvailableException("load failed (remote returned %d)".formatted(res.response().code()));

        var page = Jsoup.parse(res.body());
        var content = page.selectFirst(".imageData").attr("contentKey");

        try {
            var decrypted = DecryptResult(content);
            List<String> ret = new ArrayList<>();

            var objectMapper = new ObjectMapper();
            List<Object> list = objectMapper.readValue(decrypted, objectMapper.getTypeFactory().constructCollectionType(List.class, Object.class));
            for (var obj : list) {
                var json = new JsonWrapper(obj);
                ret.add(json.GetString("url"));
            }

            return ret;

        } catch (Exception e) {
            e.printStackTrace();
            throw new APIException(500, 60, "image data decryption error");
        }
    }

    public static String DecryptResult(String str) throws Exception {
        // Extract IV (first 16 characters) and encrypted body (remaining part)
        String ivHex = Utf8ToHex(str.substring(0, 16));
        String bodyHex = str.substring(16);

        byte[] ivBytes = HexStringToByteArray(ivHex);
        byte[] encryptedBytes = HexStringToByteArray(bodyHex);

        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, COPYMANGA_AES_KEY, iv);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private static String Utf8ToHex(String str) {
        StringBuilder hex = new StringBuilder();
        for (char c : str.toCharArray()) {
            hex.append(String.format("%02x", (int)c));
        }
        return hex.toString();
    }

    private static byte[] HexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}

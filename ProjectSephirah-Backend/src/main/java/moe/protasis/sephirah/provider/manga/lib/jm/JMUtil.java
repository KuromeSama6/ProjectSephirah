package moe.protasis.sephirah.provider.manga.lib.jm;

import lombok.experimental.UtilityClass;
import okhttp3.Headers;
import org.joda.time.DateTime;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class for interacting with the JMComic mobile API.
 * Some methods ported from https://github.com/hect0x7/JMComic-Crawler-Python/src/jmcomic/jm_toolkit.py.
 */
@UtilityClass
public class JMUtil {
    public static Headers GetRequestHeaders() {
        return new Headers.Builder()
                .add("tokenparam", "")
                .build();
    }

    public static JMTokenAndParam GetTokenAndParam() {
        return GetTokenAndParam(JMMagicConstants.APP_TOKEN_SECRET);
    }

    public static JMTokenAndParam GetTokenAndParam(String secret) {
        var ver = JMMagicConstants.APP_VERSION;
        long ts = DateTime.now().getMillis() / 1000;

        // tokenparam: "1700566805,1.6.3"
        String tokenparam = String.format("%s,%s", ts, ver);

        // token: "81498a20feea7fbb7149c637e49702e3"
        String token = Md5Sum(ts + secret);

        return new JMTokenAndParam(token, tokenparam, ts);
    }

    /**
     * Decrypt API response data.
     *
     * @param data   Encrypted data (Base64 string)
     * @param ts     Timestamp
     * @return Decrypted JSON string
     * @throws Exception if decryption fails
     */
    public static String DecryptApiResponse(String data, long ts) throws Exception {
        var secret = JMMagicConstants.APP_DATA_SECRET;
        // 1. Base64 decode
        byte[] dataB64 = Base64.getDecoder().decode(data);

        // 2. AES-ECB decrypt
        String key = Md5Sum(ts + secret);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] dataAes = cipher.doFinal(dataB64);

        // 3. Remove padding
        int paddingLength = dataAes[dataAes.length - 1];
        byte[] unpaddedData = new byte[dataAes.length - paddingLength];
        System.arraycopy(dataAes, 0, unpaddedData, 0, unpaddedData.length);

        // 4. Decode to string (JSON)
        return new String(unpaddedData, StandardCharsets.UTF_8);
    }

    /**
     * Generate MD5 hash in hexadecimal format.
     *
     * @param key Input string
     * @return MD5 hash as a hexadecimal string
     */
    public static String Md5Sum(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Key parameter must be a string.");
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(key.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    public record JMTokenAndParam (
            String token,
            String tokenParam,
            long timestamp
    ) {}
}

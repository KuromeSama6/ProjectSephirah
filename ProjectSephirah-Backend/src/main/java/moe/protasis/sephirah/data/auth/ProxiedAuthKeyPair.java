package moe.protasis.sephirah.data.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("proxied_auth_key_pair")
@Builder
public class ProxiedAuthKeyPair {
    @Id
    private String id;
    private byte[] publicKey;
    private byte[]  privateKey;
    @Indexed(expireAfterSeconds = 0)
    private DateTime expire;

    public String DecryptContent(String content) {
        try {
            Cipher ciper = Cipher.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            ciper.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] encryptedBytes = Base64.getDecoder().decode(content);

            var ret = ciper.doFinal(encryptedBytes);
            return new String(ret);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException | InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            return null;
        }

    }
}

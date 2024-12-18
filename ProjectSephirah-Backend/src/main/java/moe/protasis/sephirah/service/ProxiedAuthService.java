package moe.protasis.sephirah.service;

import moe.protasis.sephirah.data.auth.ProxiedAuthKeyPair;
import moe.protasis.sephirah.repository.ProxiedAuthRepo;
import moe.protasis.sephirah.util.RandomStringGenerator;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class ProxiedAuthService {
    @Autowired
    private ProxiedAuthRepo repo;

    public ProxiedAuthKeyPair CreateKeyPair() {
        try {
            var generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048); // Set the key size
            var keyPair = generator.generateKeyPair();

            var ret = ProxiedAuthKeyPair.builder()
                    .id(RandomStringGenerator.GenerateRandomSnowflake())
                    .privateKey(keyPair.getPrivate().getEncoded())
                    .publicKey(keyPair.getPublic().getEncoded())
                    .expire(DateTime.now().plusMinutes(1))
                    .build();

            repo.save(ret);
            return ret;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}

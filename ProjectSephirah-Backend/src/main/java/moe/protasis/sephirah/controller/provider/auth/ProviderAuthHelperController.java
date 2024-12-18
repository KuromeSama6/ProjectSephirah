package moe.protasis.sephirah.controller.provider.auth;

import moe.protasis.sephirah.service.ProxiedAuthService;
import moe.protasis.sephirah.util.JsonWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping(value = "/api/provider/auth", produces = "application/json", consumes = "application/json")
public class ProviderAuthHelperController {
    @Autowired
    private ProxiedAuthService proxiedAuthService;

    @GetMapping(value = "/proxy/get_key_pair", consumes = "*/*")
    public JsonWrapper GetProxyAuthKeyPair() {
        var ret = proxiedAuthService.CreateKeyPair();
        return new JsonWrapper()
                .Set("id", ret.getId())
                .Set("publicKey", Base64.getEncoder().encodeToString(ret.getPublicKey()))
                .Set("expire", ret.getExpire().getMillis());
    }
}

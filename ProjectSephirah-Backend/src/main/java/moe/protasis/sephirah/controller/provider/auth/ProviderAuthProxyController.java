package moe.protasis.sephirah.controller.provider.auth;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import moe.protasis.sephirah.exception.APIException;
import moe.protasis.sephirah.provider.IProxyProvider;
import moe.protasis.sephirah.repository.ProxiedAuthRepo;
import moe.protasis.sephirah.request.provider.auth.ProxiedProviderAuthRequest;
import moe.protasis.sephirah.service.ProviderService;
import moe.protasis.sephirah.service.ProxiedAuthService;
import moe.protasis.sephirah.util.JsonWrapper;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/provider/{providerId}/proxy/auth", consumes = "*/*", produces = "application/json")
public class ProviderAuthProxyController {
    @Autowired
    private ProviderService providerService;
    @Autowired
    private ProxiedAuthService authService;
    @Autowired
    private ProxiedAuthRepo authRepo;
    @Autowired
    private OkHttpClient httpClient;

    @PostMapping("/authenticate")
    public JsonWrapper Authenticate(IProxyProvider provider, @RequestBody @Valid ProxiedProviderAuthRequest req) {
        var authProvider = provider.GetAuthenticationProvider();
        if (authProvider == null)
            throw new APIException(400, 15, "provider %s does not support authentication".formatted(provider.GetId()));

        // decrypt data
        var keyPair = authRepo.findById(req.getKeyPairId()).orElse(null);
        if (keyPair == null)
            throw new APIException(401, 16, "invalid key pair");

        var credentials = req.getCredentials();
        for (var key : credentials.keySet()) {
            var decrypted = keyPair.DecryptContent(credentials.get(key));
            if (decrypted == null)
                throw new APIException(401, 16, "invalid key pair");

            credentials.put(key, decrypted);
        }
        authRepo.deleteById(req.getKeyPairId());

        var token = authProvider.Authenticate(httpClient, credentials);

        return new JsonWrapper()
                .Set("provider", provider.GetId())
                .Set("token", token);
    }
}

package moe.protasis.sephirah.provider.auth;

import okhttp3.OkHttpClient;

import java.util.Map;

public interface IAuthenticationProvider {
    String Authenticate(OkHttpClient client, Map<String, String> credentials);
}

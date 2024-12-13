package moe.protasis.sephirah.provider;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public interface IProxyProvider {
    String GetId();
    Request.Builder GetRequestBuilder(String path);

    void VerifyStatus(OkHttpClient client);
}

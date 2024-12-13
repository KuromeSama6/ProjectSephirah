package moe.protasis.sephirah.util;

import lombok.experimental.UtilityClass;
import moe.protasis.sephirah.exception.provider.ProviderConnectionException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

@UtilityClass
public class ProviderUtil {
    public static ProviderRequestResponseString SendProviderRequestString(OkHttpClient client, Request req) {
        try (var res = client.newCall(req).execute()) {
            return new ProviderRequestResponseString(res, res.body().string());

        } catch (IOException e) {
            e.printStackTrace();
            throw new ProviderConnectionException("failed to connect to provider");
        }
    }


    public record ProviderRequestResponseString(Response response, String body) { }
    public record ProviderRequestResponseBytes(Response response, byte[] body) { }
}

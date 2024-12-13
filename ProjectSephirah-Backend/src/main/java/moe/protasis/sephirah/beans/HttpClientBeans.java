package moe.protasis.sephirah.beans;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class HttpClientBeans {
    @Bean
    public OkHttpClient OkHttpClient() {
        return new OkHttpClient();
    }
}

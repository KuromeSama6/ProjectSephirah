package moe.protasis.sephirah.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonConfig {
    @Bean
    public SimpleModule JsonWrapperModule() {
        SimpleModule ret = new SimpleModule();
//        ret.addSerializer(JsonWrapper.class, new JsonWrapper.JsonWrapperSerializer());
        return ret;
    }
}

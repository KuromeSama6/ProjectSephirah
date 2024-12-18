package moe.protasis.sephirah.config;

import lombok.extern.slf4j.Slf4j;
import moe.protasis.sephirah.resolver.JsonWrapperReturnResolver;
import moe.protasis.sephirah.resolver.ProviderBodyResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@Slf4j
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private ProviderBodyResolver mangaProviderBodyResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(mangaProviderBodyResolver);
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        handlers.add(new JsonWrapperReturnResolver());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
    }
}

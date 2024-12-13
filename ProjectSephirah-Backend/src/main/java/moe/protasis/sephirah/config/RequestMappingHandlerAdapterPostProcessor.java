package moe.protasis.sephirah.config;

import moe.protasis.sephirah.resolver.JsonWrapperReturnResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

@Component
public class RequestMappingHandlerAdapterPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RequestMappingHandlerAdapter) {
            RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter)bean;
            adapter.setReturnValueHandlers(List.of(new JsonWrapperReturnResolver(), new HttpEntityMethodProcessor(adapter.getMessageConverters())));
        }
        return bean;
    }
}
package moe.protasis.sephirah.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import moe.protasis.sephirah.exception.NotFoundException;
import moe.protasis.sephirah.provider.IProxyProvider;
import moe.protasis.sephirah.provider.manga.IProxyMangaProvider;
import moe.protasis.sephirah.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Slf4j
@Component
public class ProviderBodyResolver implements HandlerMethodArgumentResolver {
    @Autowired
    private ProviderService providerService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return IProxyProvider.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        var req = webRequest.getNativeRequest(HttpServletRequest.class);
        Map<String, String> pathVariables = (Map<String, String>)req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        // Retrieve the specific path variable (replace "variableName" with the actual name)
        String providerId = pathVariables.get("providerId");
//        log.info(providerId);

        var provider = providerService.GetMangaProvider(providerId, IProxyProvider.class);
        if (provider == null)
            throw new NotFoundException("provider %s not found".formatted(providerId));

        if (!parameter.getParameterType().isAssignableFrom(provider.getClass()))
            throw new NotFoundException("provider %s has invalid provider type".formatted(providerId));

        return parameter.getParameterType().cast(provider);
    }
}

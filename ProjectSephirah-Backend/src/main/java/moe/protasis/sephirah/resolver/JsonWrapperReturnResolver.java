package moe.protasis.sephirah.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import moe.protasis.sephirah.response.StandardAPIResponse;
import moe.protasis.sephirah.util.JsonWrapper;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
public class JsonWrapperReturnResolver implements HandlerMethodReturnValueHandler {
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.getParameterType().equals(JsonWrapper.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        JsonWrapper jsonWrapper = returnValue == null ? new JsonWrapper() : (JsonWrapper)returnValue;
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

        if (response != null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            new ObjectMapper().writeValue(response.getWriter(), StandardAPIResponse.ok().data(jsonWrapper.getJson()));
            mavContainer.setRequestHandled(true);
        }
    }
}

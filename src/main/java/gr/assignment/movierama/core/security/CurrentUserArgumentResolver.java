package gr.assignment.movierama.core.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.NumberUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.security.Principal;
import java.util.Optional;

public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUser.class) != null
                && Long.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Optional<Principal> authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
        if (!authentication.isPresent()) {
            return null;
        }
        String principalName = authentication.get().getName();
        try {
            return NumberUtils.parseNumber(principalName, Long.class);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Principal " + principalName + " cannot be convert to  " + parameter.getParameterType(),e);
        }
    }
}
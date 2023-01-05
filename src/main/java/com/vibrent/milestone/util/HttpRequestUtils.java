package com.vibrent.milestone.util;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public final class HttpRequestUtils {
    private HttpRequestUtils() {
    }

    public static HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        HttpServletRequest servletRequest = null;

        if(requestAttributes instanceof  ServletRequestAttributes) {
            servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        }

        return servletRequest;
    }
}

package org.example.expert.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AdminAccessLoggingAspect {

    private final HttpServletRequest request;

    @Before("execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
    public void logBeforeChangeUserRole(JoinPoint joinPoint) {
        String userId = String.valueOf(request.getAttribute("userId")); // attribute?
        String requestUrl = request.getRequestURI();
        LocalDateTime requestTime = LocalDateTime.now();
        // Make sure request.setAttribute("userId", ...) is done before this aspect runs (usually in a filter or an authentication layer).

        log.info("Admin Access Log - User ID: {}, Request Time: {}, Request URL: {}, Method: {}",
                userId, requestTime, requestUrl, joinPoint.getSignature().getName()); // joinPoint.getSignature().getName() -> ?
    }
}

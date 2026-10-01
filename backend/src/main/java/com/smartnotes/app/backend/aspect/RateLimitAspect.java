package com.smartnotes.app.backend.aspect;

import com.smartnotes.app.backend.annotation.RateLimit;
import com.smartnotes.app.backend.exception.TooManyAttemptsException;
import com.smartnotes.app.backend.service.RateLimiterService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {
    private final RateLimiterService rateLimiterService;

    private final HttpServletRequest request;

    @Around("@annotation(rateLimit)")
    public Object rateLimit(
            ProceedingJoinPoint joinPoint,
            RateLimit rateLimit) throws Throwable {

        String ip = request.getRemoteAddr();



        Bandwidth bandwidth = switch (rateLimit.type()) {
            case STRICT -> rateLimiterService.getStrictBandwidth();
            case RELAXED -> rateLimiterService.getRelaxedBandwidth();
            default -> throw new IllegalStateException("Unknown rate limit type");
        };


        if (!rateLimiterService.isAllowed(
                rateLimit.endpoint(),
                ip,
                bandwidth)) {
            throw new TooManyAttemptsException("Too many requests");
        }

        return joinPoint.proceed();
    }
}




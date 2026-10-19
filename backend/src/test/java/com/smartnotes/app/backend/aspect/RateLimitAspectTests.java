package com.smartnotes.app.backend.aspect;

import com.smartnotes.app.backend.annotation.RateLimit;
import com.smartnotes.app.backend.annotation.RateLimitType;
import com.smartnotes.app.backend.exception.TooManyAttemptsException;
import com.smartnotes.app.backend.service.RateLimiterService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitAspectTests {

    @Mock
    private RateLimiterService rateLimiterService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private RateLimit rateLimit;

    @InjectMocks
    private RateLimitAspect rateLimitAspect;

    private Bandwidth strictBandwidth;
    private Bandwidth relaxedBandwidth;

    @BeforeEach
    void setUp() {
        strictBandwidth = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(10)));
        relaxedBandwidth = Bandwidth.classic(30, Refill.greedy(1, Duration.ofMinutes(1)));
    }

    @Test
    void RateLimitAspect_Allowed_ProceedsSuccessfully() throws Throwable {
        // Arrange
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(rateLimit.type()).thenReturn(RateLimitType.STRICT);
        when(rateLimit.endpoint()).thenReturn("login");
        when(rateLimiterService.getStrictBandwidth()).thenReturn(strictBandwidth);
        when(rateLimiterService.isAllowed("login", "127.0.0.1", strictBandwidth)).thenReturn(true);
        when(joinPoint.proceed()).thenReturn("success-result");

        // Act
        Object result = rateLimitAspect.rateLimit(joinPoint, rateLimit);

        // Assert
        Assertions.assertThat(result).isEqualTo("success-result");
        verify(joinPoint).proceed();
    }

    @Test
    void RateLimitAspect_RelaxedType_Allowed_ProceedsSuccessfully() throws Throwable {
        // Arrange
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(rateLimit.type()).thenReturn(RateLimitType.RELAXED);
        when(rateLimit.endpoint()).thenReturn("get_all_notes");
        when(rateLimiterService.getRelaxedBandwidth()).thenReturn(relaxedBandwidth);
        when(rateLimiterService.isAllowed("get_all_notes", "192.168.1.1", relaxedBandwidth)).thenReturn(true);
        when(joinPoint.proceed()).thenReturn("notes-result");

        // Act
        Object result = rateLimitAspect.rateLimit(joinPoint, rateLimit);

        // Assert
        Assertions.assertThat(result).isEqualTo("notes-result");
        verify(joinPoint).proceed();
    }

    @Test
    void RateLimitAspect_NotAllowed_ThrowsTooManyAttemptsException() throws Throwable {
        // Arrange
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(rateLimit.type()).thenReturn(RateLimitType.STRICT);
        when(rateLimit.endpoint()).thenReturn("register");
        when(rateLimiterService.getStrictBandwidth()).thenReturn(strictBandwidth);
        when(rateLimiterService.isAllowed("register", "127.0.0.1", strictBandwidth)).thenReturn(false);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> rateLimitAspect.rateLimit(joinPoint, rateLimit))
                .isInstanceOf(TooManyAttemptsException.class)
                .hasMessageContaining("Too many requests");

        verify(joinPoint, never()).proceed();
    }
}

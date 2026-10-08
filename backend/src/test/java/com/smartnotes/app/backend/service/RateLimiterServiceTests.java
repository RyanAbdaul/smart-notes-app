package com.smartnotes.app.backend.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.distributed.proxy.RemoteBucketBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimiterServiceTests {

    @Mock
    private ProxyManager<byte[]> proxyManager;

    @Mock
    private RemoteBucketBuilder<byte[]> remoteBucketBuilder;

    @Mock
    private BucketProxy bucket;

    @InjectMocks
    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        when(proxyManager.builder()).thenReturn(remoteBucketBuilder);
        when(remoteBucketBuilder.build(any(byte[].class), any(Supplier.class))).thenReturn(bucket);
    }

    @Test
    void RateLimiterService_IsAllowed_DefaultBandwidth_Allowed_ReturnsTrue() {
        // Arrange
        when(bucket.tryConsume(1)).thenReturn(true);

        // Act
        boolean allowed = rateLimiterService.isAllowed("login", "127.0.0.1");

        // Assert
        Assertions.assertThat(allowed).isTrue();
    }

    @Test
    void RateLimiterService_IsAllowed_DefaultBandwidth_Exceeded_ReturnsFalse() {
        // Arrange
        when(bucket.tryConsume(1)).thenReturn(false);

        // Act
        boolean allowed = rateLimiterService.isAllowed("login", "127.0.0.1");

        // Assert
        Assertions.assertThat(allowed).isFalse();
    }

    @Test
    void RateLimiterService_IsAllowed_CustomBandwidth_Allowed_ReturnsTrue() {
        // Arrange
        Bandwidth customBandwidth = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        when(bucket.tryConsume(1)).thenReturn(true);

        // Act
        boolean allowed = rateLimiterService.isAllowed("custom_endpoint", "192.168.1.100", customBandwidth);

        // Assert
        Assertions.assertThat(allowed).isTrue();
    }

    @Test
    void RateLimiterService_IsAllowed_CustomBandwidth_Exceeded_ReturnsFalse() {
        // Arrange
        Bandwidth customBandwidth = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        when(bucket.tryConsume(1)).thenReturn(false);

        // Act
        boolean allowed = rateLimiterService.isAllowed("custom_endpoint", "192.168.1.100", customBandwidth);

        // Assert
        Assertions.assertThat(allowed).isFalse();
    }
}

package com.smartnotes.app.backend.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RateLimiterService {
    private final ProxyManager<byte[]> proxyManager;

    // Key: endpoint:ipAddress. Never evicted, so it grows with unique IPs.
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Getter
    private final Bandwidth strictBandwidth =
            Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(10)));

    @Getter
    private final Bandwidth relaxedBandwidth =
            Bandwidth.classic(30, Refill.greedy(1, Duration.ofMinutes(1)));

    public boolean isAllowed(String endpoint, String ipAddress) {
        return isAllowed(endpoint, ipAddress, strictBandwidth);
    }

    // The bandwidth only applies when the bucket is first created for this key.
    public boolean isAllowed(String endpoint, String ipAddress, Bandwidth bandwidth) {
        byte[] key = ("rate:" + endpoint + ":" + ipAddress).getBytes(StandardCharsets.UTF_8);

        Bucket bucket = proxyManager.builder().build(key, () ->
                BucketConfiguration.builder().addLimit(bandwidth).build());

        return bucket.tryConsume(1);

    }
}
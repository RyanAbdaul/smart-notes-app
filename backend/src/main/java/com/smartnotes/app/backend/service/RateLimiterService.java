package com.smartnotes.app.backend.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for handling rate limiting using Bucket4j.
 */
@Service
public class RateLimiterService {

    /**
     * Map to store buckets for different endpoints and IP addresses.
     * Key format: endpoint:ipAddress
     */
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Strict rate limit configuration: 5 requests per minute.
     * -- GETTER --
     *  Gets the strict bandwidth configuration.
     *
     * @return the strict bandwidth

     */
    @Getter
    private Bandwidth strictBandwidth;

    /**
     * Relaxed rate limit configuration: 30 requests per minute.
     * -- GETTER --
     *  Gets the relaxed bandwidth configuration.
     *
     * @return the relaxed bandwidth

     */
    @Getter
    private Bandwidth relaxedBandwidth;

    @PostConstruct
    public void init() {
        // Strict: 5 requests per minute
        this.strictBandwidth = Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));
        // Relaxed: 30 requests per minute
        this.relaxedBandwidth = Bandwidth.classic(30, Refill.greedy(30, Duration.ofMinutes(1)));
    }

    /**
     * Checks if a request is allowed based on the endpoint and IP address.
     * Uses the strict bandwidth by default (5 requests per minute).
     *
     * @param endpoint The endpoint identifier (e.g., "login", "register")
     * @param ipAddress The client's IP address
     * @return true if the request is allowed, false otherwise
     */
    public boolean isAllowed(String endpoint, String ipAddress) {
        return isAllowed(endpoint, ipAddress, strictBandwidth);
    }

    /**
     * Checks if a request is allowed based on the endpoint, IP address, and bandwidth.
     *
     * @param endpoint The endpoint identifier (e.g., "login", "register")
     * @param ipAddress The client's IP address
     * @param bandwidth The bandwidth configuration to use
     * @return true if the request is allowed, false otherwise
     */
    public boolean isAllowed(String endpoint, String ipAddress, Bandwidth bandwidth) {
        String key = endpoint + ":" + ipAddress;
        Bucket bucket = buckets.computeIfAbsent(key, k -> Bucket.builder()
                .addLimit(bandwidth)
                .build());
        return bucket.tryConsume(1);
    }

}
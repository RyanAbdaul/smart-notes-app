package com.smartnotes.app.backend.config;

import com.smartnotes.app.backend.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import java.util.Arrays;

/**
 * Security configuration class for the Smart Notes API.
 * <p>
 * This class configures Spring Security to:
 * <ul>
 *   <li>Define which endpoints are public vs. authenticated</li>
 *   <li>Disable CSRF (stateless JWT-based auth)</li>
 *   <li>Set a custom authentication entry point for 401 responses</li>
 *   <li>Enforce stateless session policy</li>
 *   <li>Add the JWT authentication filter before the standard username/password filter</li>
 *   <li>Add security response headers (CSP, HSTS, X-Frame-Options, etc.)</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final Environment environment;

    public SecurityConfig(UserRepository userRepository,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          Environment environment) {
        this.userRepository = userRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.environment = environment;
    }

    /**
     * Provides a UserDetailsService that loads user details by email.
     * Used by Spring Security for authentication.
     */
    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Provides a BCrypt password encoder for hashing passwords.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the Spring Security AuthenticationManager as a bean.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Custom AuthenticationEntryPoint that returns a JSON 401 response
     * when an unauthenticated request is made to a protected endpoint.
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setHeader("WWW-Authenticate", "");
            response.getWriter().write("{\"error\": \"Unauthorized access\"}");
        };
    }

    /**
     * Configures the main SecurityFilterChain.
     * <p>
     * - Enables CORS with defaults (defined elsewhere, e.g. CorsConfigurationSource).
     * - Defines public endpoints (login, register, swagger docs) and admin-only routes.
     * - Disables CSRF (stateless JWT).
     * - Adds a custom authentication entry point.
     * - Sets session creation policy to STATELESS.
     * - Adds the JWT filter before the username/password filter.
     * - Adds security response headers.
     *
     * @param HttpSecurity the HttpSecurity to configure
     * @return the built SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        boolean isDev = Arrays.asList(environment.getActiveProfiles()).contains("dev");

        // Enable CORS with default configuration
        http.cors(Customizer.withDefaults());

        // Configure authorization rules
        http.authorizeHttpRequests(configurer -> {
            // Public endpoints - no authentication required
            configurer.requestMatchers(
                    "/auth/login",
                    "/auth/register",
                    "/docs",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resource/**",
                    "/webjars/**"
            ).permitAll();
            // Dev-only token endpoint
            if (isDev) {
                configurer.requestMatchers("/dev/token").permitAll();
            }
            // Admin endpoints require ADMIN role
            configurer.requestMatchers("/admin/**").hasRole("ADMIN")
                    // All other requests require authentication
                    .anyRequest().authenticated();
        });

        // Disable CSRF protection (stateless JWT-based authentication)
        http.csrf(csrf -> csrf.disable());

        // Custom exception handling for unauthenticated requests
        http.exceptionHandling(exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(authenticationEntryPoint()));

        // Stateless session: no session cookies, tokens only
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add JWT authentication filter before the standard username/password filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Configure security response headers
        http.headers(headers -> {
            // Content-Security-Policy: restricts resource loading sources
            // policyDirectives takes a single String with directives separated by semicolons
            headers.contentSecurityPolicy(csp -> csp
                    .policyDirectives(
                            "default-src 'self'; " +          // Only allow resources from same origin
                            "script-src 'self'; " +           // Only allow scripts from same origin
                            "style-src 'self' 'unsafe-inline'; " +  // Allow inline styles for UI frameworks
                            "img-src 'self' data:; " +        // Allow images from same origin and data URIs
                            "font-src 'self'; " +             // Only allow fonts from same origin
                            "connect-src 'self'; " +          // Only allow AJAX/fetch from same origin
                            "frame-ancestors 'self'; " +      // Only allow embedding from same origin
                            "base-uri 'self'; " +             // Restrict <base> tag
                            "form-action 'self'"              // Only allow form submissions to same origin
                    )
            );

            // X-Content-Type-Options: prevent MIME type sniffing
            // Calling contentTypeOptions() enables it (sets to "nosniff")
            headers.contentTypeOptions(contentType -> {});

            // X-Frame-Options: prevent clickjacking
            headers.frameOptions(frameOptions -> frameOptions.sameOrigin());

            // Strict-Transport-Security: enforce HTTPS
            headers.httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)  // 1 year in seconds
            );

            // X-XSS-Protection: enable browser XSS filter
            // enable() is private in Spring Security 6; use the no-arg variant to enable
            headers.xssProtection(xss -> {});

            // Referrer-Policy: control referrer information sent with requests
            // Use the ReferrerPolicy enum, not a String
            headers.referrerPolicy(referrer -> referrer.policy(
                    ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN
            ));
        });

        return http.build();
    }
}

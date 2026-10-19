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
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import java.util.Arrays;

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

    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 19456, 2);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setHeader("WWW-Authenticate", "");
            response.getWriter().write("{\"error\": \"Unauthorized access\"}");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        boolean isDev = Arrays.asList(environment.getActiveProfiles()).contains("dev");

        http.cors(Customizer.withDefaults());

        http.authorizeHttpRequests(configurer -> {
            configurer.requestMatchers(
                    "/auth/login",
                    "/auth/register",
                    "/docs",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resource/**",
                    "/webjars/**"
            ).permitAll();
            if (isDev) {
                configurer.requestMatchers("/dev/token").permitAll();
            }
            configurer.requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated();
        });

        http.csrf(csrf -> csrf.disable());

        http.exceptionHandling(exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(authenticationEntryPoint()));

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers(headers -> {
            headers.contentSecurityPolicy(csp -> csp
                    .policyDirectives(
                            "default-src 'self'; " +
                                    "script-src 'self'; " +
                                    "style-src 'self' 'unsafe-inline'; " +
                                    "img-src 'self' data:; " +
                                    "font-src 'self'; " +
                                    "connect-src 'self'; " +
                                    "frame-ancestors 'self'; " +
                                    "base-uri 'self'; " +
                                    "form-action 'self'"
                    )
            );

            headers.contentTypeOptions(contentType -> {});

            headers.frameOptions(frameOptions -> frameOptions.sameOrigin());

            headers.httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)
            );

            headers.xssProtection(xss -> {});

            headers.referrerPolicy(referrer -> referrer.policy(
                    ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN
            ));
        });

        return http.build();
    }
}
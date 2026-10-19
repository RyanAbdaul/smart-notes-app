package com.smartnotes.app.backend.annotation;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    String endpoint();

    RateLimitType type() default RateLimitType.RELAXED;

}

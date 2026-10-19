package com.smartnotes.app.backend.annotation;
import com.smartnotes.app.backend.repository.Owned;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireOwnership {

    // ? extends bla bla, means any class that extends bla bla can use this annotation
    Class<? extends Owned> entity();


    String idParam() default "id";
}
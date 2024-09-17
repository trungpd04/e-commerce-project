package com.nhom7.ecommercebackend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {DobConstrainValidator.class})
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DobConstrain {
    int min();

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package com.nhom7.ecommercebackend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Slf4j
public class DobConstrainValidator implements ConstraintValidator<DobConstrain, LocalDate> {
    private int min;

    @Override
    public void initialize(DobConstrain constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(localDate)) {
            return true;
        }
        long year = ChronoUnit.YEARS.between(localDate, LocalDate.now());

        return min <= year;
    }
}

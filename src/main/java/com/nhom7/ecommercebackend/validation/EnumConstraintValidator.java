package com.nhom7.ecommercebackend.validation;

import com.nhom7.ecommercebackend.model.OrderStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class EnumConstraintValidator implements ConstraintValidator<EnumConstraint, String> {
    List<String> valueList = null;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return valueList.contains(value);
    }

    @Override
    public void initialize(EnumConstraint constraint) {

        valueList = List.of(constraint.value());
    }
}

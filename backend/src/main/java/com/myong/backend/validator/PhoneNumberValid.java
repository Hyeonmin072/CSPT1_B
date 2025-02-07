package com.myong.backend.validator;

import com.myong.backend.annotation.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneNumberValid implements ConstraintValidator<PhoneNumber, String> {
    private String regexp;//정규표현식

    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
        this.regexp = constraintAnnotation.regexp(); //커스텀 어노테이션이랑 맞는지 비교
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        boolean result = Pattern.matches(regexp, value);

        return result;
    }
}

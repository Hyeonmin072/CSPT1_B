package com.myong.backend.annotation;

import com.myong.backend.validator.PhoneNumberValid;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PhoneNumberValid.class)
@Target({ElementType.FIELD})//필드에만 사용
@Retention(RetentionPolicy.RUNTIME)//런타임중일때만 어노테이션 동작
public @interface PhoneNumber {
    String message() default "핸드폰 번호 양식에 맞지 않습니다. ex) 000-0000-0000";

    String regexp() default "^\\d{2,3}-\\d{3,4}-\\d{4}$";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

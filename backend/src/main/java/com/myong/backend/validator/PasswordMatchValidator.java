package com.myong.backend.validator;

import com.myong.backend.annotation.PasswordMatch;
import com.myong.backend.domain.dto.user.request.UserPasswordUpdateRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserPasswordUpdateRequestDto> {

    @Override
    public boolean isValid(UserPasswordUpdateRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getNewPassword() == null || dto.getNewPasswordConfirm() == null) {
            return false;
        }
        return dto.getNewPassword().equals(dto.getNewPasswordConfirm());
    }
}
package com.myong.backend.domain.dto.user.request;

import com.myong.backend.annotation.PasswordMatch;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@PasswordMatch
@Data
public class UserPasswordUpdateRequestDto {


    public String curPassword;

    @Length(min = 7, message = "비밀번호는 최소 8자 이상이여야 합니다.")
    public String newPassword;

    @Length(min = 7, message = "비밀번호는 최소 8자 이상이여야 합니다.")
    public String newPasswordConfirm;
}

package com.example.string_boot_4.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserForm {
    @Size(min = 3, max = 25)
    @NotEmpty(message = "사용자 ID는 필수항목입니다.")
    private String username;

    @NotEmpty(message = "이메일은 필수항목입니다.")
    @Email
    private String email;

    @Size(min = 3, max = 20)
    @NotEmpty(message = "사용자 이름은 필수항목입니다.")
    private String name;

    private String profileImagePath;
}
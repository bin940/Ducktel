package com.ducktelback.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 6, max = 16, message = "아이디는 6자 이상 16자 이하로 입력해주세요.")
    @Pattern(regexp = "`~!@#$%^&*()-_=+[{]}|;:',<.>/?", message = "아이디는 영문자, 숫자만 입력해주세요.")
    private String username;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 16, message = "비밀번호는 8자 이상 16자 이하로 입력해주세요.")
    private String password;
    @NotBlank(message = "전화번호를 입력해주세요.")
    @Size(min = 10, max = 11, message = "전화번호는 10자 이상 11자 이하로 입력해주세요.")
    private String phoneNumber;
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식에 맞게 입력해주세요.")
    private String email;

}

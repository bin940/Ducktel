package com.ducktelback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 6, max = 16, message = "아이디는 6자 이상 16자 이하로 입력해주세요.")
    @Pattern(regexp = "`~!@#$%^&*()-_=+[{]}|;:',<.>/?", message = "아이디는 영문자, 숫자만 입력해주세요.")
    private String username;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 16, message = "비밀번호는 8자 이상 16자 이하로 입력해주세요.")
    private String password;
}

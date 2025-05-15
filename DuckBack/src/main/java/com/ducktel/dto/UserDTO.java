package com.ducktel.dto;


import com.ducktel.domain.entity.User;
import com.ducktel.validation.CreateUser;
import com.ducktel.validation.UpdateUser;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDTO {
    @NotBlank(message = "아이디를 입력해주세요.", groups = {CreateUser.class, UpdateUser.class})
    @Size(min = 6, max = 16, message = "아이디는 6자 이상 16자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영문자, 숫자만 입력해주세요.")
    private String username;
    @NotBlank(message = "비밀번호를 입력해주세요.", groups = CreateUser.class)
    @Size(min = 8, max = 16, message = "비밀번호는 8자 이상 16자 이하로 입력해주세요.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @NotBlank(message = "전화번호를 입력해주세요.", groups = {CreateUser.class, UpdateUser.class})
    @Size(min = 10, max = 11, message = "전화번호는 10자 이상 11자 이하로 입력해주세요.")
    private String phoneNumber;
    @NotBlank(message = "이메일을 입력해주세요.", groups = {CreateUser.class, UpdateUser.class})
    @Email(message = "이메일 형식에 맞게 입력해주세요.")
    private String email;
    @NotBlank(message = "이름을 입력해주세요.", groups = {CreateUser.class, UpdateUser.class})
    @Size(min =2, max =8, message= "이름은 2자 이상 8자 이하로 입력해주세요.")
    private String name;



    public User createUser(PasswordEncoder passwordEncoder) {
        UUID userIdUUID = UUID.randomUUID();
        User user = new User();
        user.setUserId(userIdUUID);
        user.setUsername(this.username);
        user.setPassword(passwordEncoder.encode(this.password));
        user.setEmail(this.email);
        user.setPhoneNumber(this.phoneNumber);
        user.setName(this.name);
        user.setRole("ROLE_USER");
        return user;
    }
    public UserDTO( String username, String email, String phoneNumber, String name) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }
    public User updateUser(User user) {
       user.setPhoneNumber(this.phoneNumber);
       user.setEmail(this.email);
        return user;
    }



}




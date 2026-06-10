package com.paulstna.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequestDTO {
    @NotBlank
    @Email
    private String email;
    @Size(max = 100)
    private String username;
    @Size(max = 100)
    private String firstName;
    @Size(max = 100)
    private String lastName;
    @Pattern(regexp = "\\+?[1-9]\\d{7,14}")
    private String phoneNumber;
    @Past
    private LocalDate birthdate;
}
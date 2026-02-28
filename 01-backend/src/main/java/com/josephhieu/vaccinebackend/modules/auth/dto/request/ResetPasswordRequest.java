package com.josephhieu.vaccinebackend.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {

    @NotBlank(message = "TOKEN_INVALID")
    private String token;

    @NotBlank(message = "PASSWORD_INVALID")
    @Size(min = 6, message = "PASSWORD_TOO_SHORT")
    private String newPassword;
}

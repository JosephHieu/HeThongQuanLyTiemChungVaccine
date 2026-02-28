package com.josephhieu.vaccinebackend.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {

    @NotBlank(message = "EMAIL_INVALID")
    @jakarta.validation.constraints.Email(message = "EMAIL_FORMAT_INVALID")
    private String email;
}

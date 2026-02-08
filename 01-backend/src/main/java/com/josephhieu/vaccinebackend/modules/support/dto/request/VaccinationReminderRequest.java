package com.josephhieu.vaccinebackend.modules.support.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO để thực hiện gửi email nhắc lịch tiêm.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccinationReminderRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Định dạng email không hợp lệ")
    private String email;

    private String tieuDe;

    private String loiNhan;
}

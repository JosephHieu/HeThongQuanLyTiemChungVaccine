package com.josephhieu.vaccinebackend.modules.vaccination.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VaccineSearchRequest {

    private String criteria;
    private String keyword;

    @Min(value = 0, message = "Số trang không được nhỏ hơn 0")
    private int page = 0;

    @Min(value = 1, message = "Kích thước trang phải lớn hơn 0")
    private int size = 10;
}

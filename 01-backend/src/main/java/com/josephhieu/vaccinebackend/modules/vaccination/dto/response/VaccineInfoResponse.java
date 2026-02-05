package com.josephhieu.vaccinebackend.modules.vaccination.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VaccineInfoResponse {
    private UUID maVacXin;        // 1. UUID
    private UUID maLo;
    private String soLo;
    private String tenVacXin;     // 2. String
    private String phongNguaBenh; // 3. String (Khớp với v.phongNguaBenh trong Query)
    private Integer soLuongLieu;  // 4. int/Integer
    private String doTuoi;        // 5. String (Dùng chung cho độ tuổi)
    private BigDecimal donGia;    // 6. BigDecimal
}
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
public class BatchSummaryResponse {

    private UUID maLo;
    private String soLo;
    private String tenVacXin;
    private Integer soLuongTon;
    private BigDecimal donGia;
    private BigDecimal giaNhap;
    private BigDecimal giaBan;
}

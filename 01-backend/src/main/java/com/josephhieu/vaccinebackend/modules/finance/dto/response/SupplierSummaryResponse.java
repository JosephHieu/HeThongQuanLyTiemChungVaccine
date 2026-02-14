package com.josephhieu.vaccinebackend.modules.finance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierSummaryResponse {

    private BigDecimal totalSpendingThisMonth; // Tổng chi tháng này
    private String spendingTrend;              // Ví dụ: "+15%"
    private BigDecimal totalDebt;              // Công nợ NCC còn lại
    private long overdueInvoices;              // Số hóa đơn quá hạn
}

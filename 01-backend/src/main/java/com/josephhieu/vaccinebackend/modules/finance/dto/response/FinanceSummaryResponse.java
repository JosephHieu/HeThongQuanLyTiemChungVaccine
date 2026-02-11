package com.josephhieu.vaccinebackend.modules.finance.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FinanceSummaryResponse {

    private BigDecimal totalRevenueToday;
    private long pendingInvoiceCount;
    private BigDecimal inventoryValue;
}

package com.goTyolo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefundPolicy {
    @NotNull
    private int refundableUntilDaysBefore;
    @NotNull
    private int cancellationFeePercent;
}

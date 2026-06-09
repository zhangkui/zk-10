package com.coldchain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BatchFlowDTO {

    private Long batchId;

    private Long fromNodeId;

    private Long toNodeId;

    private BigDecimal flowQuantity;

    private Long operatorId;

    private LocalDateTime operateTime;

    private BigDecimal temperature;

    private Integer transportDuration;

    private String remark;
}

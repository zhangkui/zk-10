package com.coldchain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
    private LocalDateTime operateTime;

    private BigDecimal temperature;

    private Integer transportDuration;

    private String remark;
}

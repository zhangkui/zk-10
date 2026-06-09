package com.coldchain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LossRecordDTO {

    private Long batchId;

    private Long flowId;

    private Long nodeId;

    private BigDecimal lossQuantity;

    private String lossType;

    private String lossReason;

    private LocalDateTime discoverTime;

    private Long operatorId;

    private String remark;
}

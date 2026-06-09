package com.coldchain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
    private LocalDateTime discoverTime;

    private Long operatorId;

    private String remark;
}

package com.coldchain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AttributionDTO {

    private Long lossId;

    private Long batchId;

    private Long nodeId;

    private String responsibleParty;

    private String responsibilityType;

    private String responsibilityLevel;

    private BigDecimal confidence;

    private String analysisBasis;

    private String suggestion;

    private Long analystId;
}

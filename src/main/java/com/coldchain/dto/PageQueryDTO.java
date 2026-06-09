package com.coldchain.dto;

import lombok.Data;

@Data
public class PageQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String keyword;

    private String batchNo;

    private Long batchId;

    private Long nodeId;

    private String lossType;

    private String status;

    private String startDate;

    private String endDate;

    private String responsibilityType;

    private String responsibilityLevel;

    private String productName;

    private String category;
}

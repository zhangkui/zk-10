package com.coldchain.dto;

import lombok.Data;

@Data
public class BatchQueryDTO {

    private String batchNo;

    private Long productId;

    private String status;

    private String startDate;

    private String endDate;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}

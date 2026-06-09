package com.coldchain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("responsibility_attribution")
public class ResponsibilityAttribution {

    @TableId(type = IdType.AUTO)
    private Long id;

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

    private LocalDateTime analysisTime;

    private String status;

    private LocalDateTime createTime;
}

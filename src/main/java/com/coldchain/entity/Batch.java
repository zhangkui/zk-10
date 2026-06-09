package com.coldchain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("batch")
public class Batch {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String batchNo;

    private Long productId;

    private BigDecimal quantity;

    private BigDecimal remainingQuantity;

    private BigDecimal totalLoss;

    private BigDecimal lossRate;

    private String origin;

    private LocalDate harvestDate;

    private LocalDate expireDate;

    private String status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

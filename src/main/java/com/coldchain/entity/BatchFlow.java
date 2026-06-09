package com.coldchain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("batch_flow")
public class BatchFlow {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long batchId;

    private Long fromNodeId;

    private Long toNodeId;

    private BigDecimal flowQuantity;

    private Long operatorId;

    private LocalDateTime operateTime;

    private BigDecimal temperature;

    private Integer transportDuration;

    private String remark;

    private LocalDateTime createTime;
}

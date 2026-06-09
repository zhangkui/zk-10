package com.coldchain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("loss_record")
public class LossRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long batchId;

    private Long flowId;

    private Long nodeId;

    private BigDecimal lossQuantity;

    private BigDecimal lossRate;

    private String lossType;

    private String lossReason;

    private LocalDateTime discoverTime;

    private Long operatorId;

    private Integer isAttributed;

    private String status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

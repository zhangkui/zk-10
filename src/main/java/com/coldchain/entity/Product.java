package com.coldchain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String productCode;

    private String productName;

    private String category;

    private String unit;

    private BigDecimal storageTemp;

    private Integer shelfLife;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

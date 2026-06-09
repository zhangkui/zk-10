package com.coldchain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("cold_chain_node")
public class ColdChainNode {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String nodeCode;

    private String nodeName;

    private String nodeType;

    private String address;

    private String manager;

    private String phone;

    private BigDecimal temperature;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

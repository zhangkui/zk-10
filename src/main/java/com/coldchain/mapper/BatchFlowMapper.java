package com.coldchain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coldchain.entity.BatchFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface BatchFlowMapper extends BaseMapper<BatchFlow> {

    @Select("SELECT bf.*, " +
            "fn.node_name AS from_node_name, fn.node_code AS from_node_code, fn.node_type AS from_node_type, " +
            "tn.node_name AS to_node_name, tn.node_code AS to_node_code, tn.node_type AS to_node_type " +
            "FROM batch_flow bf " +
            "LEFT JOIN cold_chain_node fn ON bf.from_node_id = fn.id " +
            "LEFT JOIN cold_chain_node tn ON bf.to_node_id = tn.id " +
            "WHERE bf.batch_id = #{batchId} ORDER BY bf.operate_time ASC")
    List<Map<String, Object>> selectBatchFlowTrace(@Param("batchId") Long batchId);
}


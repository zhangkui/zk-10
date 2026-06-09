package com.coldchain.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coldchain.entity.BatchFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface BatchFlowMapper extends BaseMapper<BatchFlow> {

    @Select("SELECT bf.id, bf.batch_id AS batchId, bf.from_node_id AS fromNodeId, bf.to_node_id AS toNodeId, " +
            "bf.flow_quantity AS flowQuantity, bf.flow_quantity AS quantity, bf.operator_id AS operatorId, bf.operate_time AS operateTime, " +
            "bf.temperature AS temperature, bf.temperature AS transportTemperature, bf.transport_duration AS transportDuration, " +
            "bf.remark, bf.create_time AS createTime, " +
            "b.batch_no AS batchNo, " +
            "fn.node_name AS sourceNodeName, tn.node_name AS targetNodeName, " +
            "su.real_name AS operatorName " +
            "FROM batch_flow bf " +
            "LEFT JOIN batch b ON bf.batch_id = b.id " +
            "LEFT JOIN cold_chain_node fn ON bf.from_node_id = fn.id " +
            "LEFT JOIN cold_chain_node tn ON bf.to_node_id = tn.id " +
            "LEFT JOIN sys_user su ON bf.operator_id = su.id " +
            "${ew.customSqlSegment}")
    IPage<Map<String, Object>> selectFlowDetailPage(Page<Map<String, Object>> page, @Param("ew") Wrapper<BatchFlow> wrapper);

    @Select("SELECT bf.id, bf.batch_id AS batchId, bf.from_node_id AS fromNodeId, bf.to_node_id AS toNodeId, " +
            "bf.flow_quantity AS flowQuantity, bf.flow_quantity AS quantity, bf.operator_id AS operatorId, bf.operate_time AS operateTime, " +
            "bf.temperature AS temperature, bf.temperature AS transportTemperature, bf.transport_duration AS transportDuration, " +
            "bf.remark, bf.create_time AS createTime, " +
            "b.batch_no AS batchNo, " +
            "fn.node_name AS sourceNodeName, tn.node_name AS targetNodeName, " +
            "su.real_name AS operatorName " +
            "FROM batch_flow bf " +
            "LEFT JOIN batch b ON bf.batch_id = b.id " +
            "LEFT JOIN cold_chain_node fn ON bf.from_node_id = fn.id " +
            "LEFT JOIN cold_chain_node tn ON bf.to_node_id = tn.id " +
            "LEFT JOIN sys_user su ON bf.operator_id = su.id " +
            "WHERE bf.batch_id = #{batchId} ORDER BY bf.operate_time DESC")
    List<Map<String, Object>> selectFlowListByBatchId(@Param("batchId") Long batchId);

    @Select("SELECT bf.id, bf.batch_id AS batchId, bf.from_node_id AS fromNodeId, bf.to_node_id AS toNodeId, " +
            "bf.flow_quantity AS flowQuantity, bf.flow_quantity AS quantity, bf.operator_id AS operatorId, bf.operate_time AS operateTime, " +
            "bf.temperature AS temperature, bf.temperature AS transportTemperature, bf.transport_duration AS transportDuration, " +
            "bf.remark, bf.create_time AS createTime, " +
            "b.batch_no AS batchNo, " +
            "fn.node_name AS sourceNodeName, tn.node_name AS targetNodeName, " +
            "su.real_name AS operatorName " +
            "FROM batch_flow bf " +
            "LEFT JOIN batch b ON bf.batch_id = b.id " +
            "LEFT JOIN cold_chain_node fn ON bf.from_node_id = fn.id " +
            "LEFT JOIN cold_chain_node tn ON bf.to_node_id = tn.id " +
            "LEFT JOIN sys_user su ON bf.operator_id = su.id " +
            "ORDER BY bf.operate_time DESC")
    List<Map<String, Object>> selectFlowList();

    @Select("SELECT bf.id, bf.batch_id AS batchId, bf.from_node_id AS fromNodeId, bf.to_node_id AS toNodeId, " +
            "bf.flow_quantity AS flowQuantity, bf.flow_quantity AS quantity, bf.operator_id AS operatorId, bf.operate_time AS operateTime, " +
            "bf.temperature AS temperature, bf.temperature AS transportTemperature, bf.transport_duration AS transportDuration, " +
            "bf.remark, bf.create_time AS createTime, " +
            "fn.node_name AS sourceNodeName, fn.node_code AS fromNodeCode, fn.node_type AS fromNodeType, " +
            "tn.node_name AS targetNodeName, tn.node_code AS toNodeCode, tn.node_type AS toNodeType " +
            "FROM batch_flow bf " +
            "LEFT JOIN cold_chain_node fn ON bf.from_node_id = fn.id " +
            "LEFT JOIN cold_chain_node tn ON bf.to_node_id = tn.id " +
            "WHERE bf.batch_id = #{batchId} ORDER BY bf.operate_time ASC")
    List<Map<String, Object>> selectBatchFlowTrace(@Param("batchId") Long batchId);
}

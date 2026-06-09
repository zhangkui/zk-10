package com.coldchain.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coldchain.entity.LossRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface LossRecordMapper extends BaseMapper<LossRecord> {

    @Select("SELECT lr.id, lr.batch_id AS batchId, lr.flow_id AS flowId, lr.node_id AS nodeId, " +
            "lr.loss_quantity AS lossQuantity, lr.loss_rate AS lossRate, " +
            "lr.loss_type AS lossType, lr.loss_reason AS lossReason, " +
            "lr.discover_time AS discoverTime, lr.operator_id AS operatorId, " +
            "lr.is_attributed AS hasAttribution, lr.status, lr.remark, " +
            "lr.create_time AS createTime, lr.update_time AS updateTime, " +
            "b.batch_no AS batchNo, p.product_name AS productName, " +
            "n.node_name AS nodeName, n.node_code AS nodeCode, " +
            "su.real_name AS operatorName " +
            "FROM loss_record lr " +
            "LEFT JOIN batch b ON lr.batch_id = b.id " +
            "LEFT JOIN product p ON b.product_id = p.id " +
            "LEFT JOIN cold_chain_node n ON lr.node_id = n.id " +
            "LEFT JOIN sys_user su ON lr.operator_id = su.id " +
            "${ew.customSqlSegment}")
    IPage<Map<String, Object>> selectLossRecordPage(Page<Map<String, Object>> page, @Param("ew") Wrapper<LossRecord> wrapper);

    @Select("SELECT lr.id, lr.batch_id AS batchId, lr.flow_id AS flowId, lr.node_id AS nodeId, " +
            "lr.loss_quantity AS lossQuantity, lr.loss_rate AS lossRate, " +
            "lr.loss_type AS lossType, lr.loss_reason AS lossReason, " +
            "lr.discover_time AS discoverTime, lr.operator_id AS operatorId, " +
            "lr.is_attributed AS hasAttribution, lr.status, lr.remark, " +
            "lr.create_time AS createTime, lr.update_time AS updateTime, " +
            "b.batch_no AS batchNo, p.product_name AS productName, " +
            "n.node_name AS nodeName, n.node_code AS nodeCode, " +
            "su.real_name AS operatorName " +
            "FROM loss_record lr " +
            "LEFT JOIN batch b ON lr.batch_id = b.id " +
            "LEFT JOIN product p ON b.product_id = p.id " +
            "LEFT JOIN cold_chain_node n ON lr.node_id = n.id " +
            "LEFT JOIN sys_user su ON lr.operator_id = su.id " +
            "ORDER BY lr.discover_time DESC")
    List<Map<String, Object>> selectLossList();

    @Select("SELECT lr.id, lr.batch_id AS batchId, lr.flow_id AS flowId, lr.node_id AS nodeId, " +
            "lr.loss_quantity AS lossQuantity, lr.loss_rate AS lossRate, " +
            "lr.loss_type AS lossType, lr.loss_reason AS lossReason, " +
            "lr.discover_time AS discoverTime, lr.operator_id AS operatorId, " +
            "lr.is_attributed AS hasAttribution, lr.status, lr.remark, " +
            "lr.create_time AS createTime, lr.update_time AS updateTime, " +
            "b.batch_no AS batchNo, p.product_name AS productName, " +
            "n.node_name AS nodeName, n.node_code AS nodeCode, " +
            "su.real_name AS operatorName " +
            "FROM loss_record lr " +
            "LEFT JOIN batch b ON lr.batch_id = b.id " +
            "LEFT JOIN product p ON b.product_id = p.id " +
            "LEFT JOIN cold_chain_node n ON lr.node_id = n.id " +
            "LEFT JOIN sys_user su ON lr.operator_id = su.id " +
            "WHERE lr.batch_id = #{batchId} ORDER BY lr.discover_time DESC")
    List<Map<String, Object>> selectLossListByBatchId(@Param("batchId") Long batchId);

    @Select("SELECT p.category AS category, p.category AS name, COALESCE(SUM(lr.loss_quantity), 0) AS lossQuantity, COALESCE(SUM(lr.loss_quantity), 0) AS value " +
            "FROM loss_record lr " +
            "LEFT JOIN batch b ON lr.batch_id = b.id " +
            "LEFT JOIN product p ON b.product_id = p.id " +
            "GROUP BY p.category")
    List<Map<String, Object>> selectLossByCategory();

    @Select("SELECT DATE(lr.discover_time) AS date, COALESCE(SUM(lr.loss_quantity), 0) AS lossQuantity " +
            "FROM loss_record lr " +
            "WHERE lr.discover_time >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
            "GROUP BY DATE(lr.discover_time) " +
            "ORDER BY DATE(lr.discover_time) ASC")
    List<Map<String, Object>> selectLossTrend(@Param("days") Integer days);

    @Select("SELECT n.node_name AS nodeName, n.node_name AS name, COALESCE(SUM(lr.loss_quantity), 0) AS lossQuantity, COALESCE(SUM(lr.loss_quantity), 0) AS value " +
            "FROM loss_record lr " +
            "LEFT JOIN cold_chain_node n ON lr.node_id = n.id " +
            "GROUP BY n.node_name")
    List<Map<String, Object>> selectLossByNode();

    @Select("SELECT lr.loss_type AS lossType, lr.loss_type AS name, COALESCE(SUM(lr.loss_quantity), 0) AS lossQuantity, COALESCE(SUM(lr.loss_quantity), 0) AS value " +
            "FROM loss_record lr " +
            "GROUP BY lr.loss_type")
    List<Map<String, Object>> selectLossByType();
}

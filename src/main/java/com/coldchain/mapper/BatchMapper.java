package com.coldchain.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coldchain.entity.Batch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface BatchMapper extends BaseMapper<Batch> {

    @Select("SELECT b.id, b.batch_no AS batchNo, b.product_id AS productId, b.quantity, b.remaining_quantity AS remainingQuantity, " +
            "b.total_loss AS totalLoss, b.loss_rate AS lossRate, b.origin, b.harvest_date AS harvestDate, b.expire_date AS expireDate, " +
            "b.status, b.remark, b.create_time AS createTime, b.update_time AS updateTime, " +
            "p.product_name AS productName, p.product_code AS productCode, p.category, p.unit, p.storage_temp AS storageTemp " +
            "FROM batch b LEFT JOIN product p ON b.product_id = p.id ${ew.customSqlSegment}")
    IPage<Map<String, Object>> selectBatchDetailPage(Page<Map<String, Object>> page, @Param("ew") Wrapper<Batch> wrapper);

    @Select("SELECT b.id, b.batch_no AS batchNo, b.product_id AS productId, b.quantity, b.remaining_quantity AS remainingQuantity, " +
            "b.total_loss AS totalLoss, b.loss_rate AS lossRate, b.origin, b.harvest_date AS harvestDate, b.expire_date AS expireDate, " +
            "b.status, b.remark, b.create_time AS createTime, b.update_time AS updateTime, " +
            "p.product_name AS productName, p.product_code AS productCode, p.category, p.unit, p.storage_temp AS storageTemp " +
            "FROM batch b LEFT JOIN product p ON b.product_id = p.id WHERE b.id = #{id}")
    Map<String, Object> selectBatchDetail(@Param("id") Long id);

    @Select("SELECT b.id, b.batch_no AS batchNo, b.product_id AS productId, b.quantity, b.remaining_quantity AS remainingQuantity, " +
            "b.total_loss AS totalLoss, b.loss_rate AS lossRate, b.origin, b.harvest_date AS harvestDate, b.expire_date AS expireDate, " +
            "b.status, b.remark, b.create_time AS createTime, b.update_time AS updateTime, " +
            "p.product_name AS productName, p.product_code AS productCode, p.category, p.unit, p.storage_temp AS storageTemp " +
            "FROM batch b LEFT JOIN product p ON b.product_id = p.id ORDER BY b.create_time DESC")
    List<Map<String, Object>> selectBatchList();

    @Select("SELECT COUNT(*) FROM batch")
    Long selectTotalBatchCount();

    @Select("SELECT COALESCE(SUM(total_loss), 0) FROM batch")
    BigDecimal selectTotalLossQuantity();

    @Select("SELECT COALESCE(AVG(loss_rate), 0) FROM batch")
    BigDecimal selectAvgLossRate();
}

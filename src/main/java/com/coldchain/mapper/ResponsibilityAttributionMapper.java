package com.coldchain.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coldchain.entity.ResponsibilityAttribution;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ResponsibilityAttributionMapper extends BaseMapper<ResponsibilityAttribution> {

    @Select("SELECT ra.id, ra.loss_id, ra.batch_id, ra.node_id, " +
            "ra.responsible_party AS responsibleParty, " +
            "ra.responsibility_type AS responsibilityType, " +
            "ra.responsibility_level AS responsibilityLevel, " +
            "ra.confidence, " +
            "ra.analysis_basis AS analysisBasis, " +
            "ra.suggestion AS improvementSuggestion, " +
            "ra.analyst_id, " +
            "ra.analysis_time AS analysisTime, " +
            "ra.status, ra.create_time AS createTime, " +
            "lr.loss_quantity AS lossQuantity, lr.loss_type AS lossType, lr.loss_reason AS lossReason, b.batch_no AS batchNo, n.node_name AS nodeName, " +
            "su.real_name AS analystName " +
            "FROM responsibility_attribution ra " +
            "LEFT JOIN loss_record lr ON ra.loss_id = lr.id " +
            "LEFT JOIN batch b ON ra.batch_id = b.id " +
            "LEFT JOIN cold_chain_node n ON ra.node_id = n.id " +
            "LEFT JOIN sys_user su ON ra.analyst_id = su.id " +
            "${ew.customSqlSegment}")
    IPage<Map<String, Object>> selectAttributionPage(Page<Map<String, Object>> page, @Param("ew") Wrapper<ResponsibilityAttribution> wrapper);

    @Select("SELECT ra.id, ra.loss_id, ra.batch_id, ra.node_id, " +
            "ra.responsible_party AS responsibleParty, " +
            "ra.responsibility_type AS responsibilityType, " +
            "ra.responsibility_level AS responsibilityLevel, " +
            "ra.confidence, " +
            "ra.analysis_basis AS analysisBasis, " +
            "ra.suggestion AS improvementSuggestion, " +
            "ra.analyst_id, " +
            "ra.analysis_time AS analysisTime, " +
            "ra.status, ra.create_time, " +
            "lr.loss_quantity AS lossQuantity, lr.loss_type AS lossType, lr.loss_reason AS lossReason, b.batch_no, n.node_name AS nodeName, " +
            "su.real_name AS analystName " +
            "FROM responsibility_attribution ra " +
            "LEFT JOIN loss_record lr ON ra.loss_id = lr.id " +
            "LEFT JOIN batch b ON ra.batch_id = b.id " +
            "LEFT JOIN cold_chain_node n ON ra.node_id = n.id " +
            "LEFT JOIN sys_user su ON ra.analyst_id = su.id " +
            "WHERE ra.id = #{id}")
    Map<String, Object> selectAttributionDetail(@Param("id") Long id);

    @Select("SELECT ra.responsibility_type AS name, COALESCE(SUM(lr.loss_quantity), 0) AS value " +
            "FROM responsibility_attribution ra " +
            "LEFT JOIN loss_record lr ON ra.loss_id = lr.id " +
            "GROUP BY ra.responsibility_type")
    List<Map<String, Object>> selectLossByResponsibility();
}


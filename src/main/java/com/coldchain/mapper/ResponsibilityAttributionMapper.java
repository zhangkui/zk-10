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

    @Select("SELECT ra.*, lr.loss_quantity, lr.loss_type, lr.loss_reason, b.batch_no, n.node_name " +
            "FROM responsibility_attribution ra " +
            "LEFT JOIN loss_record lr ON ra.loss_id = lr.id " +
            "LEFT JOIN batch b ON ra.batch_id = b.id " +
            "LEFT JOIN cold_chain_node n ON ra.node_id = n.id " +
            "${ew.customSqlSegment}")
    IPage<Map<String, Object>> selectAttributionPage(Page<Map<String, Object>> page, @Param("ew") Wrapper<ResponsibilityAttribution> wrapper);

    @Select("SELECT ra.responsibility_type AS name, COALESCE(SUM(lr.loss_quantity), 0) AS value " +
            "FROM responsibility_attribution ra " +
            "LEFT JOIN loss_record lr ON ra.loss_id = lr.id " +
            "GROUP BY ra.responsibility_type")
    List<Map<String, Object>> selectLossByResponsibility();
}


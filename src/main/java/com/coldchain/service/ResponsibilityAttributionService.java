package com.coldchain.service;

import com.coldchain.common.PageResult;
import com.coldchain.dto.AttributionDTO;
import com.coldchain.dto.PageQueryDTO;
import com.coldchain.entity.ResponsibilityAttribution;

import java.util.List;
import java.util.Map;

public interface ResponsibilityAttributionService {

    ResponsibilityAttribution getById(Long id);

    List<ResponsibilityAttribution> list();

    PageResult<ResponsibilityAttribution> page(PageQueryDTO dto);

    boolean save(ResponsibilityAttribution entity);

    boolean update(ResponsibilityAttribution entity);

    boolean delete(Long id);

    boolean addAttribution(AttributionDTO dto);

    Map<String, Object> analyzeLoss(Long lossId);

    PageResult<Map<String, Object>> getAttributionPage(PageQueryDTO dto);

    Map<String, Object> getAttributionDetail(Long id);
}

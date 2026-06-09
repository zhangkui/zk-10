package com.coldchain.service;

import com.coldchain.common.PageResult;
import com.coldchain.dto.LossRecordDTO;
import com.coldchain.dto.PageQueryDTO;
import com.coldchain.entity.LossRecord;

import java.util.List;
import java.util.Map;

public interface LossRecordService {

    LossRecord getById(Long id);

    List<LossRecord> list();

    PageResult<LossRecord> page(PageQueryDTO dto);

    boolean save(LossRecord entity);

    boolean update(LossRecord entity);

    boolean delete(Long id);

    boolean addLossRecord(LossRecordDTO dto);

    PageResult<Map<String, Object>> getLossRecordPage(PageQueryDTO dto);

    List<Map<String, Object>> getLossList();
}

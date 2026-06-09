package com.coldchain.service;

import com.coldchain.common.PageResult;
import com.coldchain.dto.BatchQueryDTO;
import com.coldchain.dto.PageQueryDTO;
import com.coldchain.entity.Batch;

import java.util.List;
import java.util.Map;

public interface BatchService {

    Batch getById(Long id);

    List<Batch> list();

    PageResult<Batch> page(PageQueryDTO dto);

    boolean save(Batch entity);

    boolean update(Batch entity);

    boolean delete(Long id);

    boolean createBatch(Batch batch);

    PageResult<Map<String, Object>> getBatchDetailPage(BatchQueryDTO dto);

    Map<String, Object> getBatchDetail(Long id);
}

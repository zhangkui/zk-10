package com.coldchain.service;

import com.coldchain.common.PageResult;
import com.coldchain.dto.BatchFlowDTO;
import com.coldchain.dto.PageQueryDTO;
import com.coldchain.entity.BatchFlow;

import java.util.List;
import java.util.Map;

public interface BatchFlowService {

    BatchFlow getById(Long id);

    List<BatchFlow> list();

    PageResult<BatchFlow> page(PageQueryDTO dto);

    boolean save(BatchFlow entity);

    boolean update(BatchFlow entity);

    boolean delete(Long id);

    boolean addFlow(BatchFlowDTO dto);

    List<Map<String, Object>> getBatchFlowTrace(Long batchId);
}

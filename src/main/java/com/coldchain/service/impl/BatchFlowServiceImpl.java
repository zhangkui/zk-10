package com.coldchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coldchain.common.PageResult;
import com.coldchain.dto.BatchFlowDTO;
import com.coldchain.dto.PageQueryDTO;
import com.coldchain.entity.Batch;
import com.coldchain.entity.BatchFlow;
import com.coldchain.mapper.BatchFlowMapper;
import com.coldchain.mapper.BatchMapper;
import com.coldchain.service.BatchFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class BatchFlowServiceImpl extends ServiceImpl<BatchFlowMapper, BatchFlow> implements BatchFlowService {

    @Autowired
    private BatchMapper batchMapper;

    @Override
    public BatchFlow getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<BatchFlow> list() {
        return baseMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public PageResult<BatchFlow> page(PageQueryDTO dto) {
        Page<BatchFlow> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<BatchFlow> wrapper = new LambdaQueryWrapper<>();
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            wrapper.like(BatchFlow::getBatchId, dto.getKeyword());
        }
        wrapper.orderByDesc(BatchFlow::getOperateTime);
        page = baseMapper.selectPage(page, wrapper);
        return PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords());
    }

    @Override
    public boolean save(BatchFlow entity) {
        return baseMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(BatchFlow entity) {
        return baseMapper.updateById(entity) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public boolean addFlow(BatchFlowDTO dto) {
        if (dto.getBatchId() == null) {
            throw new IllegalArgumentException("批次不能为空");
        }
        if (dto.getToNodeId() == null) {
            throw new IllegalArgumentException("目标节点不能为空");
        }
        if (dto.getOperatorId() == null) {
            throw new IllegalArgumentException("操作人不能为空");
        }
        if (dto.getFlowQuantity() == null || dto.getFlowQuantity().signum() <= 0) {
            throw new IllegalArgumentException("流转数量必须大于 0");
        }

        Batch batch = batchMapper.selectById(dto.getBatchId());
        if (batch == null) {
            throw new RuntimeException("批次不存在");
        }
        if (batch.getRemainingQuantity().compareTo(dto.getFlowQuantity()) < 0) {
            throw new RuntimeException("批次剩余数量不足");
        }

        BatchFlow batchFlow = new BatchFlow();
        batchFlow.setBatchId(dto.getBatchId());
        batchFlow.setFromNodeId(dto.getFromNodeId());
        batchFlow.setToNodeId(dto.getToNodeId());
        batchFlow.setFlowQuantity(dto.getFlowQuantity());
        batchFlow.setOperatorId(dto.getOperatorId());
        batchFlow.setOperateTime(dto.getOperateTime() != null ? dto.getOperateTime() : LocalDateTime.now());
        batchFlow.setTemperature(dto.getTemperature());
        batchFlow.setTransportDuration(dto.getTransportDuration());
        batchFlow.setRemark(dto.getRemark());
        batchFlow.setCreateTime(LocalDateTime.now());

        int result = baseMapper.insert(batchFlow);
        if (result > 0) {
            batch.setStatus("in_transit");
            batch.setUpdateTime(LocalDateTime.now());
            batchMapper.updateById(batch);
        }
        return result > 0;
    }

    @Override
    public List<Map<String, Object>> getBatchFlowTrace(Long batchId) {
        return baseMapper.selectBatchFlowTrace(batchId);
    }

    @Override
    public List<Map<String, Object>> getFlowList(Long batchId) {
        if (batchId != null) {
            return baseMapper.selectFlowListByBatchId(batchId);
        }
        return baseMapper.selectFlowList();
    }

    @Override
    public PageResult<Map<String, Object>> getFlowDetailPage(PageQueryDTO dto, Long batchId) {
        Page<Map<String, Object>> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<BatchFlow> wrapper = new LambdaQueryWrapper<>();
        if (batchId != null) {
            wrapper.eq(BatchFlow::getBatchId, batchId);
        }
        if (dto.getBatchId() != null) {
            wrapper.eq(BatchFlow::getBatchId, dto.getBatchId());
        }
        if (dto.getBatchNo() != null && !dto.getBatchNo().isBlank()) {
            List<Long> batchIds = batchMapper.selectList(new LambdaQueryWrapper<Batch>()
                            .like(Batch::getBatchNo, dto.getBatchNo().trim()))
                    .stream()
                    .map(Batch::getId)
                    .collect(Collectors.toList());
            wrapper.in(BatchFlow::getBatchId, batchIds.isEmpty() ? Collections.singletonList(-1L) : batchIds);
        }
        if (dto.getNodeId() != null) {
            wrapper.and(w -> w.eq(BatchFlow::getFromNodeId, dto.getNodeId()).or().eq(BatchFlow::getToNodeId, dto.getNodeId()));
        }
        if (dto.getStartDate() != null && !dto.getStartDate().isBlank()) {
            wrapper.ge(BatchFlow::getOperateTime, LocalDateTime.of(LocalDate.parse(dto.getStartDate()), LocalTime.MIN));
        }
        if (dto.getEndDate() != null && !dto.getEndDate().isBlank()) {
            wrapper.le(BatchFlow::getOperateTime, LocalDateTime.of(LocalDate.parse(dto.getEndDate()), LocalTime.MAX));
        }
        wrapper.orderByDesc(BatchFlow::getOperateTime);
        Page<Map<String, Object>> result = (Page<Map<String, Object>>) baseMapper.selectFlowDetailPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }
}

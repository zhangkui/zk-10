package com.coldchain.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coldchain.common.PageResult;
import com.coldchain.dto.BatchQueryDTO;
import com.coldchain.dto.PageQueryDTO;
import com.coldchain.entity.Batch;
import com.coldchain.mapper.BatchMapper;
import com.coldchain.service.BatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class BatchServiceImpl extends ServiceImpl<BatchMapper, Batch> implements BatchService {

    @Override
    public Batch getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<Batch> list() {
        return baseMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public PageResult<Batch> page(PageQueryDTO dto) {
        Page<Batch> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Batch> wrapper = new LambdaQueryWrapper<>();
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            wrapper.like(Batch::getBatchNo, dto.getKeyword());
        }
        wrapper.orderByDesc(Batch::getCreateTime);
        page = baseMapper.selectPage(page, wrapper);
        return PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords());
    }

    @Override
    public boolean save(Batch entity) {
        return baseMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(Batch entity) {
        if (entity.getStatus() != null && !entity.getStatus().isBlank()) {
            entity.setStatus(entity.getStatus().trim());
        }
        entity.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(entity) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public boolean createBatch(Batch batch) {
        if (batch.getProductId() == null) {
            throw new IllegalArgumentException("产品不能为空");
        }
        if (batch.getQuantity() == null || batch.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("数量必须大于 0");
        }

        String batchNo = "B" + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + IdUtil.getSnowflakeNextIdStr().substring(8);
        batch.setBatchNo(batchNo);
        batch.setRemainingQuantity(batch.getQuantity());
        batch.setTotalLoss(BigDecimal.ZERO);
        batch.setLossRate(BigDecimal.ZERO);
        batch.setStatus(batch.getStatus() == null || batch.getStatus().isBlank() ? "in_storage" : batch.getStatus().trim());
        batch.setCreateTime(LocalDateTime.now());
        batch.setUpdateTime(LocalDateTime.now());
        return baseMapper.insert(batch) > 0;
    }

    @Override
    public PageResult<Map<String, Object>> getBatchDetailPage(BatchQueryDTO dto) {
        Page<Map<String, Object>> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        QueryWrapper<Batch> wrapper = new QueryWrapper<>();
        if (dto.getBatchNo() != null && !dto.getBatchNo().isBlank()) {
            wrapper.like("b.batch_no", dto.getBatchNo().trim());
        }
        if (dto.getProductId() != null) {
            wrapper.eq("b.product_id", dto.getProductId());
        }
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            wrapper.eq("b.status", dto.getStatus().trim());
        }
        if (dto.getStartDate() != null && !dto.getStartDate().isBlank()) {
            try {
                LocalDateTime startDateTime = LocalDateTime.of(LocalDate.parse(dto.getStartDate()), LocalTime.MIN);
                wrapper.ge("b.create_time", startDateTime);
            } catch (Exception ignored) {
            }
        }
        if (dto.getEndDate() != null && !dto.getEndDate().isBlank()) {
            try {
                LocalDateTime endDateTime = LocalDateTime.of(LocalDate.parse(dto.getEndDate()), LocalTime.MAX);
                wrapper.le("b.create_time", endDateTime);
            } catch (Exception ignored) {
            }
        }
        wrapper.orderByDesc("b.create_time");
        Page<Map<String, Object>> result = (Page<Map<String, Object>>) baseMapper.selectBatchDetailPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public Map<String, Object> getBatchDetail(Long id) {
        return baseMapper.selectBatchDetail(id);
    }

    @Override
    public List<Map<String, Object>> getBatchList() {
        return baseMapper.selectBatchList();
    }
}

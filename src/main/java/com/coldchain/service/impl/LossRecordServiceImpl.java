package com.coldchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coldchain.common.PageResult;
import com.coldchain.dto.LossRecordDTO;
import com.coldchain.dto.PageQueryDTO;
import com.coldchain.entity.Batch;
import com.coldchain.entity.LossRecord;
import com.coldchain.mapper.BatchMapper;
import com.coldchain.mapper.LossRecordMapper;
import com.coldchain.service.LossRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class LossRecordServiceImpl extends ServiceImpl<LossRecordMapper, LossRecord> implements LossRecordService {

    @Autowired
    private BatchMapper batchMapper;

    @Override
    public LossRecord getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<LossRecord> list() {
        return baseMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public PageResult<LossRecord> page(PageQueryDTO dto) {
        Page<LossRecord> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<LossRecord> wrapper = new LambdaQueryWrapper<>();
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            wrapper.like(LossRecord::getLossType, dto.getKeyword())
                    .or().like(LossRecord::getLossReason, dto.getKeyword());
        }
        wrapper.orderByDesc(LossRecord::getDiscoverTime);
        page = baseMapper.selectPage(page, wrapper);
        return PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords());
    }

    @Override
    public boolean save(LossRecord entity) {
        return baseMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(LossRecord entity) {
        return baseMapper.updateById(entity) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public boolean addLossRecord(LossRecordDTO dto) {
        Batch batch = batchMapper.selectById(dto.getBatchId());
        if (batch == null) {
            throw new RuntimeException("批次不存在");
        }

        LossRecord lossRecord = new LossRecord();
        lossRecord.setBatchId(dto.getBatchId());
        lossRecord.setFlowId(dto.getFlowId());
        lossRecord.setNodeId(dto.getNodeId());
        lossRecord.setLossQuantity(dto.getLossQuantity());
        lossRecord.setLossType(dto.getLossType());
        lossRecord.setLossReason(dto.getLossReason());
        lossRecord.setDiscoverTime(dto.getDiscoverTime() != null ? dto.getDiscoverTime() : LocalDateTime.now());
        lossRecord.setOperatorId(dto.getOperatorId());
        lossRecord.setIsAttributed(0);
        lossRecord.setStatus("PENDING");
        lossRecord.setRemark(dto.getRemark());
        lossRecord.setCreateTime(LocalDateTime.now());
        lossRecord.setUpdateTime(LocalDateTime.now());

        BigDecimal lossRate = dto.getLossQuantity().divide(batch.getQuantity(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        lossRecord.setLossRate(lossRate);

        int result = baseMapper.insert(lossRecord);
        if (result > 0) {
            BigDecimal newTotalLoss = batch.getTotalLoss().add(dto.getLossQuantity());
            BigDecimal newLossRate = newTotalLoss.divide(batch.getQuantity(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            batch.setTotalLoss(newTotalLoss);
            batch.setLossRate(newLossRate);
            batch.setRemainingQuantity(batch.getRemainingQuantity().subtract(dto.getLossQuantity()));
            batch.setUpdateTime(LocalDateTime.now());
            batchMapper.updateById(batch);
        }
        return result > 0;
    }

    @Override
    public PageResult<Map<String, Object>> getLossRecordPage(PageQueryDTO dto) {
        Page<Map<String, Object>> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<LossRecord> wrapper = new LambdaQueryWrapper<>();
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(LossRecord::getLossType, dto.getKeyword())
                    .or().like(LossRecord::getLossReason, dto.getKeyword()));
        }
        wrapper.orderByDesc(LossRecord::getDiscoverTime);
        Page<Map<String, Object>> result = (Page<Map<String, Object>>) baseMapper.selectLossRecordPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }
}

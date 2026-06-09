package com.coldchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coldchain.common.PageResult;
import com.coldchain.dto.AttributionDTO;
import com.coldchain.dto.PageQueryDTO;
import com.coldchain.entity.Batch;
import com.coldchain.entity.BatchFlow;
import com.coldchain.entity.LossRecord;
import com.coldchain.entity.Product;
import com.coldchain.entity.ResponsibilityAttribution;
import com.coldchain.mapper.BatchFlowMapper;
import com.coldchain.mapper.BatchMapper;
import com.coldchain.mapper.LossRecordMapper;
import com.coldchain.mapper.ProductMapper;
import com.coldchain.mapper.ResponsibilityAttributionMapper;
import com.coldchain.service.ResponsibilityAttributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class ResponsibilityAttributionServiceImpl extends ServiceImpl<ResponsibilityAttributionMapper, ResponsibilityAttribution> implements ResponsibilityAttributionService {

    @Autowired
    private LossRecordMapper lossRecordMapper;

    @Autowired
    private BatchFlowMapper batchFlowMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private BatchMapper batchMapper;

    @Override
    public ResponsibilityAttribution getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<ResponsibilityAttribution> list() {
        return baseMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public PageResult<ResponsibilityAttribution> page(PageQueryDTO dto) {
        Page<ResponsibilityAttribution> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<ResponsibilityAttribution> wrapper = new LambdaQueryWrapper<>();
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            wrapper.like(ResponsibilityAttribution::getResponsibleParty, dto.getKeyword())
                    .or().like(ResponsibilityAttribution::getResponsibilityType, dto.getKeyword());
        }
        wrapper.orderByDesc(ResponsibilityAttribution::getAnalysisTime);
        page = baseMapper.selectPage(page, wrapper);
        return PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords());
    }

    @Override
    public boolean save(ResponsibilityAttribution entity) {
        return baseMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(ResponsibilityAttribution entity) {
        return baseMapper.updateById(entity) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public boolean addAttribution(AttributionDTO dto) {
        if (dto.getLossId() == null) {
            throw new IllegalArgumentException("损耗记录不能为空");
        }
        if (dto.getResponsibleParty() == null || dto.getResponsibleParty().isBlank()) {
            throw new IllegalArgumentException("责任方不能为空");
        }
        if (dto.getResponsibilityType() == null || dto.getResponsibilityType().isBlank()) {
            throw new IllegalArgumentException("责任类型不能为空");
        }
        if (dto.getResponsibilityLevel() == null || dto.getResponsibilityLevel().isBlank()) {
            throw new IllegalArgumentException("责任等级不能为空");
        }
        if (dto.getConfidence() == null) {
            throw new IllegalArgumentException("置信度不能为空");
        }
        if (dto.getAnalystId() == null) {
            throw new IllegalArgumentException("分析人不能为空");
        }

        LossRecord lossRecord = lossRecordMapper.selectById(dto.getLossId());
        if (lossRecord == null) {
            throw new RuntimeException("损耗记录不存在");
        }

        ResponsibilityAttribution attribution = new ResponsibilityAttribution();
        attribution.setLossId(dto.getLossId());
        attribution.setBatchId(dto.getBatchId());
        attribution.setNodeId(dto.getNodeId());
        attribution.setResponsibleParty(dto.getResponsibleParty());
        attribution.setResponsibilityType(dto.getResponsibilityType());
        attribution.setResponsibilityLevel(dto.getResponsibilityLevel());
        attribution.setConfidence(dto.getConfidence());
        attribution.setAnalysisBasis(dto.getAnalysisBasis());
        attribution.setSuggestion(dto.getSuggestion());
        attribution.setAnalystId(dto.getAnalystId());
        attribution.setAnalysisTime(LocalDateTime.now());
        attribution.setStatus("confirmed");
        attribution.setCreateTime(LocalDateTime.now());

        int result = baseMapper.insert(attribution);
        if (result > 0) {
            lossRecord.setIsAttributed(1);
            lossRecord.setStatus("attributed");
            lossRecord.setUpdateTime(LocalDateTime.now());
            lossRecordMapper.updateById(lossRecord);
        }
        return result > 0;
    }

    @Override
    public Map<String, Object> analyzeLoss(Long lossId) {
        LossRecord lossRecord = lossRecordMapper.selectById(lossId);
        if (lossRecord == null) {
            throw new RuntimeException("损耗记录不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("lossId", lossId);
        result.put("lossQuantity", lossRecord.getLossQuantity());
        result.put("lossType", lossRecord.getLossType());
        result.put("lossReason", lossRecord.getLossReason());

        LambdaQueryWrapper<BatchFlow> flowWrapper = new LambdaQueryWrapper<>();
        flowWrapper.eq(BatchFlow::getBatchId, lossRecord.getBatchId());
        flowWrapper.orderByDesc(BatchFlow::getOperateTime);
        flowWrapper.last("LIMIT 5");
        List<BatchFlow> recentFlows = batchFlowMapper.selectList(flowWrapper);
        result.put("recentFlows", recentFlows);

        if (!recentFlows.isEmpty()) {
            BatchFlow lastFlow = recentFlows.get(0);
            result.put("lastTemperature", lastFlow.getTemperature());
            result.put("lastTransportDuration", lastFlow.getTransportDuration());

            Product product = productMapper.selectById(getBatchProductId(lossRecord.getBatchId()));

            BigDecimal confidence = new BigDecimal("50");
            String responsibleParty = "待确认";
            String responsibilityType = "待分析";
            String responsibilityLevel = "secondary";
            StringBuilder analysisBasis = new StringBuilder();

            if (lastFlow.getTemperature() != null && product != null && product.getStorageTemp() != null) {
                BigDecimal tempDiff = lastFlow.getTemperature().subtract(product.getStorageTemp()).abs();
                analysisBasis.append("运输温度偏差: ").append(tempDiff).append("℃; ");
                if (tempDiff.compareTo(new BigDecimal("5")) > 0) {
                    confidence = confidence.add(new BigDecimal("25"));
                    responsibleParty = "运输部门";
                    responsibilityType = "温度控制不当";
                    responsibilityLevel = "primary";
                } else if (tempDiff.compareTo(new BigDecimal("2")) > 0) {
                    confidence = confidence.add(new BigDecimal("15"));
                    responsibleParty = "运输部门";
                    responsibilityType = "温度控制不当";
                    responsibilityLevel = "secondary";
                }
            }

            if (lastFlow.getTransportDuration() != null) {
                analysisBasis.append("运输时长: ").append(lastFlow.getTransportDuration()).append(" 分钟; ");
                if (lastFlow.getTransportDuration() > 2880) {
                    confidence = confidence.add(new BigDecimal("15"));
                    if ("待分析".equals(responsibilityType)) {
                        responsibleParty = "物流调度";
                        responsibilityType = "运输时长过长";
                        responsibilityLevel = "primary";
                    }
                } else if (lastFlow.getTransportDuration() > 1440) {
                    confidence = confidence.add(new BigDecimal("10"));
                }
            }

            if (lossRecord.getLossType() != null) {
                analysisBasis.append("损耗类型: ").append(lossRecord.getLossType()).append("; ");
                if (("变质".equals(lossRecord.getLossType()) || "腐烂".equals(lossRecord.getLossType())) && "待分析".equals(responsibilityType)) {
                    responsibleParty = "仓储部门";
                    responsibilityType = "仓储管理不当";
                    responsibilityLevel = "secondary";
                }
            }

            if (confidence.compareTo(new BigDecimal("95")) > 0) {
                confidence = new BigDecimal("95");
            }

            result.put("confidence", confidence);
            result.put("responsibleParty", responsibleParty);
            result.put("responsibilityType", responsibilityType);
            result.put("responsibilityLevel", responsibilityLevel);
            result.put("analysisBasis", analysisBasis.toString());
            result.put("suggestion", generateSuggestion(responsibilityType, responsibilityLevel));
        } else {
            result.put("confidence", new BigDecimal("30"));
            result.put("responsibleParty", "待确认");
            result.put("responsibilityType", "数据不足");
            result.put("responsibilityLevel", "secondary");
            result.put("analysisBasis", "无流转记录，无法进行有效分析");
            result.put("suggestion", "建议补充流转数据后重新分析");
        }

        return result;
    }

    private Long getBatchProductId(Long batchId) {
        Batch batch = batchMapper.selectById(batchId);
        return batch != null ? batch.getProductId() : null;
    }

    private String generateSuggestion(String responsibilityType, String responsibilityLevel) {
        StringBuilder suggestion = new StringBuilder();
        if ("温度控制不当".equals(responsibilityType)) {
            suggestion.append("建议加强运输过程温度监控。");
            if ("primary".equals(responsibilityLevel)) {
                suggestion.append("立即整改冷链设备，并对相关责任人进行培训。");
            } else {
                suggestion.append("定期校准温度传感器，优化运输路线。");
            }
        } else if ("运输时长过长".equals(responsibilityType)) {
            suggestion.append("建议优化物流调度系统。");
            if ("primary".equals(responsibilityLevel)) {
                suggestion.append("建立紧急运输预案，增加运力储备。");
            } else {
                suggestion.append("合理规划运输路线，提高运输效率。");
            }
        } else if ("仓储管理不当".equals(responsibilityType)) {
            suggestion.append("建议加强仓储环境监控，建立定期巡检制度，完善出入库管理流程。");
        } else {
            suggestion.append("建议深入调查具体原因，完善全链条监管机制。");
        }
        return suggestion.toString();
    }

    @Override
    public PageResult<Map<String, Object>> getAttributionPage(PageQueryDTO dto) {
        Page<Map<String, Object>> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<ResponsibilityAttribution> wrapper = new LambdaQueryWrapper<>();
        if (dto.getBatchNo() != null && !dto.getBatchNo().isBlank()) {
            List<Long> batchIds = batchMapper.selectList(new LambdaQueryWrapper<Batch>()
                            .like(Batch::getBatchNo, dto.getBatchNo().trim()))
                    .stream()
                    .map(Batch::getId)
                    .collect(Collectors.toList());
            wrapper.in(ResponsibilityAttribution::getBatchId, batchIds.isEmpty() ? Collections.singletonList(-1L) : batchIds);
        }
        if (dto.getResponsibilityType() != null && !dto.getResponsibilityType().isBlank()) {
            wrapper.eq(ResponsibilityAttribution::getResponsibilityType, dto.getResponsibilityType().trim());
        }
        if (dto.getResponsibilityLevel() != null && !dto.getResponsibilityLevel().isBlank()) {
            wrapper.eq(ResponsibilityAttribution::getResponsibilityLevel, dto.getResponsibilityLevel().trim());
        }
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            wrapper.eq(ResponsibilityAttribution::getStatus, dto.getStatus().trim());
        }
        wrapper.orderByDesc(ResponsibilityAttribution::getAnalysisTime);
        Page<Map<String, Object>> result = (Page<Map<String, Object>>) baseMapper.selectAttributionPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public Map<String, Object> getAttributionDetail(Long id) {
        return baseMapper.selectAttributionDetail(id);
    }

    @Override
    public List<Map<String, Object>> getAttributionList(Long batchId) {
        return baseMapper.selectAttributionListByBatchId(batchId);
    }
}

package com.coldchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coldchain.common.PageResult;
import com.coldchain.dto.AttributionDTO;
import com.coldchain.dto.PageQueryDTO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        attribution.setStatus("COMPLETED");
        attribution.setCreateTime(LocalDateTime.now());

        int result = baseMapper.insert(attribution);
        if (result > 0) {
            lossRecord.setIsAttributed(1);
            lossRecord.setStatus("ATTRIBUTED");
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

            Product product = productMapper.selectById(
                    lossRecord.getBatchId() != null ? getBatchProductId(lossRecord.getBatchId()) : null
            );

            BigDecimal confidence = new BigDecimal("0.5");
            String responsibleParty = "待确认";
            String responsibilityType = "待分析";
            String responsibilityLevel = "一般";
            StringBuilder analysisBasis = new StringBuilder();

            if (lastFlow.getTemperature() != null && product != null && product.getStorageTemp() != null) {
                BigDecimal tempDiff = lastFlow.getTemperature().subtract(product.getStorageTemp()).abs();
                analysisBasis.append("运输温度偏差: ").append(tempDiff).append("℃; ");
                if (tempDiff.compareTo(new BigDecimal("5")) > 0) {
                    confidence = confidence.add(new BigDecimal("0.2"));
                    responsibleParty = "运输方";
                    responsibilityType = "温度控制不当";
                    responsibilityLevel = "严重";
                } else if (tempDiff.compareTo(new BigDecimal("2")) > 0) {
                    confidence = confidence.add(new BigDecimal("0.1"));
                    responsibleParty = "运输方";
                    responsibilityType = "温度控制不当";
                    responsibilityLevel = "较重";
                }
            }

            if (lastFlow.getTransportDuration() != null) {
                analysisBasis.append("运输时长: ").append(lastFlow.getTransportDuration()).append("小时; ");
                if (lastFlow.getTransportDuration() > 48) {
                    confidence = confidence.add(new BigDecimal("0.15"));
                    if (responsibilityType.equals("待分析")) {
                        responsibleParty = "物流调度";
                        responsibilityType = "运输时长过长";
                        responsibilityLevel = "较重";
                    }
                } else if (lastFlow.getTransportDuration() > 24) {
                    confidence = confidence.add(new BigDecimal("0.1"));
                }
            }

            if (lossRecord.getLossType() != null) {
                analysisBasis.append("损耗类型: ").append(lossRecord.getLossType()).append("; ");
                if ("变质".equals(lossRecord.getLossType()) || "腐烂".equals(lossRecord.getLossType())) {
                    if (responsibilityType.equals("待分析")) {
                        responsibleParty = "仓储方";
                        responsibilityType = "仓储管理不当";
                        responsibilityLevel = "一般";
                    }
                }
            }

            if (confidence.compareTo(new BigDecimal("0.95")) > 0) {
                confidence = new BigDecimal("0.95");
            }

            result.put("confidence", confidence);
            result.put("responsibleParty", responsibleParty);
            result.put("responsibilityType", responsibilityType);
            result.put("responsibilityLevel", responsibilityLevel);
            result.put("analysisBasis", analysisBasis.toString());
            result.put("suggestion", generateSuggestion(responsibilityType, responsibilityLevel));
        } else {
            result.put("confidence", new BigDecimal("0.3"));
            result.put("responsibleParty", "待确认");
            result.put("responsibilityType", "数据不足");
            result.put("responsibilityLevel", "待评估");
            result.put("analysisBasis", "无流转记录，无法进行有效分析");
            result.put("suggestion", "建议补充流转数据后重新分析");
        }

        return result;
    }

    private Long getBatchProductId(Long batchId) {
        com.coldchain.entity.Batch batch = batchMapper.selectById(batchId);
        return batch != null ? batch.getProductId() : null;
    }

    private String generateSuggestion(String responsibilityType, String responsibilityLevel) {
        StringBuilder suggestion = new StringBuilder();
        if ("温度控制不当".equals(responsibilityType)) {
            suggestion.append("建议加强运输过程温度监控，");
            if ("严重".equals(responsibilityLevel)) {
                suggestion.append("立即整改冷链设备，对相关责任人进行培训;");
            } else {
                suggestion.append("定期校准温度传感器，优化运输路线;");
            }
        } else if ("运输时长过长".equals(responsibilityType)) {
            suggestion.append("建议优化物流调度系统，");
            if ("严重".equals(responsibilityLevel)) {
                suggestion.append("建立紧急运输预案，增加运力储备;");
            } else {
                suggestion.append("合理规划运输路线，提高运输效率;");
            }
        } else if ("仓储管理不当".equals(responsibilityType)) {
            suggestion.append("建议加强仓储环境监控，");
            suggestion.append("建立定期巡检制度，完善出入库管理流程;");
        } else {
            suggestion.append("建议深入调查具体原因，完善全链条监管机制;");
        }
        return suggestion.toString();
    }

    @Override
    public PageResult<Map<String, Object>> getAttributionPage(PageQueryDTO dto) {
        Page<Map<String, Object>> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<ResponsibilityAttribution> wrapper = new LambdaQueryWrapper<>();
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(ResponsibilityAttribution::getResponsibleParty, dto.getKeyword())
                    .or().like(ResponsibilityAttribution::getResponsibilityType, dto.getKeyword())
                    .or().like(ResponsibilityAttribution::getAnalysisBasis, dto.getKeyword()));
        }
        wrapper.orderByDesc(ResponsibilityAttribution::getAnalysisTime);
        Page<Map<String, Object>> result = (Page<Map<String, Object>>) baseMapper.selectAttributionPage(page, wrapper);
        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public Map<String, Object> getAttributionDetail(Long id) {
        return baseMapper.selectAttributionDetail(id);
    }
}

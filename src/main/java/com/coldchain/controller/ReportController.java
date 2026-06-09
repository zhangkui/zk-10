package com.coldchain.controller;

import com.coldchain.common.Result;
import com.coldchain.mapper.BatchMapper;
import com.coldchain.mapper.ColdChainNodeMapper;
import com.coldchain.mapper.LossRecordMapper;
import com.coldchain.mapper.ResponsibilityAttributionMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "统计报表")
@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private BatchMapper batchMapper;

    @Autowired
    private ColdChainNodeMapper coldChainNodeMapper;

    @Autowired
    private LossRecordMapper lossRecordMapper;

    @Autowired
    private ResponsibilityAttributionMapper responsibilityAttributionMapper;

    @ApiOperation("概览统计")
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> data = new HashMap<>();
        Long totalBatchCount = batchMapper.selectTotalBatchCount();
        BigDecimal totalLossQuantity = batchMapper.selectTotalLossQuantity();
        BigDecimal avgLossRate = batchMapper.selectAvgLossRate();
        Long activeNodeCount = coldChainNodeMapper.selectActiveNodeCount();

        data.put("totalBatchCount", totalBatchCount != null ? totalBatchCount : 0L);
        data.put("totalLossQuantity", totalLossQuantity != null ? totalLossQuantity : BigDecimal.ZERO);
        data.put("avgLossRate", avgLossRate != null ? avgLossRate : BigDecimal.ZERO);
        data.put("activeNodeCount", activeNodeCount != null ? activeNodeCount : 0L);

        return Result.success(data);
    }

    @ApiOperation("按产品分类统计损耗")
    @GetMapping("/lossByCategory")
    public Result<List<Map<String, Object>>> getLossByCategory() {
        return Result.success(lossRecordMapper.selectLossByCategory());
    }

    @ApiOperation("损耗趋势统计")
    @GetMapping("/lossTrend")
    public Result<List<Map<String, Object>>> getLossTrend(
            @ApiParam("统计天数，默认7天") @RequestParam(defaultValue = "7") Integer days) {
        return Result.success(lossRecordMapper.selectLossTrend(days));
    }

    @ApiOperation("按节点统计损耗")
    @GetMapping("/lossByNode")
    public Result<List<Map<String, Object>>> getLossByNode() {
        return Result.success(lossRecordMapper.selectLossByNode());
    }

    @ApiOperation("按损耗类型统计")
    @GetMapping("/lossByType")
    public Result<List<Map<String, Object>>> getLossByType() {
        return Result.success(lossRecordMapper.selectLossByType());
    }

    @ApiOperation("按责任类型统计")
    @GetMapping("/lossByResponsibility")
    public Result<List<Map<String, Object>>> getLossByResponsibility() {
        return Result.success(responsibilityAttributionMapper.selectLossByResponsibility());
    }
}

package com.coldchain.controller;

import com.coldchain.common.PageResult;
import com.coldchain.common.Result;
import com.coldchain.dto.BatchQueryDTO;
import com.coldchain.entity.Batch;
import com.coldchain.service.BatchFlowService;
import com.coldchain.service.BatchService;
import com.coldchain.service.LossRecordService;
import com.coldchain.service.ResponsibilityAttributionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "批次管理")
@RestController
@RequestMapping("/api/batch")
public class BatchController {

    @Autowired
    private BatchService batchService;

    @Autowired
    private BatchFlowService batchFlowService;

    @Autowired
    private LossRecordService lossRecordService;

    @Autowired
    private ResponsibilityAttributionService attributionService;

    @ApiOperation("获取所有批次列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        return Result.success(batchService.getBatchList());
    }

    @ApiOperation("分页查询批次")
    @GetMapping("/page")
    public Result<PageResult<Map<String, Object>>> page(BatchQueryDTO dto) {
        return Result.success(batchService.getBatchDetailPage(dto));
    }

    @ApiOperation("获取批次详情")
    @GetMapping("/detail")
    public Result<Map<String, Object>> getById(@ApiParam("批次ID") @RequestParam Long id) {
        return Result.success(batchService.getBatchDetail(id));
    }

    @ApiOperation("创建批次")
    @PostMapping("/add")
    public Result<Boolean> save(@RequestBody Batch batch) {
        boolean result = batchService.createBatch(batch);
        return result ? Result.success(result) : Result.error("创建失败");
    }

    @ApiOperation("更新批次")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody Batch batch) {
        boolean result = batchService.update(batch);
        return result ? Result.success(result) : Result.error("更新失败");
    }

    @ApiOperation("删除批次")
    @DeleteMapping("/delete")
    public Result<Boolean> delete(@ApiParam("批次ID") @RequestParam Long id) {
        boolean result = batchService.delete(id);
        return result ? Result.success(result) : Result.error("删除失败");
    }

    @ApiOperation("获取批次追溯链路")
    @GetMapping("/{id}/trace")
    public Result<Map<String, Object>> getTrace(@ApiParam("批次ID") @PathVariable Long id) {
        Map<String, Object> trace = new HashMap<>();
        trace.put("batch", batchService.getBatchDetail(id));
        trace.put("flows", batchFlowService.getBatchFlowTrace(id));
        trace.put("losses", lossRecordService.getLossList(id));
        trace.put("attributions", attributionService.getAttributionList(id));
        return Result.success(trace);
    }
}

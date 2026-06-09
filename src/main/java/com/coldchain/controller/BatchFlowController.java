package com.coldchain.controller;

import com.coldchain.common.PageResult;
import com.coldchain.common.Result;
import com.coldchain.dto.BatchFlowDTO;
import com.coldchain.dto.PageQueryDTO;
import com.coldchain.entity.BatchFlow;
import com.coldchain.service.BatchFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "流转记录管理")
@RestController
@RequestMapping("/api/flow")
public class BatchFlowController {

    @Autowired
    private BatchFlowService batchFlowService;

    @ApiOperation("获取流转记录列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(@ApiParam("批次ID") @RequestParam(required = false) Long batchId) {
        return Result.success(batchFlowService.getFlowList(batchId));
    }

    @ApiOperation("分页查询流转记录")
    @GetMapping("/page")
    public Result<PageResult<Map<String, Object>>> page(PageQueryDTO dto, @ApiParam("批次ID") @RequestParam(required = false) Long batchId) {
        return Result.success(batchFlowService.getFlowDetailPage(dto, batchId));
    }

    @ApiOperation("获取流转详情")
    @GetMapping("/detail")
    public Result<BatchFlow> getById(@ApiParam("流转记录ID") @RequestParam Long id) {
        return Result.success(batchFlowService.getById(id));
    }

    @ApiOperation("添加流转记录")
    @PostMapping("/add")
    public Result<Boolean> save(@RequestBody BatchFlowDTO dto) {
        boolean result = batchFlowService.addFlow(dto);
        return result ? Result.success(result) : Result.error("添加失败");
    }

    @ApiOperation("删除流转记录")
    @DeleteMapping("/delete")
    public Result<Boolean> delete(@ApiParam("流转记录ID") @RequestParam Long id) {
        boolean result = batchFlowService.delete(id);
        return result ? Result.success(result) : Result.error("删除失败");
    }
}

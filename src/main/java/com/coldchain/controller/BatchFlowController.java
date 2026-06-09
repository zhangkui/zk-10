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

@Api(tags = "流转记录管理")
@RestController
@RequestMapping("/api/flow")
public class BatchFlowController {

    @Autowired
    private BatchFlowService batchFlowService;

    @ApiOperation("分页查询流转记录")
    @GetMapping("/page")
    public Result<PageResult<BatchFlow>> page(PageQueryDTO dto) {
        return Result.success(batchFlowService.page(dto));
    }

    @ApiOperation("获取流转详情")
    @GetMapping("/{id}")
    public Result<BatchFlow> getById(@ApiParam("流转记录ID") @PathVariable Long id) {
        return Result.success(batchFlowService.getById(id));
    }

    @ApiOperation("添加流转记录")
    @PostMapping
    public Result<Boolean> save(@RequestBody BatchFlowDTO dto) {
        boolean result = batchFlowService.addFlow(dto);
        return result ? Result.success(result) : Result.error("添加失败");
    }

    @ApiOperation("删除流转记录")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@ApiParam("流转记录ID") @PathVariable Long id) {
        boolean result = batchFlowService.delete(id);
        return result ? Result.success(result) : Result.error("删除失败");
    }
}

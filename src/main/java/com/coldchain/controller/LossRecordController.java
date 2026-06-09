package com.coldchain.controller;

import com.coldchain.common.PageResult;
import com.coldchain.common.Result;
import com.coldchain.dto.LossRecordDTO;
import com.coldchain.dto.PageQueryDTO;
import com.coldchain.entity.LossRecord;
import com.coldchain.service.LossRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "损耗记录管理")
@RestController
@RequestMapping("/api/loss")
public class LossRecordController {

    @Autowired
    private LossRecordService lossRecordService;

    @ApiOperation("获取所有损耗记录列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        return Result.success(lossRecordService.getLossList());
    }

    @ApiOperation("分页查询损耗记录")
    @GetMapping("/page")
    public Result<PageResult<Map<String, Object>>> page(PageQueryDTO dto) {
        return Result.success(lossRecordService.getLossRecordPage(dto));
    }

    @ApiOperation("获取损耗详情")
    @GetMapping("/{id}")
    public Result<LossRecord> getById(@ApiParam("损耗记录ID") @PathVariable Long id) {
        return Result.success(lossRecordService.getById(id));
    }

    @ApiOperation("添加损耗记录")
    @PostMapping("/add")
    public Result<Boolean> save(@RequestBody LossRecordDTO dto) {
        boolean result = lossRecordService.addLossRecord(dto);
        return result ? Result.success(result) : Result.error("添加失败");
    }

    @ApiOperation("更新损耗记录")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody LossRecord lossRecord) {
        boolean result = lossRecordService.update(lossRecord);
        return result ? Result.success(result) : Result.error("更新失败");
    }

    @ApiOperation("删除损耗记录")
    @DeleteMapping("/delete")
    public Result<Boolean> delete(@ApiParam("损耗记录ID") @RequestParam Long id) {
        boolean result = lossRecordService.delete(id);
        return result ? Result.success(result) : Result.error("删除失败");
    }
}

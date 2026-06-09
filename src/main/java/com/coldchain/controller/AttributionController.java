package com.coldchain.controller;

import com.coldchain.common.PageResult;
import com.coldchain.common.Result;
import com.coldchain.dto.AttributionDTO;
import com.coldchain.dto.PageQueryDTO;
import com.coldchain.entity.ResponsibilityAttribution;
import com.coldchain.service.ResponsibilityAttributionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "归因分析管理")
@RestController
@RequestMapping("/api/attribution")
public class AttributionController {

    @Autowired
    private ResponsibilityAttributionService attributionService;

    @ApiOperation("分页查询归因记录")
    @GetMapping("/page")
    public Result<PageResult<Map<String, Object>>> page(PageQueryDTO dto) {
        return Result.success(attributionService.getAttributionPage(dto));
    }

    @ApiOperation("获取归因详情")
    @GetMapping("/{id}")
    public Result<ResponsibilityAttribution> getById(@ApiParam("归因记录ID") @PathVariable Long id) {
        return Result.success(attributionService.getById(id));
    }

    @ApiOperation("添加归因分析")
    @PostMapping
    public Result<Boolean> save(@RequestBody AttributionDTO dto) {
        boolean result = attributionService.addAttribution(dto);
        return result ? Result.success(result) : Result.error("添加失败");
    }

    @ApiOperation("自动分析损耗原因")
    @GetMapping("/analyze/{lossId}")
    public Result<Map<String, Object>> analyze(@ApiParam("损耗记录ID") @PathVariable Long lossId) {
        return Result.success(attributionService.analyzeLoss(lossId));
    }
}

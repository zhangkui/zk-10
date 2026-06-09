package com.coldchain.controller;

import com.coldchain.common.Result;
import com.coldchain.entity.ColdChainNode;
import com.coldchain.service.ColdChainNodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "节点管理")
@RestController
@RequestMapping("/api/node")
public class ColdChainNodeController {

    @Autowired
    private ColdChainNodeService coldChainNodeService;

    @ApiOperation("获取节点列表")
    @GetMapping("/list")
    public Result<List<ColdChainNode>> list() {
        return Result.success(coldChainNodeService.list());
    }

    @ApiOperation("获取节点详情")
    @GetMapping("/{id}")
    public Result<ColdChainNode> getById(@ApiParam("节点ID") @PathVariable Long id) {
        return Result.success(coldChainNodeService.getById(id));
    }

    @ApiOperation("新增节点")
    @PostMapping
    public Result<Boolean> save(@RequestBody ColdChainNode node) {
        boolean result = coldChainNodeService.save(node);
        return result ? Result.success(result) : Result.error("新增失败");
    }

    @ApiOperation("修改节点")
    @PutMapping
    public Result<Boolean> update(@RequestBody ColdChainNode node) {
        boolean result = coldChainNodeService.update(node);
        return result ? Result.success(result) : Result.error("修改失败");
    }

    @ApiOperation("删除节点")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@ApiParam("节点ID") @PathVariable Long id) {
        boolean result = coldChainNodeService.delete(id);
        return result ? Result.success(result) : Result.error("删除失败");
    }
}

package com.coldchain.controller;

import com.coldchain.common.PageResult;
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

    @ApiOperation("分页查询节点")
    @GetMapping("/page")
    public Result<PageResult<ColdChainNode>> page(
            @ApiParam("页码") @RequestParam(required = false) Integer pageNum,
            @ApiParam("每页大小") @RequestParam(required = false) Integer pageSize,
            @ApiParam("节点名称") @RequestParam(required = false) String nodeName,
            @ApiParam("节点类型") @RequestParam(required = false) String nodeType,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        return Result.success(coldChainNodeService.pageWithParams(pageNum, pageSize, nodeName, nodeType, status));
    }

    @ApiOperation("获取节点详情")
    @GetMapping("/detail")
    public Result<ColdChainNode> getById(@ApiParam("节点ID") @RequestParam Long id) {
        return Result.success(coldChainNodeService.getById(id));
    }

    @ApiOperation("新增节点")
    @PostMapping("/add")
    public Result<Boolean> save(@RequestBody ColdChainNode node) {
        boolean result = coldChainNodeService.save(node);
        return result ? Result.success(result) : Result.error("新增失败");
    }

    @ApiOperation("修改节点")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody ColdChainNode node) {
        boolean result = coldChainNodeService.update(node);
        return result ? Result.success(result) : Result.error("修改失败");
    }

    @ApiOperation("删除节点")
    @DeleteMapping("/delete")
    public Result<Boolean> delete(@ApiParam("节点ID") @RequestParam Long id) {
        boolean result = coldChainNodeService.delete(id);
        return result ? Result.success(result) : Result.error("删除失败");
    }
}

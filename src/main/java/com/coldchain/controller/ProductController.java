package com.coldchain.controller;

import com.coldchain.common.Result;
import com.coldchain.entity.Product;
import com.coldchain.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "产品管理")
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation("获取产品列表")
    @GetMapping("/list")
    public Result<List<Product>> list() {
        return Result.success(productService.list());
    }

    @ApiOperation("获取产品详情")
    @GetMapping("/{id}")
    public Result<Product> getById(@ApiParam("产品ID") @PathVariable Long id) {
        return Result.success(productService.getById(id));
    }

    @ApiOperation("新增产品")
    @PostMapping
    public Result<Boolean> save(@RequestBody Product product) {
        boolean result = productService.save(product);
        return result ? Result.success(result) : Result.error("新增失败");
    }

    @ApiOperation("修改产品")
    @PutMapping
    public Result<Boolean> update(@RequestBody Product product) {
        boolean result = productService.update(product);
        return result ? Result.success(result) : Result.error("修改失败");
    }

    @ApiOperation("删除产品")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@ApiParam("产品ID") @PathVariable Long id) {
        boolean result = productService.delete(id);
        return result ? Result.success(result) : Result.error("删除失败");
    }
}

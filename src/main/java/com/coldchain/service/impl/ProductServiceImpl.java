package com.coldchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coldchain.common.PageResult;
import com.coldchain.dto.PageQueryDTO;
import com.coldchain.entity.Product;
import com.coldchain.mapper.ProductMapper;
import com.coldchain.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public Product getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<Product> list() {
        return baseMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public PageResult<Product> page(PageQueryDTO dto) {
        Page<Product> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            wrapper.like(Product::getProductName, dto.getKeyword())
                    .or().like(Product::getProductCode, dto.getKeyword());
        }
        wrapper.orderByDesc(Product::getCreateTime);
        page = baseMapper.selectPage(page, wrapper);
        return PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords());
    }

    @Override
    public boolean save(Product entity) {
        return baseMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(Product entity) {
        return baseMapper.updateById(entity) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return baseMapper.deleteById(id) > 0;
    }
}

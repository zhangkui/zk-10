package com.coldchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coldchain.common.PageResult;
import com.coldchain.dto.PageQueryDTO;
import com.coldchain.entity.ColdChainNode;
import com.coldchain.mapper.ColdChainNodeMapper;
import com.coldchain.service.ColdChainNodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ColdChainNodeServiceImpl extends ServiceImpl<ColdChainNodeMapper, ColdChainNode> implements ColdChainNodeService {

    @Override
    public ColdChainNode getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<ColdChainNode> list() {
        return baseMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public PageResult<ColdChainNode> page(PageQueryDTO dto) {
        Page<ColdChainNode> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<ColdChainNode> wrapper = new LambdaQueryWrapper<>();
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            wrapper.like(ColdChainNode::getNodeName, dto.getKeyword())
                    .or().like(ColdChainNode::getNodeCode, dto.getKeyword());
        }
        wrapper.orderByDesc(ColdChainNode::getCreateTime);
        page = baseMapper.selectPage(page, wrapper);
        return PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords());
    }

    @Override
    public boolean save(ColdChainNode entity) {
        return baseMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(ColdChainNode entity) {
        return baseMapper.updateById(entity) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return baseMapper.deleteById(id) > 0;
    }
}

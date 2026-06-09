package com.coldchain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coldchain.common.PageResult;
import com.coldchain.dto.PageQueryDTO;
import com.coldchain.entity.SysUser;
import com.coldchain.mapper.SysUserMapper;
import com.coldchain.service.SysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<SysUser> list() {
        return baseMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public PageResult<SysUser> page(PageQueryDTO dto) {
        Page<SysUser> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            wrapper.like(SysUser::getUsername, dto.getKeyword())
                    .or().like(SysUser::getRealName, dto.getKeyword());
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        page = baseMapper.selectPage(page, wrapper);
        return PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords());
    }

    @Override
    public boolean save(SysUser entity) {
        return baseMapper.insert(entity) > 0;
    }

    @Override
    public boolean update(SysUser entity) {
        return baseMapper.updateById(entity) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return baseMapper.deleteById(id) > 0;
    }
}

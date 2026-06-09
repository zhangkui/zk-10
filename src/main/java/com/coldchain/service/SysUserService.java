package com.coldchain.service;

import com.coldchain.common.PageResult;
import com.coldchain.dto.PageQueryDTO;
import com.coldchain.entity.SysUser;

import java.util.List;

public interface SysUserService {

    SysUser getById(Long id);

    List<SysUser> list();

    PageResult<SysUser> page(PageQueryDTO dto);

    boolean save(SysUser entity);

    boolean update(SysUser entity);

    boolean delete(Long id);
}

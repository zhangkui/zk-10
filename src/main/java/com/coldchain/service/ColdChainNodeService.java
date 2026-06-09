package com.coldchain.service;

import com.coldchain.common.PageResult;
import com.coldchain.dto.PageQueryDTO;
import com.coldchain.entity.ColdChainNode;

import java.util.List;

public interface ColdChainNodeService {

    ColdChainNode getById(Long id);

    List<ColdChainNode> list();

    PageResult<ColdChainNode> page(PageQueryDTO dto);

    PageResult<ColdChainNode> pageWithParams(Integer pageNum, Integer pageSize, String nodeName, String nodeType, Integer status);

    boolean save(ColdChainNode entity);

    boolean update(ColdChainNode entity);

    boolean delete(Long id);
}

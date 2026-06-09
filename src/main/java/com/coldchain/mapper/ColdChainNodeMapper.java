package com.coldchain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coldchain.entity.ColdChainNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ColdChainNodeMapper extends BaseMapper<ColdChainNode> {

    @Select("SELECT COUNT(*) FROM cold_chain_node WHERE status = 1")
    Long selectActiveNodeCount();
}

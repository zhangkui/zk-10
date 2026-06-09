package com.coldchain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coldchain.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}

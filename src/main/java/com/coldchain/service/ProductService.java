package com.coldchain.service;

import com.coldchain.common.PageResult;
import com.coldchain.dto.PageQueryDTO;
import com.coldchain.entity.Product;

import java.util.List;

public interface ProductService {

    Product getById(Long id);

    List<Product> list();

    List<Product> list(String productName, String category);

    PageResult<Product> page(PageQueryDTO dto);

    boolean save(Product entity);

    boolean update(Product entity);

    boolean delete(Long id);
}

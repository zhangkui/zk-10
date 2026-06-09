package com.coldchain.common;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private Long total;
    private Long pageNum;
    private Long pageSize;
    private List<T> records;

    public PageResult() {
    }

    public PageResult(Long total, Long pageNum, Long pageSize, List<T> records) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.records = records;
    }

    public static <T> PageResult<T> of(Long total, Long pageNum, Long pageSize, List<T> records) {
        return new PageResult<>(total, pageNum, pageSize, records);
    }
}

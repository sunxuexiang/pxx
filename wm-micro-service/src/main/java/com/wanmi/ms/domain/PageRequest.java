package com.wanmi.ms.domain;

import org.springframework.data.domain.Sort;

/**
 * 分页对象，继承自spring的org.springframework.data.domain.PageRequest；
 * 新增默认构造函数，确保dubbo正常序列化
 * Created by aqlu on 15/9/2.
 */
public class PageRequest extends org.springframework.data.domain.PageRequest {
    private static final int DEFAULT_PAGE = 0;

    private static final int DEFAULT_PAGE_SIZE = 10;

    public PageRequest(){
        super(DEFAULT_PAGE, DEFAULT_PAGE_SIZE,Sort.unsorted());
    }

    public PageRequest(int page, int size) {
        super(page, size,Sort.unsorted());
    }

    public PageRequest(int page, int size, Sort.Direction direction, String... properties) {
        super(page, size, Sort.by(direction, properties));
    }

    public PageRequest(int page, int size, Sort sort) {
        super(page, size, sort);
    }
}

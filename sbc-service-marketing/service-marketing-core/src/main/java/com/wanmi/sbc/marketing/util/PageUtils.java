package com.wanmi.sbc.marketing.util;

import java.util.Collections;
import java.util.List;

public class PageUtils {

    public static <T> List<T> getCurrentPage(List<T> list, Integer pageNum, Integer pageSize) {
        //获取当前分页的商品id
        int fromIndex = (pageNum <= 0 ? 1 : pageNum - 1) * pageSize;
        //分页起始索引大于最大索引，返回空页
        int total = list.size();
        if (fromIndex > total - 1) {
            return Collections.emptyList();
        }
        //分页结束索引大于 total，返回total， 最大索引+1，subList不包含
        int endIndex = Math.min(fromIndex + pageSize, total);

        return list.subList(fromIndex, endIndex);
    }
}
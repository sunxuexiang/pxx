package com.wanmi.sbc.customer.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: sbc-backgroud
 * @description: '
 * @author: gdq
 * @create: 2023-06-30 10:47
 **/
public class MallIntoCompanyEnums {

    @AllArgsConstructor
    @Getter
    public enum SortType {

        MARKET(1, "市场"),
        TAB(2, "商城"),
        RECOMMEND(3, "推荐商家"),
        ;
        private Integer value;
        private String name;
    }
}

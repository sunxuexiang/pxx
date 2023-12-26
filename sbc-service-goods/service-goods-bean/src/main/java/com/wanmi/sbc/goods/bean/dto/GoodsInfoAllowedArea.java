package com.wanmi.sbc.goods.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jeffrey
 * @create 2021-08-12 15:24
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsInfoAllowedArea {
    /**
     * 城市code
     */
    private String code;
    /**
     * 城市名称
     */
    private String name;
    /**
     * 该城市对应的上级省份code
     */
    private String parentCode;
}

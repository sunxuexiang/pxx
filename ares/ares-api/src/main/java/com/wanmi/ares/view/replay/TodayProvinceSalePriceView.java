package com.wanmi.ares.view.replay;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/10/7 15:10
 */
@Data
public class TodayProvinceSalePriceView {

    private String provinceId;

    private String provinceName;

    private BigDecimal salePrice;

    private Long orderUser;
}

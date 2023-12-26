package com.wanmi.ares.response.screen;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 品类销售排名响应
 * @author lm
 * @date 2022/09/09 10:50
 */
@Data
public class CateSaleResponse implements Serializable {

    private String cateName;//品类
    private BigDecimal totalPrice; // 总金额
    private BigDecimal percent;// 百分比

}

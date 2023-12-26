package com.wanmi.ares.response.screen;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 区域预售总金额分析接口数据响应
 * @author lm
 * @date 2022/09/06 14:11
 */
@Data
public class AreaPreSaleTotalMoneyResponse implements Serializable {

    /*总金额*/
    private BigDecimal total;

    /*长沙仓库*/
    private BigDecimal cs;

    /*武汉仓库*/
    private BigDecimal wh;

    /*南昌仓库*/
    private BigDecimal nc;
}

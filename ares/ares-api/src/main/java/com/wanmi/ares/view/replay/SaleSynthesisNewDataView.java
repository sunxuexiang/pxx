package com.wanmi.ares.view.replay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaleSynthesisNewDataView implements Serializable {

    private static final long serialVersionUID = -7694864849821760105L;

    /**
     * 省份ID
     */
    private String provinceId;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 总金额
     */
    private BigDecimal salePrice;

    /**
     * 下单用户数
     */
    private Integer buyNum;

    /**
     * 订单数
     */
    private Integer orderNum;

    /**
     * 订单箱数
     */
    private Long orderItemNum;


    private List<RecentSevenDaySaleNewView> recentSevenDaySaleTotalCaseList;

    private List<RecentSevenDaySalePriceNewView> recentSevenDaySaleTotalPriceList;
}

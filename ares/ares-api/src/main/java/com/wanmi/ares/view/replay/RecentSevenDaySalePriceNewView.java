package com.wanmi.ares.view.replay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentSevenDaySalePriceNewView implements Serializable {

    private static final long serialVersionUID = -2492601676835363242L;

    /**
     * 省份ID
     */
    private String provinceId;

    /**
     *  省份名称
     */
    private String provinceName;

    private String dayTime;

    private BigDecimal daySalePrice;
}

package com.wanmi.sbc.tms.api.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 站点信息表
 * </p>
 *
 * @author xyy
 * @since 2023-09-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TmsSitePickupQueryVO implements Serializable {

    private static final long serialVersionUID = 1L;
    // 收货人省code
    private Integer buyerProvinceCode;
    // 收货人城市code
    private Integer buyerCityCode;
    //  收货人区县code
    private Integer buyerDistrictCode;

    // 收货人街道code
    private Integer buyerStreetCode;

    // 承运商省份
    private Integer carrierProvinceCode;
}

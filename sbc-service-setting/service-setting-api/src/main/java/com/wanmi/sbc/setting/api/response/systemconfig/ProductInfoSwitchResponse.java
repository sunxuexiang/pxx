package com.wanmi.sbc.setting.api.response.systemconfig;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoSwitchResponse {

    /**
     * 品牌开关：0、不显示
     */
    private Integer brandSwitch;

    /**
     * 销售开关：0、不显示
     */
    private Integer saleSwitch;

    /**
     * 价格开关：0、不显示
     */
    private Integer priceSwitch;

    /**
     * 举报开关
     */
    private Integer liveTipSwitch;
}

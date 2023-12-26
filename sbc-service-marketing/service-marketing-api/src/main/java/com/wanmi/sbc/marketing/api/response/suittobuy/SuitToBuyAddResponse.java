package com.wanmi.sbc.marketing.api.response.suittobuy;

import com.wanmi.sbc.marketing.bean.vo.MarketingVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: TODO
 * @author: XinJiang
 * @time: 2022/2/4 16:17
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuitToBuyAddResponse {
    /**
     * 营销视图对象
     */
    @ApiModelProperty(value = "营销视图对象")
    private MarketingVO marketingVO;
}

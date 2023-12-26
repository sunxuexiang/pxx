package com.wanmi.sbc.marketing.api.request.market;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-16 16:39
 */
@ApiModel
@Data
@NoArgsConstructor
public class MarketingGetByIdByIdRequest  {
    private static final long serialVersionUID = -665448956536447095L;

    @ApiModelProperty(value = "营销Id")
    private Long marketingId;

}

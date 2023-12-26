package com.wanmi.sbc.marketing.api.request.market;

import com.wanmi.sbc.marketing.bean.dto.MarketingGoodsExportDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class MarketingGoodsExportRequest extends MarketingGoodsExportDTO {

    // jwt token
    @ApiModelProperty(value = "jwt token")
    private String token;
}

package com.wanmi.sbc.returnorder.api.response.purchase;

import com.wanmi.sbc.marketing.bean.vo.MarketingViewVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@ApiModel
public class MarketingAndGoodsResponse implements Serializable {
    private static final long serialVersionUID = 6728197148556399094L;

    @ApiModelProperty(value = "营销活动实体")
    private MarketingViewVO marketingViewVO;

}

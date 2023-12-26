package com.wanmi.sbc.marketing.api.response.market;

import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingGroupCard;
import com.wanmi.sbc.marketing.bean.vo.MarketingVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 商品对应营销列表
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingByGoodsInfoIdResponse implements Serializable {
    private static final long serialVersionUID = 7293672850773285107L;


    @ApiModelProperty(value = "商品对应的营销")
    public Map<String,List<MarketingVO>> marketingVOS;

}

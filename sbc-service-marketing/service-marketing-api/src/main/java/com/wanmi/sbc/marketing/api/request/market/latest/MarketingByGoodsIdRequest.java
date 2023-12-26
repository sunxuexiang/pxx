package com.wanmi.sbc.marketing.api.request.market.latest;

import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingByGoodsIdRequest implements Serializable {

    private static final long serialVersionUID = -494737949098158484L;

    @ApiModelProperty(value = "请求商品id")
    public  List<String> goodsinfos;


}

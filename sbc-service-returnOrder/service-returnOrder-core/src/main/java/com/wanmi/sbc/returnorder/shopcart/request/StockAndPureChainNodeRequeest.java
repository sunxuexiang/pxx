package com.wanmi.sbc.returnorder.shopcart.request;

import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoMarketingVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockAndPureChainNodeRequeest {

    private List<DevanningGoodsInfoMarketingVO> CheckPure;



    @ApiModelProperty(value = "仓位id")
    private Long wareId;
    @ApiModelProperty(value = "用户当前收货地址")
    private Long provinceId;
    @ApiModelProperty(value = "用户当前收货地址")
    private Long cityId;
    @ApiModelProperty(value = "用户id")
    private String customerId;

}

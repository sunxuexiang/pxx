package com.wanmi.sbc.shopcart.api.request.purchase;

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
    @ApiModelProperty(value = "商品信息")
    private List<DevanningGoodsInfoMarketingVO> checkPure;



    @ApiModelProperty(value = "仓位id")
    private Long wareId;
    @ApiModelProperty(value = "用户当前收货地址")
    private Long provinceId;
    @ApiModelProperty(value = "用户当前收货地址")
    private Long cityId;
    @ApiModelProperty(value = "用户id")
    private String customerId;
    @ApiModelProperty(value = "是否是去mysql查询")
    private Boolean ismysql =false;
    @ApiModelProperty(value = "是否需要后端查询营销id")
    private Boolean needCheack = false;
    //subType 默认 0
    //0是非囤货
    //1是囤货
    //2是提货
    //3是散批校验只校验库存和限购
    //4是散批
    @ApiModelProperty(value = "区分囤货")
    private Integer subType = 0;
    @ApiModelProperty(value = "当前订单号")
    private String tid;

}

package com.wanmi.sbc.wallet.request;

import com.wanmi.sbc.marketing.bean.vo.CheckGoodsInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@ApiModel
@Data
public class CustomerWalletBaseRequest {

    /**
     * 是否使用余额
     */
    @ApiModelProperty(value = "是否使用余额")
    @NotNull
    private Boolean useWallet;

    /**
     * 商品是否含运费
     */
    @ApiModelProperty(value = "商品是否含运费")
    @NotNull
    private Boolean isDeliveryPrice;

    //todo:待优化 暂时使用此方案

//    /**
//     * 已勾选的优惠券码id
//     */
//    @ApiModelProperty(value = "已勾选的优惠券码id集合")
//    @NotNull
//    private List<String> couponCodeIds;

    /**
     * 商品总价
     */
    @ApiModelProperty(value = "商品总价")
    private BigDecimal totalPrice;

    /**
     * 均摊商品价格
     */
    @ApiModelProperty(value = "均摊商品价格")
    List<CheckGoodsInfoVO> checkGoodsInfos;
}

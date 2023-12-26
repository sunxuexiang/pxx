package com.wanmi.sbc.marketing.api.response.coupon;

import com.wanmi.sbc.marketing.bean.vo.CoinGoodsVo;
import com.wanmi.sbc.marketing.bean.vo.CouponCodeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 购买指定商品赠券响应类
 * @author: jiangxin
 * @create: 2021-09-09 11:41
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuyGoodsOrFullOrderSendCouponResponse implements Serializable {

    private static final long serialVersionUID = 9026873765833658626L;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String desc;

    /**
     * 优惠券列表
     */
    @ApiModelProperty(value = "优惠券列表")
    private List<GetCouponGroupResponse> couponList;

    /**
     * 赠券集合
     */
    @ApiModelProperty(value = "赠券集合")
    private List<CouponCodeVO> couponCodeVOS;

    /**
     * 参与活动的商品id集合
     */
    @ApiModelProperty(value = "参与活动的商品id集合")
    private List<String> couponSkuIds;

    /**
     * 是否满足赠券条件
     */
    @ApiModelProperty(value = "是否满足赠券条件")
    private Boolean isMeetSendCoupon = false;


    /**
     * 是否满足赠鲸币条件
     */
    @ApiModelProperty(value = "是否满足赠鲸币条件")
    private Boolean isMeetSendCoin = false;

    /**
     * 增金币集合
     */
    @ApiModelProperty(value = "赠金币集合")
    private List<CoinGoodsVo> coinGoodsVos;

}

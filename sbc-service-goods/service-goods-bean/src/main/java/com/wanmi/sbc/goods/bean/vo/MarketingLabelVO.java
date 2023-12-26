package com.wanmi.sbc.goods.bean.vo;

import com.wanmi.sbc.common.enums.BoolFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 营销标签
 * Created by dyt on 2018/2/28.
 */
@ApiModel
@Data
public class MarketingLabelVO  implements Serializable {

    private static final long serialVersionUID = -3098691550938179678L;

    /**
     * 营销编号
     */
    @ApiModelProperty(value = "营销编号")
    private Long marketingId;

    /**
     * 促销类型 0：满减 1:满折 2:满赠
     * 与Marketing.marketingType保持一致
     * 4：购买指定商品赠券[add by jiangxin 20220302，优惠券活动营销插件查询时使用，仅限购买指定商品赠券]
     */
    @ApiModelProperty(value = "促销类型", dataType = "com.wanmi.sbc.goods.bean.enums.MarketingType")
    private Integer marketingType;

    /**
     * 促销描述
     */
    @ApiModelProperty(value = "促销描述")
    private String marketingDesc;

    /**
     * 满减、满折促销描述集合
     */
    private List<String> marketingDescList;

    /**
     * 促销子类型 0：满金额减 1：满数量减 2:满金额折 3:满数量折 4:满金额赠 5:满数量赠
     */
    private Integer subType;

    /**
     * 是否为必选商品
     */
        private BoolFlag whetherChoice;

    /**
     * 满足数量
     */
    private Integer number;

    /**
     * 金额
     */
    private BigDecimal amount = BigDecimal.ZERO;


    /**
     * 满折
     */
    private BigDecimal fullFold;

    /**
     * 营销活动具体数据
     */
    @ApiModelProperty(value = "营销活动具体数据")
    private List<MarketingReductionLevelLabelVO> levelLabelVOS;

    /**
     * 商品限购数量
     */
    private Integer goodsPurchasingNumber;

    /**
     * 优惠券活动id
     */
    @ApiModelProperty(value = "优惠券活动id")
    private String activityId;

    /**
     * 购买指定商品赠券满足类型：
     * 0 全部满足赠，1 满足任意一个赠，2 满金额赠， 3 满数量赠
     */
    @ApiModelProperty(value = "购买指定商品赠券满足类型：0 全部满足赠，1 满足任意一个赠，2 满金额赠， 3 满数量赠")
    private Integer couponActivityFullType;

    /**
     * 购买指定商品赠券，满赠活动赠送优惠券集合
     */
    @ApiModelProperty(value = "购买指定商品赠券，满赠活动赠送优惠券集合")
    private List<CouponLabelVO> couponLabelVOS;

    @ApiModelProperty(value = "赠鲸币数量")
    private BigDecimal coinNum;

    /**
     * 优惠券活动id
     */
    @ApiModelProperty(value = "鲸币活动ID")
    private String conActivityId;

}

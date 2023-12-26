package com.wanmi.sbc.marketing.bean.vo;

import com.wanmi.sbc.marketing.bean.enums.GiftType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 满赠
 */
@ApiModel
@Data
public class MarketingFullGiftLevelVO implements Serializable {

    private static final long serialVersionUID = -2319539958865078581L;

    /**
     *  满赠多级促销Id
     */
    @ApiModelProperty(value = "满赠多级促销主键Id")
    private Long giftLevelId;

    /**
     *  满赠Id
     */
    @ApiModelProperty(value = "满赠营销Id")
    private Long marketingId;

    /**
     *  满金额赠
     */
    @ApiModelProperty(value = "满金额赠")
    private BigDecimal fullAmount;

    /**
     *  满数量赠
     */
    @ApiModelProperty(value = "满数量赠")
    private Long fullCount;

    /**
     *  赠品赠送的方式 0:全赠  1：赠一个
     */
    @ApiModelProperty(value = "赠品赠送的方式")
    private GiftType giftType;

    /**
     * 满赠赠品明细
     */
    @ApiModelProperty(value = "满赠赠品明细列表")
    private List<MarketingFullGiftDetailVO> fullGiftDetailList;

//    @ApiModelProperty(value = "商品营销表")
//    private List<MarketingScopeVO> marketingScopeVOList;
}

package com.wanmi.sbc.marketing.bean.vo;

import com.wanmi.sbc.goods.bean.vo.GoodsInfoPurchaseResponseVO;
import com.wanmi.sbc.marketing.bean.enums.MarketingScopeType;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
public class MarketingPurchaseViewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 营销Id
     */
    @ApiModelProperty(value = "营销Id")
    private Long marketingId;

    /**
     * 营销类型 0：满减 1:满折 2:满赠
     */
    @ApiModelProperty(value = "营销活动类型")
    private MarketingType marketingType;

    /**
     * 营销子类型 0：满金额减 1：满数量减 2:满金额折 3:满数量折 4:满金额赠 5:满数量赠
     */
    @ApiModelProperty(value = "营销子类型")
    private MarketingSubType subType;

    /**
     * 参加营销类型 0：全部货品 1：货品 2：品牌 3：分类
     */
    @ApiModelProperty(value = "参加营销类型")
    private MarketingScopeType scopeType;

    /**
     * 满减等级
     */
    @ApiModelProperty(value = "营销满减多级优惠列表")
    private List<MarketingFullReductionLevelVO> fullReductionLevelList;

    /**
     * 满折等级
     */
    @ApiModelProperty(value = "营销满折多级优惠列表")
    private List<MarketingFullDiscountLevelVO> fullDiscountLevelList;

    /**
     * 满赠等级
     */
    @ApiModelProperty(value = "营销满赠多级优惠列表")
    private List<MarketingFullGiftLevelVO> fullGiftLevelList;

    /**
     * 营销关联商品
     */
    @ApiModelProperty(value = "营销关联商品")
    private GoodsInfoPurchaseResponseVO goodsList;

    /**
     * 商家Id  0：boss,  other:其他商家
     */
    @ApiModelProperty(value = "商家Id，0：boss, other:其他商家")
    private Long storeId;

}

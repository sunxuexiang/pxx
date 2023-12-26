package com.wanmi.sbc.marketing.bean.vo;

import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.goods.bean.vo.GiftGoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 满赠
 */
@ApiModel
@Data
public class MarketingFullGiftDetailVO implements Serializable {

    private static final long serialVersionUID = -5305430465244477726L;

    /**
     *  满赠赠品Id
     */
    @ApiModelProperty(value = "满赠赠品主键Id")
    private Long giftDetailId;

    /**
     *  满赠多级促销Id
     */
    @ApiModelProperty(value = "满赠多级促销Id")
    private Long giftLevelId;

    /**
     *  赠品Id
     */
    @ApiModelProperty(value = "赠品Id")
    private String productId;

    /**
     *  赠品数量
     */
    @ApiModelProperty(value = "赠品数量")
    private Long productNum;

    /**
     *  满赠ID
     */
    @ApiModelProperty(value = "满赠营销ID")
    private Long marketingId;

    /**
     *  限赠数量（只存总数，redis存剩余数量）
     */
    @ApiModelProperty(value = "限赠数量")
    private Long boundsNum;

    /**
     * 是否终止
     */
    @ApiModelProperty(value = "是否终止")
    private BoolFlag terminationFlag;
    /**
     *  满赠ID
     */
    @ApiModelProperty(value = "商品详情")
    private GiftGoodsInfoVO giftGoodsInfoVO;

    public int mgetGitGoodsStaus(){
        return giftGoodsInfoVO.getGoodsStatus().toValue();
    }
    public BigDecimal mgetGitGoodsPrice(){
        return giftGoodsInfoVO.getMarketPrice();
    }
}

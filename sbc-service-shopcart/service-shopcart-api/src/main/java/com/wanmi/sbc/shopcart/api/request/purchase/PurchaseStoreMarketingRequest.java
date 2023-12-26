package com.wanmi.sbc.shopcart.api.request.purchase;

import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.bean.dto.GoodsMarketingDTO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsIntervalPriceVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingViewVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 * author: yang
 * Date: 2021-01-13
 */
@Data
@ApiModel
public class PurchaseStoreMarketingRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品营销信息
     */
    @ApiModelProperty(value = "商品营销信息")
    private List<GoodsMarketingDTO> goodsMarketingDTOList;

    /**
     * 商品营销信息，skuId作为map的key
     */
    @ApiModelProperty(value = "单品营销信息map,key为单品id，value为营销列表")
    private Map<String, List<MarketingViewVO>> goodsMarketingMap;

    /**
     * 商品区间价格列表
     */
    @ApiModelProperty(value = "商品区间价格列表")
    private List<GoodsIntervalPriceVO> goodsIntervalPrices;

    /**
     * 商品SKU信息
     */
    @ApiModelProperty(value = "商品SKU信息")
    private List<GoodsInfoVO> goodsInfos;

    /**
     *goodsInfoIdList
     */
    @ApiModelProperty(value = "goodsInfoIdList")
    private List<String> goodsInfoIdList;

    /**
     *devanningIdList
     */
    @ApiModelProperty(value = "devanningIdList")
    private List<Long> devanningIdList;

    /**
     * 仓库Id
     */
    @ApiModelProperty(value = "仓库Id")
    private Long wareId;

    /**
     * 登录用户
     */
    @ApiModelProperty(value = "登录用户")
    private CustomerVO customer;

    @ApiModelProperty(value = "公司信息ID")
    private Long companyInfoId;

    /**
     * 邀请人id-会员id
     */
    @ApiModelProperty(value = "邀请人id")
    String inviteeId;

    /**
     * 是否为关键字查询
     */
    @ApiModelProperty(value = "是否能匹配仓")
    private Boolean matchWareHouseFlag;
}

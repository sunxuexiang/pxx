package com.wanmi.sbc.returnorder.api.request.trade;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeMarketingDTO;
import com.wanmi.sbc.returnorder.bean.dto.TradeItemDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 保存订单商品快照请求结构
 * @Author: daiyitian
 * @Description:
 * @Date: 2018-11-16 16:39
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class TradeItemSnapshotRequest implements Serializable {

    private static final long serialVersionUID = -1076979847505660373L;

    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    @NotBlank
    private String customerId;

    /**
     * 开店礼包
     */
    @ApiModelProperty(value = "开店礼包")
    private DefaultFlag storeBagsFlag = DefaultFlag.NO;

    /**
     * 是否开团购买(true:开团 false:参团 null:非拼团购买)
     */
    @ApiModelProperty(value = "是否开团购买")
    private Boolean openGroupon;

    /**
     * 团号
     */
    @ApiModelProperty(value = "团号")
    private String grouponNo;

    /**
     * 商品快照，只包含skuId与购买数量
     */
    @ApiModelProperty(value = "商品快照，只包含skuId与购买数量")
//    @NotNull
    private List<TradeItemDTO> tradeItems;

    /**
     * 营销快照
     */
    @ApiModelProperty(value = "营销快照")
    //生产提货订单快照时，不需要营销信息，以前旧的购物车逻辑不变取消这里为空的标志  jiangxin 20211003
    //@NotNull
    private List<TradeMarketingDTO> tradeMarketingList;

    /**
     * 快照商品详细信息，包含所属商家，店铺等信息
     */
    @ApiModelProperty(value = "快照商品详细信息，包含所属商家，店铺等信息")
//    @NotNull
    private List<GoodsInfoDTO> skuList;

    /**
     * 商品快照，只包含skuId与购买数量
     */
    @ApiModelProperty(value = "商品快照，只包含skuId与购买数量（零售）")
//    @NotNull
    private List<TradeItemDTO> retailTradeItems;


    /**
     * 商品快照，只包含skuId与购买数量
     */
    @ApiModelProperty(value = "拆箱散批商品快照，只包含skuId与购买数量（拆箱散批）")
//    @NotNull
    private List<TradeItemDTO> bulkTradeItems;

    /**
     * 快照商品详细信息，包含所属商家，店铺等信息
     */
    @ApiModelProperty(value = "快照商品详细信息，包含所属商家，店铺等信息(拆箱散批)")
//    @NotNull
    private List<GoodsInfoDTO> bulkSkuList;


    /**
     * 营销快照
     */
    @ApiModelProperty(value = "营销快照（零售）")
    //生产提货订单快照时，不需要营销信息，以前旧的购物车逻辑不变取消这里为空的标志  jiangxin 20211003
    //@NotNull
    private List<TradeMarketingDTO> retailtTradeMarketingList;

    /**
     * 快照商品详细信息，包含所属商家，店铺等信息
     */
    @ApiModelProperty(value = "快照商品详细信息，包含所属商家，店铺等信息(零售)")
//    @NotNull
    private List<GoodsInfoDTO> retailSkuList;

    /**
     * 快照类型--秒杀活动抢购商品订单快照："FLASH_SALE"
     */
    @ApiModelProperty(value = "快照类型--秒杀活动抢购商品订单快照：FLASH_SALE")
    private String snapshotType;

    /**
     * 套装购买数量
     */
    private Long suitBuyCount;

    /**
     * 套装id
     */
    private Long marketingId;

}

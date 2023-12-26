package com.wanmi.sbc.returnorder.bean.vo;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>按商家，店铺分组的订单商品快照</p>
 * Created by of628-wenzhi on 2017-11-23-下午2:46.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class TradeItemGroupVO implements Serializable {

    private static final long serialVersionUID = 8178188691751557994L;

    /**
     * 订单商品sku
     */
    @ApiModelProperty(value = "订单商品sku")
    private List<TradeItemVO> tradeItems;

    /**
     * 商家与店铺信息
     */
    @ApiModelProperty(value = "商家与店铺信息")
    private SupplierVO supplier;

    /**
     * 订单营销信息
     */
    @ApiModelProperty(value = "订单营销信息")
    private List<TradeItemMarketingVO> tradeMarketingList;

    /**
     * 开店礼包
     */
    @ApiModelProperty(value = "开店礼包")
    private DefaultFlag storeBagsFlag = DefaultFlag.NO;

    /**
     * 快照类型--秒杀活动抢购商品订单快照："FLASH_SALE"
     */
    @ApiModelProperty(value = "快照类型--秒杀活动抢购商品订单快照：FLASH_SALE")
    private String snapshotType;

    /**
     * 下单拼团相关字段
     */
    private TradeGrouponCommitFormVO grouponForm;

    /**
     * 分仓Id
     */
    @ApiModelProperty(value = "分仓Id")
    private Long wareId;

    /**
     * 仓库编码
     */
    @ApiModelProperty(value = "仓库编码")
    private String wareCode;

    /**
     * 销售类型 0批发 1零售
     */
    @ApiModelProperty(value = "销售类型 0批发 1零售 2散批")
    private SaleType saleType;

}

package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/23 9:49
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoAreaEditRequest implements Serializable {

    private static final long serialVersionUID = -8059163304117903253L;
    /**
     * skuId集合
     */
    @ApiModelProperty("skuId集合")
    private List<String> goodsInfoIds;

    /**
     * skuId
     */
    @ApiModelProperty("skuId集合（修改传）")
    private String goodsInfoId;

    /**
     * 该商品允许销售的地区id
     */
    @ApiModelProperty("该商品允许销售的地区id")
    private String allowedPurchaseArea;

    /**
     * 该商品允许销售的地区名称
     */
    @ApiModelProperty("该商品允许销售的地区名称")
    private String allowedPurchaseAreaName;

    /**
     * 单笔订单指定限购区域id，用“，”隔开
     */
    @ApiModelProperty("单笔订单指定限购区域id，用“，”隔开")
    private String singleOrderAssignArea;

    /**
     * 单笔订单指定限购区域名称，用“，”隔开
     */
    @ApiModelProperty("单笔订单指定限购区域名称，用“，”隔开")
    private String singleOrderAssignAreaName;

    /**
     * 单笔订单限购数量
     */
    @ApiModelProperty("单笔订单限购数量")
    private Long singleOrderPurchaseNum;
}

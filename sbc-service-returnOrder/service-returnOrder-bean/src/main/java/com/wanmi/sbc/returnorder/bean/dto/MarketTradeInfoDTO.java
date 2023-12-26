package com.wanmi.sbc.returnorder.bean.dto;


import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>批发市场配送到店提交信息</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class MarketTradeInfoDTO extends BaseRequest {

    private static final long serialVersionUID = -1980544654461057449L;
    @ApiModelProperty(value = "收货地址id", name = "addressId")
    private String addressId;
    @ApiModelProperty(value = "批发市场集合")
    private List<MarketItem> marketlist;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MarketStoreItem implements Serializable {
        @ApiModelProperty(value = "商家Id")
        private Long storeId;

        @ApiModelProperty(value = "商家名称")
        private String storeName;
        @ApiModelProperty(value = "商家商品数量")
        private Long  tradeNum;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MarketItem implements Serializable {
        @ApiModelProperty(value = "批发市场Id")
        private Long marketId;
        @ApiModelProperty(value = "商品总数量")
        private Long  totalSkuNum;
        @ApiModelProperty(value = "批发市场明细")
        private List<MarketStoreItem> marketStoreItems;
    }
}

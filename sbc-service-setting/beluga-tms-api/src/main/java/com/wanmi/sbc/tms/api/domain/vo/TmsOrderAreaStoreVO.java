package com.wanmi.sbc.tms.api.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 购买店铺的信息
 */
@Data
public class TmsOrderAreaStoreVO implements Serializable {

    /**
     * 配送总件数
     */
    private Long quantity;

    /**
     * 店铺id
     */
    private String storeId;


    /**
     * 店铺省份code
     */
    private String provinceCode;

    /**
     * sku 商品信息
     */
    private List<GoodsInfoVO> goodsInfoList;


    @Data
    public static class GoodsInfoVO{
        private String skuId;

        /**
         * 配送单个件数
         */
        private Long quantity;
        /**
         * 单个商品重量
         */
        private Double weight;

        /**
         * 单个商品体积
         */
        private Double volumn;

    }
}

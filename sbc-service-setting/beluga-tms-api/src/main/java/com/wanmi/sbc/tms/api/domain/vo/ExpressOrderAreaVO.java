package com.wanmi.sbc.tms.api.domain.vo;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 快递到家运费计算VO类
 */
@Data
public class ExpressOrderAreaVO implements Serializable {

    /**
     * 配送省份
     */
    private String proviceCode;


    /**
     * 配送city
     */
    private String cityCode;

    /**
     * 配送区域
     */
    private String districtCode;

    /**
     * 配送街道
     */
    private String streetCode;


    /**
     * 乡镇件标识:默认false
     */
    private boolean villageFlag;


    /**
     * 跨商家凑门槛数量集合
     */
    private List<ExpressAreaStoreVO> areaStoreList;

    /**
     * 商家货物信息
     */
    @Data
    public static class ExpressAreaStoreVO implements Serializable {

        /**
         * 配送件数
         */
        private Long quantity;

        /**
         * 店铺id
         */
        private String storeId;

        /**
         * 总重量
         */
        private Double totalWeight;

        /**
         * 商家发货省份
         */
        private String deliverProviceCode;
    }
}

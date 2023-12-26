package com.wanmi.sbc.tms.api.domain.vo;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TmsOrderAreaVO implements Serializable {

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
     * 乡镇件是否只加收一票
     */
    private boolean firstPlusFlag;

    /**
     * 配送件数
     */
    @Deprecated
    private Long quantity;

    /**
     * 客户id
     */
    private String receiverId;

    /**
     * 跨商家凑门槛数量集合
     */
    private List<TmsOrderAreaStoreVO> areaStoreList;
}

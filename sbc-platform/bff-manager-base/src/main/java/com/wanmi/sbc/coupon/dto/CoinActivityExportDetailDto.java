package com.wanmi.sbc.coupon.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/5/25 16:16
 */
@Data
public class CoinActivityExportDetailDto implements Serializable {
    private static final long serialVersionUID = 2276142968169772751L;

    private String activityName;
    private String activityType;
    private String activityTime;
    private String activityStatus;
    private String activityArea;
    private String isSupper;
    private String coinNum;
    private String erpNo;
    private String goodsName;
    private String goodsCate;
    private String goodsBrand;
    private String price;
    private String goodsStatus;
    
    private String supplierName;
    private String storeName;
}

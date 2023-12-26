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
public class CoinActivityExportDto implements Serializable {

    private static final long serialVersionUID = -2339685958478226353L;

    private String erpNo;
    private String skuNo;
    private String goodsName;
    private String goodsCate;
    private String goodsBrand;
    private String activityType;
    private String activityName;
    private String activityTime;
    private String coinNum;
    private String activityArea;
    private String isSupper;
    private String isTermination;
    private String lastOperator;
    private String lastUpdateTime;

}

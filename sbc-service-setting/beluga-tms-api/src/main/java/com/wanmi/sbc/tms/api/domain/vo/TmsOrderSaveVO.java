package com.wanmi.sbc.tms.api.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-09-19 09:55
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmsOrderSaveVO implements Serializable {
    private static final long serialVersionUID = 1466616553963975952L;

    //   ("收件人省份id")
    private String receiverProvinceCode;

    //   ("收件人城市id")
    private String receiverCityCode;

    //   ("收件人区县id")
    private String receiverDistrictCode;

    //   ("收件人街道code")
    private String receiverStreetCode;

    //   ("收件人详细地址")
    private String receiverAddress;

    //   ("收件人姓名")
    private String receiverName;

    //   ("收件人ID")
    private String receiverId;

    //   ("收件人电话")
    private String receiverPhone;

    //   ("发件人省份id")
    private String senderProvinceCode;

    //   ("发件人城市id")
    private String senderCityCode;

    //   ("发件人区县id")
    private String senderDistrictCode;


    //   ("发件人街道id")
    private String senderStreetCode;

    //   ("发件人详细地址")
    private String senderAddress;

    //   ("发件人姓名")
    private String senderName;

    // ("发件人id，承运商id")
    private String senderId;

    //   ("发件人电话")
    private String senderPhone;

    //   ("接货站点Id")
    private String shipmentSiteId;

    //   ("接货站点名称")
    private String shipmentSiteName;

    //   ("提货站点Id")
    private String pickupSiteId;

    //   ("提货站点名称")
    private String pickupSiteName;

    //   ("付款方式,1.预结2到付")
    private Integer paymentMethod;

    //   ("付款状态,1.未付2已付")
    private Integer paymentStatus;

    //   ("运单金额")
    private BigDecimal amount;

    //   ("运单总件数")
    private Integer quantity;

    //   ("预计到达时间")
    private LocalDateTime estimatedArrivalTime;

    //   ("距离，单位：公里")
    private BigDecimal distance;

    //   ("乡镇件标识:0否1是")
    private Integer villageFlag;

    //   ("备注")
    private String remark;
}

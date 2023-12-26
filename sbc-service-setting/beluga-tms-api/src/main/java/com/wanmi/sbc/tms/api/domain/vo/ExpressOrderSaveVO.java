package com.wanmi.sbc.tms.api.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 快递到家-运单保存VO
 * @author jkp
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpressOrderSaveVO implements Serializable {

    //("收件人省份id")
    private String receiverProvinceCode;

    //("收件人城市id")
    private String receiverCityCode;

    //("收件人区县id")
    private String receiverDistrictCode;

    //("收件人街道code")
    private String receiverStreetCode;

    //("收件人详细地址")
    private String receiverAddress;

    //("收件人姓名")
    private String receiverName;

    //("收件人id")
    private String receiverId;

    //("收件人电话")
    private String receiverPhone;

    //("发件人省份id")
    private String senderProvinceCode;

    //("发件人城市id")
    private String senderCityCode;

    //("发件人区县id")
    private String senderDistrictCode;

    //("发件人详细地址")
    private String senderStreetCode;

    //("发件人姓名")
    private String senderName;

    //("发件人详细地址")
    private String senderAddress;

    //("发件人id(承运商)")
    private String senderId;

    //("发件人电话")
    private String senderPhone;

    //("运单金额")
    private BigDecimal amount;

    //("运单重量")
    private BigDecimal weight;

    //("运单总件数")
    private Integer quantity;

    //("乡镇件标识:0否1是")
    private Integer villageFlag;

    //("备注")
    private String remark;
}

package com.wanmi.sbc.tms.api.domain.vo;



import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 承运单
 * </p>
 *
 * @author xyy
 * @since 2023-09-18
 */
@Getter
@Setter
public class TmsOrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //   ("id")
    private String id;

    //   ("订单类型，1为同城订单，2为城际订单")
    private Integer orderType;

    //   ("取件类型，1为网点自寄，2为上门取件")
    private Integer pickupType;

    //   ("下单时间")
    private LocalDateTime tradeTime;


    //   ("第三方发货单号")
    private String thirdPartyDeliveryOrderNo;

    //   ("第三方发货单号类型")
    private String thirdPartyDeliveryType;

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

    private String receiverId;

    //   ("收件人电话")
    private String receiverPhone;

    //   ("发件人省份id")
    private String senderProvinceCode;

    //   ("发件人城市id")
    private String senderCityCode;

    //   ("发件人区县id")
    private String senderDistrictCode;

    //   ("发件人详细地址")
    private String senderStreetCode;

    //   ("发件人姓名")
    private String senderName;

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

    //   ("运单状态(1.新建 2.已装车 3.到达 4.到达终端网点 5.已签收 6.拒收)")
    private Integer status;

    //   ("备注")
    private String remark;

    //   ("创建时间")
    private LocalDateTime createdAt;

    //   ("最后修改时间")
    private LocalDateTime updatedAt;

    //   ("创建人")
    private String createdBy;

    //   ("最后修改人")
    private String updatedBy;

    //   ("删除标志（0代表存在 1代表删除）")
    private Integer delFlag;

    //("计算运费规则")
    private String freightRule;

    //("第三方发货单号信息")
    private String thirdPartyDeliveryInfo;

    //("实际发货数量")
    private Integer deliverQuantity;

    //("实际发货运费金额")
    private BigDecimal deliverAmount;

    //("未发货需要退款的运费金额")
    private BigDecimal refundAmount;

    //   ("接货站点联系电话")
    private String shipmentSiteMobile;
}

package com.wanmi.sbc.order.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 承运单面单基本信息DTO
 */
@Data
public class TmsOrderBillNoDTO implements Serializable {

    @ApiModelProperty("运单id")
    private String tmsOrderId;
    @ApiModelProperty("订单id")
    private String tradeOrderId;

    @ApiModelProperty("商家id")
    private Long storeId;
    @ApiModelProperty("商家名称")
    private String storeName;

    @ApiModelProperty("收货人名称")
    private String receiverName;

    @ApiModelProperty("收货人电话")
    private String receiverPhone;
    @ApiModelProperty("运单人详细地址")
    private String receiverAddress;
    @ApiModelProperty("接货点标识（顺丰）")
    private String areaName;

    @ApiModelProperty("面单信息（顺丰）")
    private List<BillNoItemDTO> billNoList;



    @Data
    public static class BillNoItemDTO{

        /**
         * 序号
         */
        private String idx;

        /**
         * 运单号类型1：母单 2 :子单 3 : 签回单
         */
        private Integer waybillType =1;

        /**
         * 母单号
         */
        private String originBillNo;

        /**
         * 子单号
         */
        private String childBillNo;
    }
}

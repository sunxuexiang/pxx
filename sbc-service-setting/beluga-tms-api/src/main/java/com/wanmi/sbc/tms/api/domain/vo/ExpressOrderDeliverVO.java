package com.wanmi.sbc.tms.api.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 快递到家-发货VO类
 * @author jkp
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpressOrderDeliverVO implements Serializable {

    //("交易订单ID")
    private String tradeOrderId;

    //("承运单ID")
    private String expressOrderId;

    //("第三方发货单号")
    private String thirdOrderNo;

    //("第三方发货单号类型")
    private String thirdOrderType;

    //("接货点ID")
    private String shipmentSiteId;

    //("接货点名称")
    private String shipmentSiteName;

    /**
     * 发货信息
     */
    private List<ExpressOrderDeliverGoodsVO> goodsList;

    @Data
    public static class ExpressOrderDeliverGoodsVO implements Serializable {

        //("商品skuId")
        private String skuId;

        //("实际发货数量")
        private Integer deliverNum;

        //("单箱货物重量")
        private Double weight;

        //("欠货数量")
        private Integer refundNum;
    }
}

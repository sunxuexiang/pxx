package com.wanmi.sbc.pay.api.request;

import com.wanmi.sbc.pay.bean.enums.RefundSourceType;
import com.wanmi.sbc.pay.bean.enums.RefundType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundByChannelRequest {
    /**
     * 订单对应各支付方式退款金额
     */
    Map<String, List<RefundItem>> payTypeRefundItemsByOrderMap;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RefundItem {
        @ApiModelProperty(value = "退款来源类型")
        private RefundSourceType sourceType;

        @ApiModelProperty(value = "退款方式")
        private RefundType refundType;

        @ApiModelProperty(value = "商户id-boss端取默认值")
        @NotNull
        private Long storeId;

        @ApiModelProperty(value = "关联的订单业务id")
        private String bizId;

        @ApiModelProperty(value = "退单业务id")
        private String refundBizId;

        @ApiModelProperty(value = "总在线交易金额")
        private BigDecimal totalOnlineTradePrice;

        @ApiModelProperty(value = "退款金额")
        private BigDecimal refundAmount;

        @ApiModelProperty(value = "描述信息")
        private String description;

        @ApiModelProperty(value = "附加信息")
        private Map<String, String> extraInfos;

        @ApiModelProperty(value = "订单ID")
        private String tid;

        @ApiModelProperty(value = "是否退运费")
        private Boolean refundFreight;

        @ApiModelProperty(value = "运费金额")
        private BigDecimal freightPrice;
    }
}



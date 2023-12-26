package com.wanmi.sbc.order.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryOrderDeliverWayVO implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 主键
     */
    private Long id;


    /**
     * 店铺id
     */
    private Long storeId;


    /**
     * 会员id
     */
    private String customerId;


    /**
     * 收货地址ID
     */
    private String consigneeId;


    /**
     * 发货方式
     */
    private Integer deliverWay;


    /**
     * 订单id
     */
    private String lastTradeId;


    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

}

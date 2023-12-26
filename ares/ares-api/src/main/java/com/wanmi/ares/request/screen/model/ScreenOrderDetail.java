package com.wanmi.ares.request.screen.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.ms.util.CustomLocalDateTimeDeserializer;
import com.wanmi.ms.util.CustomLocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
public class ScreenOrderDetail implements Serializable {

    private BigInteger detailId; // 逐渐id
    private String orderId; // 订单id
    private long productNum; // 商品购买数量
    private BigDecimal productPrice; // 商品成交价格
    private long cateId; // 商品分类id
    private String cateName; //商品分类名称
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime; //商品创建时间
}

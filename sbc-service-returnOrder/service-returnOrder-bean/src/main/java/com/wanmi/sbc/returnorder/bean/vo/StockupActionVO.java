package com.wanmi.sbc.returnorder.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Convert;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description: 提货明细VO实体类
 * @author: XinJiang
 * @time: 2021/12/17 16:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class StockupActionVO {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 父订单号
     */
    @ApiModelProperty(value = "父订单号")
    private String pid;

    /**
     * 提货订单号
     */
    @ApiModelProperty(value = "提货订单号")
    private String orderCode;

    /**
     * 订单总金额
     */
    @ApiModelProperty(value = "订单总金额")
    private BigDecimal orderTotalPrice;

    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    private String customerId;

    /**
     * 商品spuId
     */
    @ApiModelProperty(value = "商品spuId")
    private String goodsId;

    /**
     * 商品skuId
     */
    @ApiModelProperty(value = "商品skuId")
    private String goodsInfoId;

    /**
     * 商品单价
     */
    @ApiModelProperty(value = "商品单价")
    private BigDecimal goodsSplitPrice;

    /**
     * 购买数量
     */
    @ApiModelProperty(value = "购买数量")
    private Long goodsNum;

    /**
     * 创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}

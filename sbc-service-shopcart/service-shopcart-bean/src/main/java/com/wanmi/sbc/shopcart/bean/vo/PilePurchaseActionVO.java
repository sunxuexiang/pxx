package com.wanmi.sbc.shopcart.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Convert;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description: 囤货明细视图实体类
 * @author: XinJiang
 * @time: 2021/12/20 16:39
 */
@Data
@ApiModel
public class PilePurchaseActionVO implements Serializable {

    private static final long serialVersionUID = 7756207015064403971L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 订单pid
     */
    private String pid;

    /**
     * 采购单编号
     */
    private Long purchaseId;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 订单总金额
     */
    private BigDecimal orderTotalPrice;

    private String  customerId;

    private String goodsId;

    private String goodsInfoId;

    /**
     * 订单优惠后商品均摊价
     */
    private BigDecimal goodsSplitPrice;

    private Long companyInfoId;

    private Long goodsNum;

    /**
     * 提货状态
     * 0-待提货，1-部分提货，3-提货完成
     */
    private Integer takeGoodsStatus;

    /**
     * 采购创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    private String inviteeId;
}

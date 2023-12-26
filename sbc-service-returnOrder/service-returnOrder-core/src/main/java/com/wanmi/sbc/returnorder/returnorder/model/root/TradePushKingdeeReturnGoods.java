package com.wanmi.sbc.returnorder.returnorder.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "push_kingdee_return_goods")
public class TradePushKingdeeReturnGoods implements Serializable{
    private static final long serialVersionUID = 4460300505736847191L;

    /**
     * 推送金蝶退货单id
     */
    @Id
    @Column(name = "push_kingdee_return_goods_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pushKingdeeReturnGoodsId;

    /**
     * 退货单编号
     */
    @Column(name = "return_goods_code")
    private String returnGoodsCode;

    /**
     * 推送给金蝶状态，0：创建 1：推送成功 2推送失败
     */
    @Column(name = "push_status")
    private Integer pushStatus;

    /**
     * 销售订单
     */
    @Column(name = "order_code")
    private String orderCode;

    /**
     * 支付类型
     */
    @Column(name = "pay_type")
    private String payType;

    /**
     * 说明
     */
    @Column(name = "instructions")
    private String instructions;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;
}

package com.wanmi.sbc.walletorder.trade.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 缓存销售订单推金蝶
 *
 * @author yitang
 * @version 1.0
 */
@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cache_push_kingdee_order")
public class TradeCachePushKingdeeOrder implements Serializable {

    /**
     * 推送金蝶订单id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "push_kingdee_id")
    private Long pushKingdeeId;

    /**
     * 销售订单编号
     */
    @Column(name = "order_code")
    private String orderCode;

    /**
     * 销售父订单编号
     */
    @Column(name = "parent_id")
    private String parentId;

    /**
     * 推送给金蝶状态，0：创建 1：已推送 -1人工干预
     */
    @Column(name = "push_status")
    private Integer pushStatus;

    /**
     * 订单状态：0：创建  1：已作废
     */
    @Column(name = "order_status")
    private Integer orderStatus;


    /**
     * 是否进入循环：0：进入1：不能进入
     */
    @Column(name = "flag_id")
    private String flagId;



    /**
     * 错误次数
     */
    @Column(name = "erro_num")
    private Integer erroNum=0;


    /**
     * 业务原因
     */
    @Column(name = "erro_reson")
    private Integer erroReson;


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

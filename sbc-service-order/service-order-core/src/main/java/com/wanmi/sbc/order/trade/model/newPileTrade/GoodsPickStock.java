package com.wanmi.sbc.order.trade.model.newPileTrade;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 提货囤货关联表
 */
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "goods_pick_stock")
@Data
public class GoodsPickStock implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "new_pile_trade_no")
    private String newPileTradeNo;

    @Column(name = "goods_info_id")
    private String goodsInfoId;


    @Column(name = "goods_info_no")
    private String goodsInfoNo;


    @Column(name = "goodsId")
    private String goodsId;

    @Column(name = "stock")
    private Long stock;

    @Column(name = "state")
    private Integer state;

    @Column(name = "create_time")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    @Column(name = "ware_id")
    private Long wareId;
}

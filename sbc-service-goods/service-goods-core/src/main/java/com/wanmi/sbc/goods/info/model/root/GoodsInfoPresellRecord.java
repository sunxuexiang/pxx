package com.wanmi.sbc.goods.info.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 预售库存更新记录表
 */
@Data
@Entity
@Table(name = "goods_info_presell_record")
public class GoodsInfoPresellRecord {

    /**
     * 编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * SPU标识
     */
    @Column(name = "goods_id")
    private String goodsId;
    /**
     * SKU标识
     */
    @Column(name = "goods_info_id")
    private String goodsInfoId;

    /**
     *店铺id
     */
    @Column(name = "store_id")
    private Long storeId;


    /**
     *仓库id
     */
    @Column(name = "ware_id")
    private Long wareId;

    /**
     *当前预售库存
     */
    @Column(name = "presell_count")
    private Long presellCount;


    /**
     *订单id
     */
    @Column(name = "trade_id")
    private String tradeId;

    /**
     * 库存变更类型:0扣减库存，1增加库存
     */
    @Column(name = "type")
    private Integer type;


    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;
}

package com.wanmi.sbc.goods.goodswarestock.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @description: 乡镇件 未推送wms 累计库存实体类
 * @author: XinJiang
 * @time: 2022/4/27 10:05
 */
@Data
@Entity
@Table(name = "goods_ware_stock_villages")
public class GoodsWareStockVillages implements Serializable {

    private static final long serialVersionUID = -6683590576280319681L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * sku ID
     */
    @Column(name = "goods_info_id")
    private String goodsInfoId;

    /**
     * sku编码
     */
    @Column(name = "goods_info_no")
    private String goodsInfoNo;

    /**
     * spu ID
     */
    @Column(name = "goods_id")
    private String goodsId;

    /**
     * 店铺id
     */
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 仓库ID
     */
    @Column(name = "ware_id")
    private Long wareId;

    /**
     * 货品库存
     */
    @Column(name = "stock")
    private BigDecimal stock;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @Column(name = "create_person")
    private String createPerson;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 编辑人
     */
    @Column(name = "update_person")
    private String updatePerson;

    /**
     * 是否删除标志 0：否，1：是
     */
    @Column(name = "del_flag")
    @Enumerated
    private DeleteFlag delFlag;

    /**
     * 商品id+库存id
     */
    @Column(name = "goods_info_ware_id")
    private String goodsInfoWareId;

    @Transient
    private String wareName;
}

package com.wanmi.sbc.goods.goodswarestock.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>sku分仓库存表实体类</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:22:56
 */
@Data
@Entity
@Table(name = "goods_ware_stock")
public class GoodsWareStock extends BaseEntity {
    private static final long serialVersionUID = 1L;

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
     * 锁定库存
     */
    @Column(name = "lock_stock", insertable = false, updatable = false)
    private BigDecimal lockStock;

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

    /**
     * 相对最小单位的换算率
     */
    @Column(name = "add_step")
    private BigDecimal addStep;

    /**
     * 相对主物料的换算率
     */
    @Column(name = "main_add_step")
    private BigDecimal mainAddStep;

    /**
     * 销售类别(0:批发,1:零售,2散批)
     */
    @Column(name = "sale_type")
    private Integer saleType;

    /**
     * 主物料的Id
     */
    @Column(name = "main_sku_id")
    private String mainSkuId;

    /**
     * 主物料仓库的Id
     */
    @Column(name = "main_sku_ware_id")
    private Long mainSkuWareId;

    /**
     * 父物料库存Id
     */
    @Column(name = "parent_goods_ware_stock_id")
    private Long parentGoodsWareStockId;

    @Transient
    private BigDecimal selfStock;

    @Transient
    private BigDecimal parentStock;

    @Transient
    private String wareName;
}

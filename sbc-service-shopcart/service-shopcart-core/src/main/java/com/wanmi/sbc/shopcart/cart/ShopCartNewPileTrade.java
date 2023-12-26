package com.wanmi.sbc.shopcart.cart;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 采购单实体
 * Created by sunkun on 2017/11/27.
 */
@Data
@Entity
@Table(name = "shop_cart_new_pile_trade")
@NoArgsConstructor
@AllArgsConstructor
public class ShopCartNewPileTrade {
    /**
     * 采购单编号
     */
    @Id
    @Column(name = "cart_id")
    private Long cartId;

    /**
     * 客户编号
     */
    @Column(name = "customer_id")
    private String customerId;

    /**
     * 默认为0、店铺精选时对应邀请人id-会员id
     */
    @ApiModelProperty(value = "邀请人id")
    String inviteeId;

    /**
     * 商品编号
     */
    @Column(name = "goods_id")
    private String goodsId;

    /**
     * SKU编号
     */
    @Column(name = "goods_info_id")
    private String goodsInfoId;

    /**
     * 全局购买数
     */
    @Column(name = "goods_num")
    private Long goodsNum;


    @Transient
    private BigDecimal goodsBNum;


    /**
     * 非数据库字段删除标识
     */
    @Transient
    private Boolean isDelFlag =false;


    /**
     * 公司信息ID
     */
    @Column(name = "company_info_id")
    private Long companyInfoId;

    /**
     * 商品是否选中
     */
    @Column(name = "is_check")
    private DefaultFlag isCheck;

    /**
     * 采购创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 根据有效商品排序
     */
    @Column(name = "valid_sort")
    private Integer validSort;


    /**
     * 装箱id
     */
    @Column(name = "devanning_id")
    private Long devanningId;


    /**
     * 仓库ID
     */
    @Column(name = "ware_id")
    private Long wareId;

    /**
     * 父级SKUID
     */
    @Column(name = "parent_goods_info_id")
    private String parentGoodsInfoId;


}


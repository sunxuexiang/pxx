package com.wanmi.sbc.goods.goodsdevanning.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.annotation.CanEmpty;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.goodslabel.model.root.GoodsLabel;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Proxy;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品实体类
 * Created by dyt on 2017/4/11.
 */
@Proxy(lazy = false)
@Data
@Entity
@Table(name = "goods_devanning")
public class GoodsDevanning {




    /**
     * 商品编号，采用UUID
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "devanning_goods_id")
    private String devanningGoodsId;

    /**
     * 商品编号，采用UUID
     */
    @Column(name = "goods_id")
    private String goodsId;

    /**
     * 分类编号
     */
    @Column(name = "cate_id")
    private Long cateId;

    /**
     * 销售类别
     */
    @Column(name = "sale_type")
    private Integer saleType;

    /**
     * 品牌编号
     */
    @Column(name = "brand_id")
    @CanEmpty
    private Long brandId;

    /**
     * 商品名称
     */
    @Column(name = "goods_name")
    private String goodsName;

    /**
     * 商品副标题
     */
    @Column(name = "goods_subtitle")
    private String goodsSubtitle;

    /**
     * SPU编码
     */
    @Column(name = "goods_no")
    private String goodsNo;

    /**
     * 计量单位
     */
    @Column(name = "goods_unit")
    @CanEmpty
    private String goodsUnit;

    /**
     * 商品主图
     */
    @Column(name = "goods_img")
    @CanEmpty
    private String goodsImg;

    /**
     * 商品视频地址
     */
    @Column(name = "goods_video")
    @CanEmpty
    private String goodsVideo;

    /**
     * 商品重量
     */
    @Column(name = "goods_weight")
    private BigDecimal goodsWeight;

    /**
     * 市场价
     */
    @Column(name = "market_price")
    @CanEmpty
    private BigDecimal marketPrice;

    /**
     * 大客户价
     */
    @Column(name = "vip_price")
    @CanEmpty
    private BigDecimal vipPrice;

    /**
     * 供货价
     */
    @Column(name = "supply_price")
    @CanEmpty
    private BigDecimal supplyPrice;

    /**
     * 建议零售价
     */
    @Column(name = "recommended_retail_price")
    @CanEmpty
    private BigDecimal recommendedRetailPrice;

    /**
     * 商品类型，0:实体商品，1：虚拟商品 2.特价商品
     */
    @Column(name = "goods_type")
    private Integer goodsType;

    /**
     * 划线价格
     */
    @Column(name = "line_price")
    @CanEmpty
    private BigDecimal linePrice;
    /*
     * 商品浏览量
     */
    @Column(name = "goods_view_num")
    private Long goodsViewNum;

    /**
     * 成本价
     */
    @Column(name = "cost_price")
    @CanEmpty
    private BigDecimal costPrice;

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

    /**
     * 上下架时间
     */
    @Column(name = "added_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime addedTime;

    /**
     * 商品来源，0供应商，1商家
     */
    @Column(name = "goods_source")
    private Integer goodsSource;

    /**
     * 删除标记
     */
    @Column(name = "del_flag")
    @Enumerated
    private DeleteFlag delFlag;

    /**
     * 上下架状态
     */
    @Column(name = "added_flag")
    private Integer addedFlag;

    /**
     * 是否多规格标记
     */
    @Column(name = "more_spec_flag")
    private Integer moreSpecFlag;

    /**
     * 设价类型 0:客户,1:订货
     */
    @Column(name = "price_type")
    private Integer priceType;

    /**
     * 是否按客户单独定价
     */
    @Column(name = "custom_flag")
    private Integer customFlag;

    /**
     * 订货量设价时,是否允许sku独立设阶梯价(0:不允许,1:允许)
     */
    @Column(name = "allow_price_set")
    private Integer allowPriceSet;

    /**
     * 是否叠加客户等级折扣
     */
    @Column(name = "level_discount_flag")
    private Integer levelDiscountFlag;

    /**
     * 公司信息ID
     */
    @Column(name = "company_info_id")
    private Long companyInfoId;

    /**
     * 公司名称
     */
    @Column(name = "supplier_name")
    private String supplierName;


    /**
     * 店铺ID
     */
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 提交审核时间
     */
    @Column(name = "submit_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime submitTime;

    /**
     * 审核状态
     */
    @Column(name = "audit_status")
    @Enumerated
    private CheckStatus auditStatus;

    /**
     * 审核驳回原因
     */
    @Column(name = "audit_reason")
    private String auditReason;

    /**
     * 商品详情
     */
    @Column(name = "goods_detail")
    private String goodsDetail;

    /**
     * 商品移动端详情
     */
    @Column(name = "goods_mobile_detail")
    private String goodsMobileDetail;

    /**
     * 库存，根据相关所有SKU库存来合计
     */
    @Transient
    private Long stock;

    /**
     * 一对多关系，多个SKU编号
     */
    @Transient
    private List<String> goodsInfoIds;

    /**
     * 多对多关系，多个店铺分类编号
     */
    @Transient
    private List<Long> storeCateIds;

    /**
     * 商家类型 0、平台自营 1、第三方商家
     */
    @Column(name = "company_type")
    @Enumerated
    private CompanyType companyType;

    /**
     * 商品体积 单位：m3
     */
    @Column(name = "goods_cubage")
    private BigDecimal goodsCubage;

    /**
     * 运费模板ID
     */
    @Column(name = "freight_temp_id")
    private Long freightTempId;

    /**
     * 运费模板名称
     */
    @Transient
    private String freightTempName;

    /**
     * 商品评论数
     */
    @Column(name = "goods_evaluate_num")
    private Long goodsEvaluateNum;

    /**
     * 商品收藏量
     */
    @Column(name = "goods_collect_num")
    private Long goodsCollectNum;

    /**
     * 商品销量
     */
    @Column(name = "goods_sales_num")
    private Long goodsSalesNum;

    /**
     * 商品好评数量
     */
    @Column(name = "goods_favorable_comment_num")
    private Long goodsFavorableCommentNum;

    /**
     * 供应商名称
     */
    @Column(name = "provider_name")
    private String providerName;

    /**
     * 供应商id
     */
    @Column(name = "provider_id")
    private Long providerId;

    /**
     * 所属供应商商品Id
     */
    @Column(name = "provider_goods_id")
    private String providerGoodsId;

    /**
     * 特价
     */
    @Column(name = "special_price")
    @CanEmpty
    private BigDecimal specialPrice;

    /**
     * 商品排序序号
     */
    @Column(name = "goods_seq_num")
    private Integer goodsSeqNum;

    /**
     * 最小价格
     */
    @Column(name = "minimum_price")
    private BigDecimal minimumPrice;


    @Column(name = "label_id_str")
//    @Transient
    private String labelIdStr;

    /**
     * 锁定库存
     */
    @Column(name = "lock_stock")
    private Integer lockStock;

    /**
     * 步长
     */
    @Column(name = "step")
    private BigDecimal step;


    @Transient
    private List<GoodsLabel> goodsLabels;

    /**
     * 批次号
     */
    @Transient
    private String goodsInfoBatchNo;

    /**
     * 营销id
     */
    @Transient
    private Long marketingId;

    /**
     * 限购数量
     */
    @Transient
    private Long purchaseNum;




}

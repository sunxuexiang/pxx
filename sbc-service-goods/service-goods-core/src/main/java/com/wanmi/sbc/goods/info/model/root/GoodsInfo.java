package com.wanmi.sbc.goods.info.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.annotation.CanEmpty;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.EnterpriseAuditState;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.goodsattributekey.root.GoodsAttributeKey;
import com.wanmi.sbc.goods.goodslabel.model.root.GoodsLabel;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.marketing.CouponLabel;
import com.wanmi.sbc.goods.marketing.MarketingLabel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * 商品SKU实体类
 * Created by dyt on 2017/4/11.
 */
@Data
@Entity
@Table(name = "goods_info")
@NamedEntityGraph(name = "GoodsInfo.Graph", attributeNodes = {@NamedAttributeNode(value = "goods")})
public class GoodsInfo implements Serializable {

    /**
     * 拆箱主键id goodsinfovo需要加拆箱主键 在查询商品快照接口需要用到
     */
    @Transient
    private Long devanningId;

    @Transient
    private BigDecimal divisorFlag;


    /**
     * 商品SKU编号
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "goods_info_id")
    private String goodsInfoId;

    /**
     * 商品编号
     */
    @Column(name = "goods_id")
    private String goodsId;

    /**
     * 商品SKU名称
     */
    @Column(name = "goods_info_name")
    private String goodsInfoName;

    /**
     * 商品SKU编码
     */
    @Column(name = "goods_info_no")
    private String goodsInfoNo;

    /**
     * erpSKU编码
     */
    @Column(name = "erp_goods_info_no")
    private String erpGoodsInfoNo;

    /**
     * 商品图片
     */
    @Column(name = "goods_info_img")
    @CanEmpty
    private String goodsInfoImg;

    /**
     * 商品条形码
     */
    @Column(name = "goods_info_barcode")
    @CanEmpty
    private String goodsInfoBarcode;

    /**
     * 散称或定量
     */
    @Column(name = "is_scattered_quantitative")
    private Integer isScatteredQuantitative;

    /**
     * 二维码
     */
    @Column(name = "goods_info_qrcode")
    private String goodsInfoQrcode;

    /**
     * 商品库存
     */
    @Column(name = "stock")
    private BigDecimal stock;

    /**
     * 锁定库存
     */
    @Column(name = "lock_stock")
    private BigDecimal lockStock;

    /**
     * 囤货虚拟库存
     */
    @Column(name = "virtual_stock")
    private BigDecimal virtualStock;

    /**
     * 商品市场价
     */
    @Column(name = "market_price")
    private BigDecimal marketPrice;

    /**
     * 商品供货价
     */
    @Column(name = "supply_price")
    private BigDecimal supplyPrice;

    /**
     * 建议零售价价
     */
    @Column(name = "retail_price")
    private BigDecimal retailPrice;

    /**
     * 商品成本价
     */
    @CanEmpty
    @Column(name = "cost_price")
    private BigDecimal costPrice;

    /**
     * 大客户价
     */
    @CanEmpty
    @Column(name = "vip_Price")
    private BigDecimal vipPrice;

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
    @Column(name = "added_flag" )
    private Integer addedFlag;

    /**
     * 提货是否校验商品上下架状态
     * 0-否，1或null-是
     */
    @Column(name = "checked_added_flag")
    private Integer checkedAddedFlag = 1;

    /**
     * 询问底价标志 0-否，1-是
     */
    @Column(name = "inquiry_flag")
    private Integer inquiryFlag;

    /**
     * 公司信息ID
     */
    @Column(name = "company_info_id")
    private Long companyInfoId;

    /**
     * 按客户单独定价
     */
    @Column(name = "custom_flag")
    private Integer customFlag;

    /**
     * 是否叠加客户等级折扣
     */
    @Column(name = "level_discount_flag")
    private Integer levelDiscountFlag;

    /**
     * 店铺ID
     */
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 审核状态
     */
    @Enumerated
    @Column(name = "audit_status")
    private CheckStatus auditStatus;

    /**
     * 商家类型 0、平台自营 1、第三方商家
     */
    @Enumerated
    @Column(name = "company_type")
    private CompanyType companyType;

    /**
     * 是否独立设价 1:是 0:否
     */
    @Column(name = "alone_flag")
    private Boolean aloneFlag;

    /**
     * 商品详情小程序码
     */
    @Column(name = "small_program_code")
    private String smallProgramCode;

    /**
     * 预估佣金
     */
    @Column(name = "distribution_commission")
    private BigDecimal distributionCommission;

    /**
     * 分销销量
     */
    @Column(name = "distribution_sales_count")
    private Integer distributionSalesCount;

    /**
     * 分销商品审核状态 0:普通商品 1:待审核 2:已审核通过 3:审核不通过 4:禁止分销
     */
    @Column(name = "distribution_goods_audit")
    private DistributionGoodsAudit distributionGoodsAudit;

    /**
     * 分销商品审核不通过或禁止分销原因
     */
    @Column(name = "distribution_goods_audit_reason")
    private String distributionGoodsAuditReason;

    /**
     * 商品分类ID
     */
    @Column(name = "cate_id")
    private Long cateId;

    /**
     * 佣金比例
     */
    @Column(name = "commission_rate")
    private BigDecimal commissionRate;

    /**
     * 品牌ID
     */
    @Column(name = "brand_id")
    private Long brandId;

    /**
     * 销售类型 0:批发, 1:零售
     */
    @Column(name = "sale_type")
    private Integer saleType;

    /**
     * 企业购商品的价格
     */
    @Column(name = "enterprise_price")
    private BigDecimal enterPrisePrice;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "goods_id", insertable = false, updatable = false)
    private Goods goods;


    /**
     * 企业购商品审核状态
     */
    @Column(name = "enterprise_goods_audit")
    @Enumerated
    private EnterpriseAuditState enterPriseAuditState;

    /**
     * 企业购商品审核被驳回的原因
     */
    @Column(name = "enterprise_goods_audit_reason")
    private String enterPriseGoodsAuditReason;

    /**
     * 所属供应商商品skuId
     */
    @Column(name = "provider_goods_info_id")
    private String providerGoodsInfoId;

    /**
     * 供应商Id
     */
    @Column(name = "provider_id")
    private Long providerId;

    /**
     * 批次号
     */
    @Column(name = "goods_info_batch_no")
    private String goodsInfoBatchNo;

    /**
     * 特价
     */
    @Column(name = "special_price")
    private BigDecimal specialPrice;

    /**
     * 商品状态
     */
    @Column(name = "goods_info_type")
    private Integer goodsInfoType;

    /**
     * 增加的步长
     */
    @Column(name = "add_step")
    private BigDecimal addStep;

    /**
     * 关键词、分类
     */
    @Column(name = "key_words")
    private String keyWords;

    /**
     * 竞价排名的关键字排序值
     */
    @Column(name = "sort_num_key")
    private Integer sortNumKey;

    /**
     * 竞价排名的分类排序值
     */
    @Column(name = "sort_num_cate")
    private Integer sortNumCate;

    /**
     * 保质期
     */
    @Column(name = "shelflife")
    private Long shelflife;

    /**
     * 映射表伪spuId
     */
    @Column(name = "parent_goods_info_id")
    private String parentGoodsInfoId;


    /**
     * 仓库id
     */
    @Column(name = "ware_id")
    private Long wareId;


    /**
     * 单位
     */
    @Column(name = "goods_info_unit")
    private String goodsInfoUnit;
    /**
     * 重量
     */
    @Column(name = "goods_info_weight")
    private BigDecimal goodsInfoWeight;
    /**
     * 体积
     */
    @Column(name = "goods_info_cubage")
    private BigDecimal goodsInfoCubage;
    /**
     * 是否允许独立设价 0:不允许, 1:允许
     */
    @Transient
    private Integer allowPriceSet;

    /**
     * 多对多关系，多个店铺分类编号
     */
    @Transient
    private List<Long> storeCateIds;

    /**
     * 最新计算的会员价
     * 为空，以市场价为准
     */
    @Transient
    private BigDecimal salePrice;

    /**
     * 设价类型 0:客户,1:订货
     */
    @Transient
    private Integer priceType;

    /**
     * 新增时，模拟多个规格ID
     * 查询详情返回响应，扁平化多个规格ID
     */
    @Transient
    private List<Long> mockSpecIds;

    /**
     * 新增时，模拟多个规格值 ID
     * 查询详情返回响应，扁平化多个规格值ID
     */
    @Transient
    private List<Long> mockSpecDetailIds;

    /**
     * 商品分页，扁平化多个商品规格值ID
     */
    @Transient
    private List<Long> specDetailRelIds;

    /**
     * 购买量
     */
    @Transient
    private Long buyCount = 0L;

    /**
     * 最新计算的起订量
     * 为空，则不限
     */
    @Transient
    private Long count;

    /**
     * 最新计算的限定量
     * 为空，则不限
     */
    @Transient
    private Long maxCount;

    /**
     * 一对多关系，多个订货区间价格编号
     */
    @Transient
    private List<Long> intervalPriceIds;

    /**
     * 规格名称规格值 颜色:红色;大小:16G
     */
    @Transient
    private String specText;

    /**
     * 最小区间价
     */
    @Transient
    private BigDecimal intervalMinPrice;

    /**
     * 最大区间价
     */
    @Transient
    private BigDecimal intervalMaxPrice;

    /**
     * 有效状态 0:无效,1:有效
     */
    @Transient
    private Integer validFlag;

    /**
     * 前端是否选中
     */
    @Transient
    private Boolean checked = false;

    /**
     * 商品状态 0：正常 1：缺货 2：失效
     */
    @Transient
    private GoodsStatus goodsStatus = GoodsStatus.OK;

    /**
     * 计算单位
     */
    @Transient
    private String goodsUnit;

    /**
     * 促销标签
     */
    @Transient
    private List<MarketingLabel> marketingLabels = new ArrayList<>();

    /**
     * 优惠券标签
     */
    @Transient
    private List<CouponLabel> couponLabels = new ArrayList<>();

    /**
     * 商品体积 单位：m3
     */
    @Transient
    private BigDecimal goodsCubage;

    /**
     * 商品重量
     */
    @Transient
    private BigDecimal goodsWeight;

    /**
     * 运费模板ID
     */
    @Transient
    private Long freightTempId;

    /**
     * 商品评论数
     */
    @Transient
    private Long goodsEvaluateNum;

    /**
     * 商品收藏量
     */
    @Transient
    private Long goodsCollectNum;

    /**
     * 商品销量
     */
    @Transient
    private Long goodsSalesNum;

    /**
     * 商品好评数
     */
    @Transient
    private Long goodsFavorableCommentNum;

    /**
     * sku关联仓库库存
     */
    @Transient
    private List<GoodsWareStock> goodsWareStocks;

    /**
     * 购物车中是否选中
     */
    @Transient
    private DefaultFlag isCheck;

    @Transient
    private List<GoodsLabel> goodsLabels = new ArrayList<>();

    /**
     * 产地
     */
    @Column(name = "origin")
    private String origin;

    /**
     * 产地code
     */
    @Column(name = "origin_code")
    private Long originCode;

    /**
     * 该商品允许销售的地区id
     */
    @Column(name = "allowed_purchase_area")
    private String allowedPurchaseArea;

    /**
     * 该商品允许销售的地区名称
     */
    @Column(name = "allowed_purchase_area_name")
    private String allowedPurchaseAreaName;

    /**
     * 单笔订单指定限购区域id，用“，”隔开
     */
    @Column(name = "single_order_assign_area")
    private String singleOrderAssignArea;

    /**
     * 单笔订单指定限购区域名称，用“，”隔开
     */
    @Column(name = "single_order_assign_area_name")
    private String singleOrderAssignAreaName;

    /**
     * 单笔订单限购数量
     */
    @Column(name = "single_order_purchase_num")
    private Long singleOrderPurchaseNum;

    /**
     * 是否指定区域
     */
    @Column(name = "area_flag")
    private BoolFlag areaFlag;

    /**
     * 指定区域更新时间
     */
    @Column(name = "area_update_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime areaUpdateTime;


    /**
     * 营销id
     */
    @Column(name = "marketing_id")
    private Long marketingId;

    /**
     * 限购数量
     */
    @Column(name = "purchase_num")
    private Long purchaseNum;

    /**
     * 商品是否可囤货
     */
    @Enumerated
    @Column(name = "is_can_pile")
    private DefaultFlag isCanPile;

    /**
     * 是否隐藏 0否 1是
     */
    @Column(name = "hidden_flag")
    private Integer hiddenFlag = 0;

    /**
     * 是否套装商品 0否 1是；默认否
     */
    @Enumerated
    @Column(name = "is_suit_goods")
    private DefaultFlag isSuitGoods = DefaultFlag.NO;

    /**
     * 拆箱规格（0，1  ，分割）
     */
    @Column(name = "devanning_type")
    private String devanningType;

    /**
     * 选中的本品skuId 当isSuitGoods为“1-是”时为必填项
     */
    @Column(name = "chose_product_skuid")
    private String choseProductSkuId;

    /**
     * 是否零售商品 0否 1是
     */
    @Transient
    private Integer isSupermarketGoods = 0;

    /**
     * 商品副标题
     */
    @Transient
    private String goodsSubtitle;

    /**
     * 是否限制起购数量(零售字段，防止填充时被覆盖，零售和批发通用GoodsInfoVO、GoodsInfoDTO)
     */
    @Transient
    private DefaultFlag isStartBuyNum;
    /**
     * 起购数量（默认为 0 即：不限制）(零售字段，防止填充时被覆盖，零售和批发通用GoodsInfoVO、GoodsInfoDTO)
     */
    @Transient
    private Integer startBuyNum;

    // 体积信息
    @Column(name = "volume_info")
    private String volumeInfo;

    /**
     * 推荐商品排序 默认0
     */
    @Column(name = "recommend_sort")
    private Integer recommendSort;

    /**
     * 是否惊爆价
     */
    @Column(name = "is_surprise_price")
    private DefaultFlag isSurprisePrice;


    /**
     * sku属性关联关系
     */
    @Transient
    private List<GoodsAttributeKey> goodsAttributeKeys;
    /**
     * 主sku）——0 否 1是
     */
    @Column(name = "host_sku")
    private Integer hostSku;

    /**
     * 最小单位
     */
    @Transient
    @ApiModelProperty(value = "最小单位")
    private String devanningUnit;

    /**
     * 预售虚拟库存
     */
    @Column(name = "presell_stock")
    private Long presellStock;

    /**
     * 日期单位
     */
    @Column(name = "date_unit")
    private String dateUnit;

    @PrePersist
    public void prePersist() {
        if (areaFlag == null) {
            // 设置默认值
            areaFlag = BoolFlag.NO;
        }
    }
}

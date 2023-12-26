package com.wanmi.sbc.es.elastic.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.ForcePileFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.EsConstants;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.EnterpriseAuditState;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Enumerated;
import javax.persistence.Id;
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
@ApiModel
public class GoodsInfoNest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品SKU编号
     */
    @Id
    @ApiModelProperty(value = "商品SKU编号")
    private String goodsInfoId;

    /**
     * 映射表伪spuId
     */
    @ApiModelProperty(value = "映射表伪spuId")
    private String parentGoodsInfoId;


    /**
     * 参库code
     */
    @ApiModelProperty(value = "参库Id")
    private Long wareId;


    /**
     * 商品编号
     */
    @ApiModelProperty(value = "商品编号")
    private String goodsId;

    /**
     * 商品SKU名称
     */
    @ApiModelProperty(value = "商品SKU名称")
    @Field(searchAnalyzer = EsConstants.DEF_ANALYZER, analyzer = EsConstants.DEF_ANALYZER, type = FieldType.Text)
    private String goodsInfoName;

    /**
     * 商品SKU编码
     */
    @ApiModelProperty(value = "商品SKU编码")
    private String goodsInfoNo;

    /**
     * 商品图片
     */
    @ApiModelProperty(value = "商品图片")
    private String goodsInfoImg;

    /**
     * 商品条形码
     */
    @ApiModelProperty(value = "erp商品no")
    private String erpGoodsInfoNo;


    /**
     * 商品库存
     */
    @ApiModelProperty(value = "商品库存")
    @Field(type = FieldType.Double)
    private BigDecimal stock;

    /**
     * 囤货商品虚拟库存
     */
    @ApiModelProperty(value = "虚拟库存")
    @Field(index = false, type = FieldType.Double)
    private BigDecimal virtualStock;

    /**
     * 是否参与囤货标记
     */
    @ApiModelProperty(value = "是否参与囤货标记")
    private ForcePileFlag pileFlag;

    /**
     * 商品市场价
     */
    @ApiModelProperty(value = "商品市场价")
    @Field(index = false, type = FieldType.Double)
    private BigDecimal marketPrice;

    /**
     * 拼团价
     */
    @ApiModelProperty(value = "拼团价")
    private BigDecimal grouponPrice;

    /**
     * 商品成本价
     */
    @ApiModelProperty(value = "商品成本价")
    @Field(index = false, type = FieldType.Double)
    private BigDecimal costPrice;

    /**
     * 大客户价
     */
    @ApiModelProperty(value = "大客户价")
    @Field(index = false, type = FieldType.Double)
    private BigDecimal vipPrice;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 上下架时间
     */
    @ApiModelProperty(value = "上下架时间")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime addedTime;

    /**
     * 删除标记
     */
    @ApiModelProperty(value = "删除标记")
    @Enumerated
    private DeleteFlag delFlag;

    /**
     * 上下架状态
     */
    @ApiModelProperty(value = "上下架状态", dataType = "com.wanmi.sbc.goods.bean.enums.AddedFlag")
    private Integer addedFlag;

    /**
     * 提货是否校验商品上下架状态,默认1
     * 0-否，1-是
     */
    @ApiModelProperty(value = "提货是否校验商品上下架状态,0-否，1-是,默认1")
    private Integer checkedAddedFlag = 1;

    /**
     * 询问底价标志，0-否，1-是
     */
    @ApiModelProperty(value = "询问底价标志，0-否，1-是")
    private Integer inquiryFlag;

    /**
     * 公司信息ID
     */
    @ApiModelProperty(value = "公司信息ID")
    private Long companyInfoId;

    /**
     * 店铺ID
     */
    @ApiModelProperty(value = "店铺ID")
    private Long storeId;

    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称")
    private String storeName;

    /**
     * 按客户单独定价
     */
    @ApiModelProperty(value = "按客户单独定价")
    @Field(index = false, type = FieldType.Integer)
    private Integer customFlag;

    /**
     * 是否叠加客户等级折扣
     */
    @ApiModelProperty(value = "是否叠加客户等级折扣")
    @Field(index = false, type = FieldType.Integer)
    private Integer levelDiscountFlag;

    /**
     * 审核状态
     */
    @ApiModelProperty(value = "审核状态")
    @Field(index = false, type = FieldType.Integer)
    private CheckStatus auditStatus;

    /**
     * 商家类型 0、平台自营 1、第三方商家
     */
    @ApiModelProperty(value = "商家类型")
    @Field(type = FieldType.Integer)
    private CompanyType companyType;

    /**
     * 是否独立设价 1:是 0:否
     */
    @ApiModelProperty(value = "是否独立设价")
    @Field(index = false, type = FieldType.Boolean)
    private Boolean aloneFlag;

    /**
     * 最新计算的会员价
     * 为空，以市场价为准
     */
    @ApiModelProperty(value = "最新计算的会员价")
    @Field(index = false, type = FieldType.Double)
    private BigDecimal salePrice;

    /**
     * 设价类型 0:客户,1:订货
     */
    @ApiModelProperty(value = "设价类型")
    @Field(index = false, type = FieldType.Integer)
    private Integer priceType;

    /**
     * 新增时，模拟多个规格ID
     * 查询详情返回响应，扁平化多个规格ID
     */
    @ApiModelProperty(value = "扁平化多个规格ID")
    private List<Long> mockSpecIds;

    /**
     * 新增时，模拟多个规格值 ID
     * 查询详情返回响应，扁平化多个规格值ID
     */
    @ApiModelProperty(value = "扁平化多个规格值ID")
    private List<Long> mockSpecDetailIds;

    /**
     * 商品分页，扁平化多个商品规格值ID
     */
    @ApiModelProperty(value = "商品分页")
    private List<Long> specDetailRelIds;

    /**
     * 购买量
     */
    @ApiModelProperty(value = "购买量")
    private Long buyCount = 0L;

    /**
     * 最新计算的起订量
     * 为空，则不限
     */
    @ApiModelProperty(value = "最新计算的起订量")
    private Long count;

    /**
     * 最新计算的限定量
     * 为空，则不限
     */
    @ApiModelProperty(value = "最新计算的限定量")
    private Long maxCount;

    /**
     * 一对多关系，多个订货区间价格编号
     */
    @ApiModelProperty(value = "多个订货区间价格编号")
    private List<Long> intervalPriceIds;

    /**
     * 规格名称规格值 颜色:红色;大小:16G
     */
    @ApiModelProperty(value = "规格名称规格值")
    private String specText;

    /**
     * 最小区间价
     */
    @ApiModelProperty(value = "最小区间价")
    private BigDecimal intervalMinPrice;

    /**
     * 最大区间价
     */
    @ApiModelProperty(value = "最大区间价")
    private BigDecimal intervalMaxPrice;

    /**
     * 有效状态 0:无效,1:有效
     */
    @ApiModelProperty(value = "有效状态")
    private Integer validFlag;

    /**
     * 商品分类ID
     */
    @ApiModelProperty(value = "商品分类ID")
    private Long cateId;

    /**
     * 品牌ID
     */
    @ApiModelProperty(value = "品牌ID")
    private Long brandId;

    /**
     * 多对多关系，多个店铺分类编号
     */
    @ApiModelProperty(value = "多对多关系，多个店铺分类编号")
    private List<Long> storeCateIds;

    /**
     * 预估佣金
     */
    @ApiModelProperty(value = "预估佣金")
    @Field(index = false, type = FieldType.Double)
    private BigDecimal distributionCommission;

    /**
     * 佣金比例
     */
    @ApiModelProperty(value = "佣金比例")
    @Field(index = false, type = FieldType.Double)
    private BigDecimal commissionRate;

    /**
     * 分销销量
     */
    @ApiModelProperty(value = "分销销量")
    @Field(index = false, type = FieldType.Integer)
    private Integer distributionSalesCount;

    /**
     * 分销商品审核状态 0:普通商品 1:待审核 2:已审核通过 3:审核不通过 4:禁止分销
     */
    @ApiModelProperty(value = "分销商品审核状态")
    @Field(index = false, type = FieldType.Integer)
    @Enumerated
    private DistributionGoodsAudit distributionGoodsAudit;

    /**
     * 分销商品审核不通过或禁止分销原因
     */
    @ApiModelProperty(value = "分销商品审核不通过或禁止分销原因")
    @Field(index = false, type = FieldType.Keyword)
    private String distributionGoodsAuditReason;

    /**
     * 前端是否选中
     */
    @ApiModelProperty(value = "前端是否选中")
    private Boolean checked = false;

    /**
     * 商品状态 0：正常 1：缺货 2：失效
     */
    @ApiModelProperty(value = "商品状态")
    private GoodsStatus goodsStatus = GoodsStatus.OK;

    /**
     * 计算单位
     */
    @ApiModelProperty(value = "计算单位")
    private String goodsUnit;

    /**
     * 促销标签
     */
    @ApiModelProperty(value = "促销标签")
    private List<MarketingLabelVO> marketingLabels = new ArrayList<>();

    /**
     * 拼团标签
     */
    @ApiModelProperty(value = "促销标签")
    private GrouponLabelVO grouponLabel;

    /**
     * 优惠券标签
     */
    @ApiModelProperty(value = "优惠券标签")
    private List<CouponLabelVO> couponLabels = new ArrayList<>();

    /**
     * 商品体积 单位：m3
     */
    @ApiModelProperty(value = "商品体积")
    private BigDecimal goodsCubage;

    /**
     * 商品重量
     */
    @ApiModelProperty(value = "商品重量")
    private BigDecimal goodsWeight;

    /**
     * 运费模板ID
     */
    @ApiModelProperty(value = "运费模板ID")
    private Long freightTempId;

    /**
     * 销售类型 0:批发, 1:零售
     */
    @ApiModelProperty(value = "销售类型", dataType = "com.wanmi.sbc.goods.bean.enums.SaleType")
    private Integer saleType;

    /**
     * 是否允许独立设价 0:不允许, 1:允许
     */
    @ApiModelProperty(value = "是否允许独立设价", dataType = "com.wanmi.sbc.common.enums.DefaultFlag")
    private Integer allowPriceSet;

    /**
     * 商品详情小程序码
     */
    @ApiModelProperty(value = "商品详情小程序码")
    private String smallProgramCode;

    /**
     * 是否已关联分销员
     */
    @ApiModelProperty(value = "是否已关联分销员，0：否，1：是")
    private Integer joinDistributior;

    /**
     * 分销员商品表ID
     */
    @ApiModelProperty(value = "分销员商品表ID")
    private String distributionGoodsInfoId;

    /**
     * 商品评论数
     */
    @ApiModelProperty(value = "商品评论数")
    @Field(index = false, type = FieldType.Integer)
    private Long goodsEvaluateNum;

    /**
     * 商品收藏量
     */
    @ApiModelProperty(value = "商品收藏量")
    @Field(index = false, type = FieldType.Long)
    private Long goodsCollectNum;

    /**
     * 商品销量
     */
    @ApiModelProperty(value = "商品销量")
    @Field(index = false, type = FieldType.Long)
    private Long goodsSalesNum;

    /**
     * 商品好评数量
     */
    @ApiModelProperty(value = "商品好评数量")
    @Field(index = false, type = FieldType.Long)
    private Long goodsFavorableCommentNum;

    /**
     * 商品好评率
     */
    @Field(index = false, type = FieldType.Long)
    private Long goodsFeedbackRate;

    /**
     * 企业购商品的价格
     */
    @ApiModelProperty(value = "企业购商品的销售价格")
    @Field(index = false, type = FieldType.Double)
    private BigDecimal enterPrisePrice;

    /**
     * 企业购商品审核的状态
     */
    @ApiModelProperty(value = "企业购商品的审核状态", dataType = "com.wanmi.sbc.customer.bean.enums.EnterpriseAuditState")
    @Field(index = false, type = FieldType.Integer)
    private Integer enterPriseAuditStatus = EnterpriseAuditState.INIT.toValue();

    /**
     * 企业购商品审核未通过的原因
     */
    @ApiModelProperty(value = "企业购商品审核未通过的原因")
    @Field(index = false, type = FieldType.Text)
    private String enterPriseGoodsAuditReason;

    /**
     * 排序的价格
     */
    @ApiModelProperty(value = "排序的价格")
    @Field(index = false, type = FieldType.Double)
    private BigDecimal esSortPrice;

    /**
     * 库存列表
     */
    @ApiModelProperty(value = "库存列表")
    private List<GoodsWareStockVO> goodsWareStockVOS = new ArrayList<>();

    public void setEsSortPrice() {
        this.esSortPrice = enterPriseAuditStatus == EnterpriseAuditState.CHECKED.toValue() ? enterPrisePrice : marketPrice;
    }

    /**
     * 是否是特价商品
     */
    @ApiModelProperty(value = "是否是特价商品")
    @Field(index = false, type = FieldType.Integer)
    private Integer goodsInfoType;

    /**
     * 特价
     */
    @ApiModelProperty(value = "特价")
    @Field(index = false, type = FieldType.Double)
    private BigDecimal specialPrice;


    /**
     * 商品增加步长
     */
    @Field(index = false, type = FieldType.Double)
    @ApiModelProperty(value = "商品增加步长")
    private BigDecimal addStep;

    /**
     * 竞价排名的关键字排序值
     */
    @ApiModelProperty(value = "竞价排名的关键字排序值")
    @Field(index = false, type = FieldType.Integer)
    private Integer sortNumKey;

    /**
     * 竞价排名的分类排序值
     */
    @ApiModelProperty(value = "竞价排名的分类排序值")
    @Field(index = false, type = FieldType.Integer)
    private Integer sortNumCate;


    /**
     * 关键字
     */
    @ApiModelProperty(value = "关键字")
    @Field(searchAnalyzer = EsConstants.DEF_ANALYZER, analyzer = EsConstants.DEF_ANALYZER, type = FieldType.Text)
    private String keyWords;

    /**
     * 商品排序序号
     */
    @ApiModelProperty(value = "商品排序序号")
    private Integer goodsSeqNum;

    /**
     * 散称或定量
     */
    @ApiModelProperty(value = "散称或定量")
    private Integer isScatteredQuantitative;

    /**
     * 商品保质期
     */
    @ApiModelProperty(value = "商品保质期")
    @Field(type = FieldType.Integer)
    private Integer shelflife;

    /**
     * 商品副标题
     */
    @ApiModelProperty(value = "商品fu标题")
    private String goodsSubtitle;


    /**
     * 商品副标题new
     */
    @ApiModelProperty(value = "商品副标题new")
    private String goodsSubtitleNew;

    /**
     * 直播间ID
     */
    private Long roomId;

    /**
     * 直播开始时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime liveStartTime;

    /**
     * 直播结束时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime liveEndTime;

    /**
     * 该商品允许销售的城市
     */
    @ApiModelProperty(value = "该商品允许销售的城市")
    private String allowedPurchaseArea;

    @ApiModelProperty(value = "指定销售区域名称，以“，”分隔")
    private String allowedPurchaseAreaName;

    /**
     * 单笔订单指定限购区域id，用“，”隔开
     */
    @ApiModelProperty(value = "单笔订单指定限购区域id，以“，”分隔")
    private String singleOrderAssignArea;

    /**
     * 单笔订单指定限购区域名称，用“，”隔开
     */
    @ApiModelProperty(value = "单笔订单指定限购区域名称，以“，”分隔")
    private String singleOrderAssignAreaName;

    /**
     * 单笔订单限购数量
     */
    @ApiModelProperty(value = "单笔订单限购数量")
    private Long singleOrderPurchaseNum;


    @ApiModelProperty(value = "到手价")
    @Field(index = false, type = FieldType.Double)
    private BigDecimal theirPrice;

    @ApiModelProperty(value = "零售价")
    @Field(index = false, type = FieldType.Double)
    private BigDecimal retailPrice;

    /**
     * 营销编号
     */
    @ApiModelProperty(value = "营销编号")
    private Long marketingId;

    /**
     * 限购数量
     */
    @ApiModelProperty(value = "限购数量")
    private Long purchaseNum;

    /**
     * 是否隐藏 0否 1是
     */
    @ApiModelProperty(value = "是否隐藏 0否 1是")
    private Integer hiddenFlag;

    /**
     * 是否套装商品 0否 1是
     */
    @ApiModelProperty(value = "是否套装商品 0否 1是")
    private DefaultFlag isSuitGoods;

    /**
     * 选中的本品skuId 当isSuitGoods为“1-是”时为必填项
     */
    @ApiModelProperty(value = "选中的本品skuId 当isSuitGoods为“1-是”时为必填项")
    private String choseProductSkuId;

    /**
     * 是否限制起购数量
     */
    @ApiModelProperty(value = "是否限制起购数量")
    private DefaultFlag isStartBuyNum;

    /**
     * 起购数量（默认为 0 即：不限制）
     */
    @ApiModelProperty(value = "起购数量（默认为 0 即：不限制）")
    private Integer startBuyNum;

    /**
     * 推荐商品排序 默认0
     */
    @ApiModelProperty(value = "推荐商品排序 默认0")
    @Field(index = false, type = FieldType.Long)
    private Integer recommendSort;

    /**
     * 是否惊爆价
     */
    @ApiModelProperty(value = "是否惊爆价")
    private DefaultFlag isSurprisePrice;


    /**
     * 0 批发 1散批
     */
    private Long goodsType;

    @ApiModelProperty(value = "'是否有直播间")
    private Integer isHaveLive;

    @ApiModelProperty(value = "'直播间id")
    private Integer liveRoomId;

    @ApiModelProperty(value = "直播liveId")
    private Integer liveId;
    /**
     * 商品主sku
     */
    @ApiModelProperty(value = "商品主sku")
    private Integer hostSku;
    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String goodsInfoUnit;
    /**
     * 重量
     */
    @ApiModelProperty(value = "重量")
    private BigDecimal goodsInfoWeight;
    /**
     * 体积
     */
    @ApiModelProperty(value = "体积")
    private BigDecimal goodsInfoCubage;

    /**
     * 是否可预售
     */
    @ApiModelProperty(value = "是否可预售：0否1是")
    private Integer isPresell = 0;

    /**
     * 预售虚拟库存
     */
    @ApiModelProperty(value = "预售虚拟库存")
    private Long presellStock;
}

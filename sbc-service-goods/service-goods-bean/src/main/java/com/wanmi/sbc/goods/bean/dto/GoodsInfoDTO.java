package com.wanmi.sbc.goods.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.EnterpriseAuditState;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.GoodsLabelVO;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockVO;
import com.wanmi.sbc.goods.bean.vo.GrouponLabelVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * GoodsInfoDTO
 * 商品信息传输对象
 *
 * @author lipeng
 * @dateTime 2018/11/6 下午2:29
 */
@ApiModel
@Data
public class GoodsInfoDTO implements Serializable {

    private static final long serialVersionUID = 9040193270990319318L;


    /**
     * MarketingId
     */
    @ApiModelProperty(value = "营销id")
    private Long marketing;


    /**
     * 拆箱主键id goodsinfovo需要加拆箱主键 在查询商品快照接口需要用到
     */
    @ApiModelProperty(value = "拆箱主键id")
    private Long devanningId;

    /**
     * 除数标识
     */
    @ApiModelProperty(value = "除数标识")
    private BigDecimal divisorFlag = BigDecimal.ONE;
    /**
     * 商品SKU编号
     */
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
    private String goodsInfoName;

    /**
     * 商品SKU编码
     */
    @ApiModelProperty(value = "商品SKU编码")
    private String goodsInfoNo;

    /**
     * erpSKU编码
     */
    @ApiModelProperty(value = "erpSKU编码")
    private String erpGoodsInfoNo;

    /**
     * 商品图片
     */
    @ApiModelProperty(value = "商品图片")
    private String goodsInfoImg;

    /**
     * 商品条形码
     */
    @ApiModelProperty(value = "商品条形码")
    private String goodsInfoBarcode;

    /**
     * 是否是散称或定量
     */
    @ApiModelProperty(value = "散称或定量")
    private Integer isScatteredQuantitative;
    /**
     * 商品二维码
     */
    @ApiModelProperty(value = "商品二维码")
    private String goodsInfoQrcode;

    /**
     * 商品库存
     */
    @ApiModelProperty(value = "商品库存")
    private BigDecimal stock;

    /**
     * 锁定库存
     */
    private BigDecimal lockStock;

    /**
     * 囤货虚拟库存
     */
    @ApiModelProperty(value = "虚拟库存")
    private BigDecimal virtualStock;

    /**
     * 商品市场价
     */
    @ApiModelProperty(value = "商品市场价")
    private BigDecimal marketPrice;

    /**
     * 商品供货价
     */
    @ApiModelProperty(value = "商品供货价")
    private BigDecimal supplyPrice;

    /**
     * 商品建议零售价
     */
    @ApiModelProperty(value = "商品建议零售价")
    private BigDecimal retailPrice;

    /**
     * 商品成本价
     */
    @ApiModelProperty(value = "商品成本价")
    private BigDecimal costPrice;

    /**
     * 大客户价
     */
    @ApiModelProperty(value = "大客户价")
    private BigDecimal vipPrice;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 上下架时间
     */
    @ApiModelProperty(value = "上下架时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime addedTime;

    /**
     * 删除标记
     */
    @ApiModelProperty(value = "删除标记", notes = "0: 否, 1: 是")
    private DeleteFlag delFlag;

    /**
     * 上下架状态
     */
    @ApiModelProperty(value = "上下架状态", dataType = "com.wanmi.sbc.goods.bean.enums.AddedFlag")
    private Integer addedFlag;

    /**
     * 提货是否校验商品上下架状态
     * 0-否，1或null-是
     */
    @ApiModelProperty(value = "提货是否校验商品上下架状态，0-否，1或null-是")
    private Integer checkedAddedFlag;

    /**
     * 询问底价标志 0-否，1-是
     * add by jiangxin 20210831
     */
    @ApiModelProperty(value = "询问底价标志", notes = "0-否，1-是")
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
     * 按客户单独定价
     */
    @ApiModelProperty(value = "按客户单独定价", dataType = "com.wanmi.sbc.common.enums.DefaultFlag")
    private Integer customFlag;

    /**
     * 是否叠加客户等级折扣
     */
    @ApiModelProperty(value = "是否叠加客户等级折扣", dataType = "com.wanmi.sbc.common.enums.DefaultFlag")
    private Integer levelDiscountFlag;

    /**
     * 审核状态
     */
    @ApiModelProperty(value = "审核状态")
    private CheckStatus auditStatus;

    /**
     * 商家类型 0、平台自营 1、第三方商家
     */
    @ApiModelProperty(value = "商家类型")
    private CompanyType companyType;

    /**
     * 是否独立设价 1:是 0:否
     */
    @ApiModelProperty(value = "是否独立设价")
    private Boolean aloneFlag;

    /**
     * 最新计算的会员价
     * 为空，以市场价为准
     */
    @ApiModelProperty(value = "最新计算的会员价", notes = "为空，以市场价为准")
    private BigDecimal salePrice;

    /**
     * 设价类型 0:客户,1:订货
     */
    @ApiModelProperty(value = "设价类型", dataType = "com.wanmi.sbc.goods.bean.enums.PriceType")
    private Integer priceType;

    /**
     * 新增时，模拟多个规格ID
     * 查询详情返回响应，扁平化多个规格ID
     */
    @ApiModelProperty(value = "新增时，模拟多个规格ID", notes = "查询详情返回响应，扁平化多个规格ID")
    private List<Long> mockSpecIds;

    /**
     * 新增时，模拟多个规格值 ID
     * 查询详情返回响应，扁平化多个规格值ID
     */
    @ApiModelProperty(value = "新增时，模拟多个规格值 ID", notes = "查询详情返回响应，扁平化多个规格值ID")
    private List<Long> mockSpecDetailIds;

    /**
     * 商品分页，扁平化多个商品规格值ID
     */
    @ApiModelProperty(value = "商品分页，扁平化多个商品规格值ID")
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
    @ApiModelProperty(value = "最新计算的起订量", notes = "为空，则不限")
    private Long count;

    /**
     * 最新计算的限定量
     * 为空，则不限
     */
    @ApiModelProperty(value = "最新计算的限定量", notes = "为空，则不限")
    private Long maxCount;

    /**
     * 一对多关系，多个订货区间价格编号
     */
    @ApiModelProperty(value = "一对多关系，多个订货区间价格编号")
    private List<Long> intervalPriceIds;

    /**
     * 规格名称规格值 颜色:红色;大小:16G
     */
    @ApiModelProperty(value = "规格名称规格值", example = "颜色:红色;大小:16G")
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
    @ApiModelProperty(value = "有效状态", dataType = "com.wanmi.sbc.common.enums.DefaultFlag")
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
     * 分销佣金
     */
    @ApiModelProperty(value = "分销佣金")
    private BigDecimal distributionCommission;

    /**
     * 佣金比例
     */
    @ApiModelProperty(value = "佣金比例")
    private BigDecimal commissionRate;

    /**
     * 分销销量
     */
    @ApiModelProperty(value = "分销销量")
    private Integer distributionSalesCount;

    /**
     * 分销商品审核状态 0:普通商品 1:待审核 2:已审核通过 3:审核不通过 4:禁止分销
     */
    @ApiModelProperty(value = "分销商品审核状态")
    private DistributionGoodsAudit distributionGoodsAudit;

    /**
     * 分销商品审核不通过或禁止分销原因
     */
    @ApiModelProperty(value = "分销商品审核不通过或禁止分销原因")
    private String distributionGoodsAuditReason;

    /**
     * 前端是否选中
     */
    @ApiModelProperty(value = "前端是否选中", notes = "true: 是, false: 否")
    private Boolean checked = false;

    /**
     * 商品状态 0：正常 1：缺货 2：失效
     */
    @ApiModelProperty(value = "商品状态", notes = "0：正常 1：缺货 2：失效")
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
    private List<MarketingLabelDTO> marketingLabels = new ArrayList<>();

    /**
     * 拼团标签
     */
    @ApiModelProperty(value = "促销标签")
    private GrouponLabelVO grouponLabel;

    /**
     * 优惠券标签
     */
    @ApiModelProperty(value = "优惠券标签")
    private List<CouponLabelDTO> couponLabels = new ArrayList<>();

    /**
     * 商品体积 单位：m3
     */
    @ApiModelProperty(value = "商品体积 单位：m3")
    private BigDecimal goodsCubage;

    // 体积信息
    private String volumeInfo;

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
    private String smallProgramCode;

    /**
     * 商品评论数
     */
    @ApiModelProperty(value = "商品评论数")
    private Long goodsEvaluateNum;

    /**
     * 商品收藏量
     */
    @ApiModelProperty(value = "商品收藏量")
    private Long goodsCollectNum;

    /**
     * 商品销量
     */
    @ApiModelProperty(value = "商品销量")
    private Long goodsSalesNum;

    /**
     * 商品好评数
     */
    @ApiModelProperty(value = "商品好评数")
    private Long goodsFavorableCommentNum;

    /**
     * 企业购商品的价格
     */
    @ApiModelProperty(value = "企业购商品的销售价格")
    private BigDecimal enterPrisePrice;

    /**
     * 企业购商品的审核状态
     */
    @ApiModelProperty(value = "企业购商品的审核状态", dataType = "com.wanmi.sbc.customer.bean.enums.EnterpriseAuditState")
    private EnterpriseAuditState enterPriseAuditState;

    /**
     * 企业购商品审核被驳回的原因
     */
    @ApiModelProperty(value = "企业购商品审核被驳回的原因")
    private String enterPriseGoodsAuditReason;

    /**
     * 商品来源，0供应商，1商家
     */
    @ApiModelProperty(value = "商品来源，0供应商，1商家")
    private Integer goodsSource;

    /**
     * 喜吖吖企业会员审核状态
     */
    @ApiModelProperty(value = "喜吖吖企业会员审核状态")
    private Integer enterpriseStatusXyy;

    /**
     * 批次号
     */
    @ApiModelProperty(value = "批次号")
    private String goodsInfoBatchNo;

    /**
     * 商品状态（0：普通商品，1：特价商品）
     */
    @ApiModelProperty("商品状态（0：普通商品，1：特价商品）")
    private Integer goodsInfoType;

    /**
     * sku关联仓库库存
     */
    @ApiModelProperty(value = "sku关联仓库库存")
    private List<GoodsWareStockVO> goodsWareStocks;

    /**
     * 整箱规格
     */
    @ApiModelProperty(value = "整箱规格")
    private Integer fullStep;

    /**
     * 商品增加的步长
     */
    @ApiModelProperty(value = "商品增加的步长")
    private BigDecimal addStep;

    /**
     * 特价
     */
    @ApiModelProperty(value = "特价")
    private BigDecimal specialPrice;

    /**
     * 关键词、分类
     */
    @ApiModelProperty(value = "关键词、分类")
    private String keyWords;

    /**
     * 竞价排名的关键字排序值
     */
    @ApiModelProperty(value = "竞价排名的关键字排序值")
    private Integer sortNumKey;

    /**
     * 竞价排名的分类排序值
     */
    @ApiModelProperty(value = "竞价排名的分类排序值")
    private Integer sortNumCate;

    /**
     * 保质期
     */
    @ApiModelProperty(value = "保质期")
    private Long shelflife;

    /**
     * 购物车商品是否选中
     */
    @ApiModelProperty(value = "购物车商品是否选中")
    private DefaultFlag isCheck = DefaultFlag.YES;

    private List<GoodsLabelVO> goodsLabels;

    @ApiModelProperty(value = "产地")
    private String origin;

    @ApiModelProperty(value = "产地code")
    private Long originCode;

    @ApiModelProperty(value = "指定销售区域id，以“，”分隔")
    private String allowedPurchaseArea;

    @ApiModelProperty(value = "指定销售区域名称，以“，”分隔")
    private String allowedPurchaseAreaName;

    /**
     * 单笔订单指定限购区域id，用“，”隔开
     */
    @ApiModelProperty(value = "单用户限购区域id，以“，”分隔")
    private String singleOrderAssignArea;

    /**
     * 单笔订单指定限购区域名称，用“，”隔开
     */
    @ApiModelProperty(value = "单用户限购区域名称，以“，”分隔")
    private String singleOrderAssignAreaName;

    /**
     * 单笔订单限购数量
     */
    @ApiModelProperty(value = "单用户限购数量")
    private Long singleOrderPurchaseNum;

    /**
     * 关联活动限购营销id
     */
    @ApiModelProperty(value = "关联活动限购营销id")
    private Long marketingId;

    /**
     * 限购数量
     */
    @ApiModelProperty(value = "限购数量")
    private Long purchaseNum;

    /**
     * 囤货订单商品优惠后单价，囤货订单专用字段
     */
    private BigDecimal goodsSplitPrice;

    /**
     * 商品是否可囤货
     */
    @ApiModelProperty(value = "商品是否可囤货")
    private DefaultFlag isCanPile;

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
     * 是否零售商品 0否 1是
     */
    private Integer isSupermarketGoods = 0;

    /**
     * 商品副标题
     */
    @ApiModelProperty(value = "商品副标题")
    private String goodsSubtitle;

    /**
     * 是否限制起购数量
     */
    @ApiModelProperty(value = "是否限制起购数量 0否 1是")
    private DefaultFlag isStartBuyNum;
    /**
     * 起购数量（默认为 0 即：不限制）
     */
    @ApiModelProperty(value = "起购数量（默认为 0 即：不限制）")
    private Integer startBuyNum;

    /**
     * 是否拆箱
     */
    @ApiModelProperty(value = "0->否 1->是")
    private DefaultFlag isDevanning;

    /**
     * 拆箱规格
     */
    @ApiModelProperty(value = "拆箱规格 0->半箱 1->1/4")
    private String devanningType;

    /**
     * 推荐商品排序 默认0
     */
    @ApiModelProperty(value = "推荐商品排序 默认0")
    private Integer recommendSort;

    /**
     * 是否惊爆价
     */
    @ApiModelProperty(value = "0->否 1->是")
    private DefaultFlag isSurprisePrice;
    /**
     * sku属性关联关系
     */
    @Transient
    private List<GoodsAttributeKeyDTO> goodsAttributeKeys;
    /**
     * 主spu0->否 1->是
     */
    @ApiModelProperty(value = "0->否 1->是")
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
     * 最小单位
     */
    @ApiModelProperty(value = "最小单位")
    private String devanningUnit;

    /**
     * 预售虚拟库存
     */
    @ApiModelProperty(value = "预售虚拟库存")
    private Long presellStock;

    /**
     * 日期单位
     */
    @ApiModelProperty(value = "日期单位")
    private String dateUnit;

}

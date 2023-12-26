package com.wanmi.sbc.order.response;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
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
import com.wanmi.sbc.goods.bean.vo.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ImportGoodsInfo {

    @ExcelProperty(value = "ERP编码", index = 0)
    private String erpGoodsInfoNo;

    @ExcelProperty(value = "商品名称", index = 1)
    private String goodsInfoName;

    @ExcelProperty(value = "规格", index = 2)
    private String goodsSpecs;

    @ExcelProperty(value = "数量", index = 3)
    private Integer num;

    @ExcelIgnore
    //补充会员价
    private BigDecimal price;

    @ExcelIgnore
    //校验报错信息
    private String excelError;

    /**
     * 商品编号
     */
    @ExcelIgnore
    private String goodsId;

    /**
     * 商品编号
     */
    @ExcelIgnore
    private String goodsInfoId;

    /**
     * 商品SKU编码
     */
    @ExcelIgnore
    private String goodsInfoNo;

    /**
     * 商品图片
     */
    @ExcelIgnore
    private String goodsInfoImg;

    /**
     * 商品条形码
     */
    @ExcelIgnore
    private String goodsInfoBarcode;

    /**
     * 商品二维码
     */
    @ExcelIgnore
    private String goodsInfoQrcode;

    /**
     * 商品库存
     */
    @ExcelIgnore
    private Long stock;

    /**
     * 商品市场价
     */
    @ExcelIgnore
    private BigDecimal marketPrice;

    /**
     * 供货价
     */
    @ExcelIgnore
    @CanEmpty
    private BigDecimal supplyPrice;

    /**
     * 建议零售价
     */
    @ExcelIgnore
    @CanEmpty
    private BigDecimal retailPrice;

    /**
     * 拼团价
     */
    @ExcelIgnore
    private BigDecimal grouponPrice;

    /**
     * 商品成本价
     */
    @ExcelIgnore
    private BigDecimal costPrice;

    /**
     * 大客户价
     */
    @ExcelIgnore
    private BigDecimal vipPrice;

    /**
     * 创建时间
     */
    @ExcelIgnore
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ExcelIgnore
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 上下架时间
     */
    @ExcelIgnore
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime addedTime;

    /**
     * 删除标记
     */
    @ExcelIgnore
    private DeleteFlag delFlag;

    /**
     * 上下架状态
     */
    @ExcelIgnore
    private Integer addedFlag;

    /**
     * 公司信息ID
     */
    @ExcelIgnore
    private Long companyInfoId;

    /**
     * 店铺ID
     */
    @ExcelIgnore
    private Long storeId;

    /**
     * 店铺名称
     */
    @ExcelIgnore
    private String storeName;

    /**
     * 按客户单独定价
     */
    @ExcelIgnore
    private Integer customFlag;

    /**
     * 是否叠加客户等级折扣
     */
    @ExcelIgnore
    private Integer levelDiscountFlag;

    /**
     * 审核状态
     */
    @ExcelIgnore
    private CheckStatus auditStatus;

    /**
     * 商家类型 0、平台自营 1、第三方商家
     */
    @ExcelIgnore
    private CompanyType companyType;

    /**
     * 是否独立设价 1:是 0:否
     */
    @ExcelIgnore
    private Boolean aloneFlag;

    /**
     * 最新计算的会员价
     * 为空，以市场价为准
     */
    @ExcelIgnore
    private BigDecimal salePrice;

    /**
     * 设价类型 0:客户,1:订货
     */
    @ExcelIgnore
    private Integer priceType;

    /**
     * 新增时，模拟多个规格ID
     * 查询详情返回响应，扁平化多个规格ID
     */
    @ExcelIgnore
    private List<Long> mockSpecIds;

    /**
     * 新增时，模拟多个规格值 ID
     * 查询详情返回响应，扁平化多个规格值ID
     */
    @ExcelIgnore
    private  List<Long> mockSpecDetailIds;

    /**
     * 商品分页，扁平化多个商品规格值ID
     */
    @ExcelIgnore
    private List<Long> specDetailRelIds;

    /**
     * 购买量
     */
    @ExcelIgnore
    private Long buyCount = 0L;

    /**
     * 最新计算的起订量
     * 为空，则不限
     */
    @ExcelIgnore
    private Long count;

    /**
     * 最新计算的限定量
     * 为空，则不限
     */
    @ExcelIgnore
    private Long maxCount;

    /**
     * 一对多关系，多个订货区间价格编号
     */
    @ExcelIgnore
    private List<Long> intervalPriceIds;

    /**
     * 规格名称规格值 颜色:红色;大小:16G
     */
    @ExcelIgnore
    private String specText;

    /**
     * 最小区间价
     */
    @ExcelIgnore
    private BigDecimal intervalMinPrice;

    /**
     * 最大区间价
     */
    @ExcelIgnore
    private BigDecimal intervalMaxPrice;

    /**
     * 有效状态 0:无效,1:有效
     */
    @ExcelIgnore
    private Integer validFlag;

    /**
     * 商品分类ID
     */
    @ExcelIgnore
    private Long cateId;

    /**
     * 品牌ID
     */
    @ExcelIgnore
    private Long brandId;

    /**
     * 多对多关系，多个店铺分类编号
     */
    @ExcelIgnore
    private List<Long> storeCateIds;

    /**
     * 预估佣金
     */
    @ExcelIgnore
    private BigDecimal distributionCommission;

    /**
     * 佣金比例
     */
    @ExcelIgnore
    private BigDecimal commissionRate;

    /**
     * 分销销量
     */
    @ExcelIgnore
    private Integer distributionSalesCount;

    /**
     * 分销商品审核状态 0:普通商品 1:待审核 2:已审核通过 3:审核不通过 4:禁止分销
     */
    @ExcelIgnore
    private DistributionGoodsAudit distributionGoodsAudit;

    /**
     * 分销商品审核不通过或禁止分销原因
     */
    @ExcelIgnore
    private String distributionGoodsAuditReason;

    /**
     * 前端是否选中
     */
    @ExcelIgnore
    private Boolean checked = false;

    /**
     * 商品状态 0：正常 1：缺货 2：失效
     */
    @ExcelIgnore
    private GoodsStatus goodsStatus = GoodsStatus.OK;

    /**
     * 计算单位
     */
    @ExcelIgnore
    private String goodsUnit;

    /**
     * 促销标签
     */
    @ExcelIgnore
    private List<MarketingLabelVO> marketingLabels = new ArrayList<>();

    /**
     * 拼团标签
     */
    @ExcelIgnore
    private GrouponLabelVO grouponLabel;
    /**
     * 优惠券标签
     */
    @ExcelIgnore
    private List<CouponLabelVO> couponLabels = new ArrayList<>();

    /**
     * 商品体积 单位：m3
     */
    @ExcelIgnore
    private BigDecimal goodsCubage;

    /**
     * 商品重量
     */
    @ExcelIgnore
    private BigDecimal goodsWeight;

    /**
     * 运费模板ID
     */
    @ExcelIgnore
    private Long freightTempId;

    /**
     * 销售类型 0:批发, 1:零售
     */
    @ExcelIgnore
    private Integer saleType;

    /**
     * 是否允许独立设价 0:不允许, 1:允许
     */
    @ExcelIgnore
    private Integer allowPriceSet;

    /**
     * 商品详情小程序码
     */
    @ExcelIgnore
    private String  smallProgramCode;

    /**
     * 是否已关联分销员
     */
    @ExcelIgnore
    private Integer joinDistributior;

    /**
     * 商品评论数
     */
    @ExcelIgnore
    private Long goodsEvaluateNum;

    /**
     * 商品收藏量
     */
    @ExcelIgnore
    private Long goodsCollectNum;

    /**
     * 商品销量
     */
    @ExcelIgnore
    private Long goodsSalesNum;

    /**
     * 商品好评数
     */
    @ExcelIgnore
    private Long goodsFavorableCommentNum;

    /**
     * 企业购商品的价格
     */
    @ExcelIgnore
    private BigDecimal enterPrisePrice;

    /**
     * 企业购商品的审核状态
     */
    @ExcelIgnore
    private EnterpriseAuditState enterPriseAuditState;

    /**
     * 企业购商品审核被驳回的原因
     */
    @ExcelIgnore
    private String enterPriseGoodsAuditReason;

    @ExcelIgnore
    private GoodsVO goods;

    /**
     * 所属供应商商品skuId
     */
    @ExcelIgnore
    private String providerGoodsInfoId;

    /**
     * 供应商Id
     */
    @ExcelIgnore
    private Long providerId;

    /**
     * 商品来源，0供应商，1商家
     */
    @ExcelIgnore
    private Integer goodsSource;

    /**
     * 喜吖吖企业会员审核状态
     */
    @ExcelIgnore
    private Integer enterpriseStatusXyy;

    /**
     * 批次号
     */
    @ExcelIgnore
    private String goodsInfoBatchNo;

    /**
     * 特价
     */
    @ExcelIgnore
    private BigDecimal specialPrice;

    /**
     * sku关联仓库库存
     */
    @ExcelIgnore
    private List<GoodsWareStockVO> goodsWareStocks;

    /**
     * 商品状态（0：普通商品，1：特价商品）
     */
    @ExcelIgnore
    private  Integer goodsInfoType;

    /**
     * 商品增加的步长
     */
    @ExcelIgnore
    private BigDecimal addStep;

    /**
     * 关键词、分类
     */
    @ExcelIgnore
    private String keyWords;

    /**
     * 竞价排名的关键字排序值
     */
    @ExcelIgnore
    private Integer sortNumKey;

    /**
     * 竞价排名的分类排序值
     */
    @ExcelIgnore
    private Integer sortNumCate;

    /**
     * 保质期
     */
    @ExcelIgnore
    private Long shelflife;

    /**
     * 商品副标题
     */
    @ExcelIgnore
    private String goodsSubtitle;

    /**
     * 购物车商品是否选中
     */
    @ExcelIgnore
    private DefaultFlag isCheck = DefaultFlag.YES;

    /**
     * 直播间ID
     */
    @ExcelIgnore
    private Long roomId;

    /**
     * 直播开始时间
     */
    @ExcelIgnore
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime liveStartTime;

    /**
     * 直播结束时间
     */
    @ExcelIgnore
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime liveEndTime;

    /**
     * 营销 商品活动状态展示
     */
    @ExcelIgnore
    private BoolFlag terminationFlag =BoolFlag.NO;

    /**
     *  商品标签
     */
    @ExcelIgnore
    private List<GoodsLabelVO> goodsLabels;
}

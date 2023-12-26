package com.wanmi.sbc.goods.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.annotation.CanEmpty;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品实体类
 * Created by dyt on 2017/4/11.
 */
@ApiModel
@Data
public class GoodsVO  implements Serializable {

    private static final long serialVersionUID = 2757888812286445293L;

    /**
     * 商品编号，采用UUID
     */
    @ApiModelProperty(value = "商品编号，采用UUID")
    private String goodsId;

    /**
     * 分类编号
     */
    @ApiModelProperty(value = "分类编号")
    private Long cateId;

    /**
     * 品牌编号
     */
    @ApiModelProperty(value = "品牌编号")
    @CanEmpty
    private Long brandId;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    /**
     * 商品副标题
     */
    @ApiModelProperty(value = "商品副标题")
    private String goodsSubtitle;

    /**
     * 商品副标题_新
     */
    @ApiModelProperty(value = "商品副标题_新")
    private String goodsSubtitleNew;

    /**
     * SPU编码
     */
    @ApiModelProperty(value = "SPU编码")
    private String goodsNo;

    /**
     * 计量单位
     */
    @ApiModelProperty(value = "计量单位")
    @CanEmpty
    private String goodsUnit;

    /**
     * 商品主图
     */
    @ApiModelProperty(value = "商品主图")
    @CanEmpty
    private String goodsImg;

    /**
     * 商品重量
     */
    @ApiModelProperty(value = "商品重量")
    private BigDecimal goodsWeight;

    /**
     * 市场价
     */
    @ApiModelProperty(value = "市场价")
    @CanEmpty
    private BigDecimal marketPrice;
    /**
     * 大客户价
     */
    @ApiModelProperty(value = "大客户价")
    @CanEmpty
    private BigDecimal vipPrice;

    /**
     * 供货价
     */
    @ApiModelProperty(value = "供货价")
    @CanEmpty
    private BigDecimal supplyPrice;

    /**
     * 建议零售价
     */
    @ApiModelProperty(value = "建议零售价")
    @CanEmpty
    private BigDecimal recommendedRetailPrice;

    /**
     * 商品类型，0:实体商品，1：虚拟商品
     */
    @ApiModelProperty(value = "商品类型")
    @CanEmpty
    private Integer goodsType;


    /**
     * 成本价
     */
    @ApiModelProperty(value = "成本价")
    @CanEmpty
    private BigDecimal costPrice;

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
     * 商品来源，0供应商，1商家
     */
    @ApiModelProperty(value = "商品来源，0供应商，1商家")
    private Integer goodsSource;

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
     * 是否多规格标记
     */
    @ApiModelProperty(value = "是否多规格标记", dataType = "com.wanmi.sbc.common.enums.DefaultFlag")
    private Integer moreSpecFlag;

    /**
     * 设价类型 0:客户,1:订货
     */
    @ApiModelProperty(value = "设价类型", dataType = "com.wanmi.sbc.goods.bean.enums.PriceType")
    private Integer priceType;

    /**
     * 是否按客户单独定价
     */
    @ApiModelProperty(value = "是否按客户单独定价", dataType = "com.wanmi.sbc.common.enums.DefaultFlag")
    private Integer customFlag;

    /**
     * 是否叠加客户等级折扣
     */
    @ApiModelProperty(value = "是否叠加客户等级折扣", dataType = "com.wanmi.sbc.common.enums.DefaultFlag")
    private Integer levelDiscountFlag;

    /**
     * 公司信息ID
     */
    @ApiModelProperty(value = "公司信息ID")
    private Long companyInfoId;

    /**
     * 公司名称
     */
    @ApiModelProperty(value = "公司名称")
    private String supplierName;

    /**
     * 供应商名称
     */
    @ApiModelProperty(value = "供应商名称")
    private String providerName;

    /**
     * 供应商id
     */
    @ApiModelProperty(value = "供应商id")
    private Long providerId;

    /**
     * 所属供应商商品Id
     */
    @ApiModelProperty(value = "所属供应商商品Id")
    private String providerGoodsId;

    /**
     * 店铺ID
     */
    @ApiModelProperty(value = "店铺ID")
    private Long storeId;
    /**
     * 店铺
     */
    @ApiModelProperty(value = "店铺")
    private String storeName;
    /**
     * 提交审核时间
     */
    @ApiModelProperty(value = "提交审核时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime submitTime;

    /**
     * 审核状态
     */
    @ApiModelProperty(value = "审核状态")
    @Enumerated
    private CheckStatus auditStatus;

    /**
     * 审核驳回原因
     */
    @ApiModelProperty(value = "审核驳回原因")
    private String auditReason;

    /**
     * 商品详情
     */
    @ApiModelProperty(value = "商品详情")
    private String goodsDetail;

    /**
     * 商品移动端详情
     */
    @ApiModelProperty(value = "商品移动端详情")
    private String goodsMobileDetail;

    /**
     * 库存，根据相关所有SKU库存来合计
     */
    @ApiModelProperty(value = "库存，根据相关所有SKU库存来合计")
    @Transient
    private BigDecimal stock;

    /**
     * 一对多关系，多个SKU编号
     */
    @ApiModelProperty(value = "一对多关系，多个SKU编号")
    @Transient
    private List<String> goodsInfoIds;

    /**
     * 多对多关系，多个店铺分类编号
     */
    @ApiModelProperty(value = "多对多关系，多个店铺分类编号")
    @Transient
    private List<Long> storeCateIds;

    /**
     * 商家类型 0、平台自营 1、第三方商家
     */
    @ApiModelProperty(value = "商家类型")
    @Enumerated
    private CompanyType companyType;

    /**
     * 商品体积 单位：m3
     */
    @ApiModelProperty(value = "商品体积", notes = "单位：m3")
    private BigDecimal goodsCubage;

    /**
     * 运费模板ID
     */
    @ApiModelProperty(value = "运费模板ID")
    private Long freightTempId;

    /**
     * 运费模板名称
     */
    @ApiModelProperty(value = "运费模板名称")
    @Transient
    private String freightTempName;

    /**
     * 销售类别
     */
    @ApiModelProperty(value = "销售类别", dataType = "com.wanmi.sbc.goods.bean.enums.SaleType")
    private Integer saleType;

    /**
     * 商品视频地址
     */
    @ApiModelProperty(value = "商品视频地址")
    @CanEmpty
    private String goodsVideo;

    /**
     * 划线价格
     */
    @ApiModelProperty(value = "划线价格")
    @CanEmpty
    private BigDecimal linePrice;

    /**
     * 商品浏览量
     */
    @ApiModelProperty(value = "商品浏览量")
    private Long goodsViewNum;

    /**
     * 订货量设价时,是否允许sku独立设阶梯价(0:不允许,1:允许)
     */
    @ApiModelProperty(value = "订货量设价时,是否允许sku独立设阶梯价", dataType = "com.wanmi.sbc.common.enums.DefaultFlag")
    private Integer allowPriceSet;

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
     * 是否禁止在新增拼团活动时选择
     */
    @ApiModelProperty(value = "是否禁止在新增拼团活动时选择")
    private boolean grouponForbiddenFlag;

    /**
     * 特价
     */
    @ApiModelProperty(value = "特价")
    private BigDecimal specialPrice;

    /**
     * 商品排序序号
     */
    @ApiModelProperty(value = "商品排序序号")
    private Integer goodsSeqNum;
    
    /**
     * 店铺内商品排序序号
     */
    @ApiModelProperty(value = "店铺内商品排序序号")
    private Integer storeGoodsSeqNum;

    /**
     * 品牌排序序号
     */
    @ApiModelProperty(value = "品牌排序序号")
    private Integer brandSeqNum;

    /**
     * 对应的erpNo
     */
    @ApiModelProperty(value = "对应的erpNo")
    private String erpNo;

    @ApiModelProperty(value = "最小价格")
    private BigDecimal minimumPrice;

    @ApiModelProperty(value = "绑定的商品标签")
    private String labelIdStr;

    private List<GoodsLabelVO> goodsLabels = new ArrayList<>();

    @ApiModelProperty(value = "批次号")
    private String goodsInfoBatchNo;

    /**
     * 对应的erpNo
     */
    @ApiModelProperty(value = "是否是特价商品")
    private Integer goodsInfoType;

    /**
     * 锁定库存数（用于库存同步）
     */
    @ApiModelProperty(value = "锁定库存数（用于库存同步）")
    private Integer lockStock;

    /**
     * 营销id
     */
    private Long marketingId;

    /**
     * 限购数量
     */
    private Long purchaseNum;

    /**
     * 仓库ID
     */
    private Long wareId;

    /**
     * 仓库名称
     */
    private String wareName;

    /**
     * 父级SKUID
     */
    private String parentGoodsInfoId;


    /**
     * 商品相关图片
     */
    @ApiModelProperty(value = "商品相关图片")
    private List<GoodsImageVO> images;

    /**
     * 促销图片和合成图片合集
     */
    private List<GoodsImageStypeVO> goodsImageStypeVOS;

    /**
     * 合成图片与促销图片与image的关系
     */
    private RelationGoodsImagesVO relationGoodsImagesVO;
    @ApiModelProperty(value = "生产日期")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime goodDate;
    /**
     * 是否是散称或定量
     */
    @ApiModelProperty(value = "散称或定量")
    private Integer isScatteredQuantitative;
    /**
     * 保质期
     */
    @ApiModelProperty(value = "保质期")
    private Long shelflife;

    /**
     * 日期单位
     */
    @ApiModelProperty(value = "日期单位")
    private String dateUnit;
}

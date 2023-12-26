package com.wanmi.sbc.goods.bean.vo;

import com.wanmi.sbc.common.enums.CompanyType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品导出 临时类
 * Created by lf on 2020/12/30.
 */
@ApiModel
@Data
public class StoreGoodsExportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品SKU编号
     */
    @ApiModelProperty(value = "商品SKU编号")
    private String goodsInfoId;

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
     * 商品类型，0:实体商品，1：虚拟商品 2.特价商品
     */
    @ApiModelProperty(value = "商品类型")
    private String goodsType;
    /**
     * 是否开启囤货 0、未开启 1、已开启、2已关闭
     */
    @ApiModelProperty(value = "囤货状态")
    private String pileState;

    /**
     * 商品市场价
     */
    @ApiModelProperty(value = "商品市场价")
    private BigDecimal marketPrice;
    /**
     * 大客户价
     */
    @ApiModelProperty(value = "大客户价")
    private BigDecimal vipPrice;
    /**
     * 成本价
     */
    @ApiModelProperty(value = "成本价")
    private BigDecimal costPrice;
    /**
     * 特价
     */
    @ApiModelProperty(value = "特价")
    private BigDecimal specialPrice;
    /**
     * 批次号
     */
    @ApiModelProperty(value = "批次号")
    private String goodsInfoBatchNo;
    /**
     * 多对多关系，多个店铺分类编号
     */
    @ApiModelProperty(value = "多对多关系，多个店铺分类编号")
    private List<Long> storeCateIds;
    /**
     * 多对多关系，多个店铺分类编号
     */
    @ApiModelProperty(value = "多对多关系，多个店铺分类编号")
    private List<String> storeCateNames;
    /**
     * 品牌编号
     */
    @ApiModelProperty(value = "品牌编号")
    private Long brandId;

    /**
     * 品牌名称
     */
    @ApiModelProperty(value = "品牌名称")
    private String brandName;
    /**
     * 上下架状态
     */
    @ApiModelProperty(value = "上下架状态", dataType = "com.wanmi.sbc.goods.bean.enums.AddedFlag")
    private Integer addedFlag;
    /**
     * 仓库名称
     */
    @ApiModelProperty(value = "仓库名称")
    private String wareName;
    /**
     * 销售类型 0:批发, 1:零售
     */
    @ApiModelProperty(value = "销售类型")
    private String saleType;
    /**
     * 上下架状态
     */
    @ApiModelProperty(value = "上下架状态")
    private String addedFlagInfo;

    /**
     * erp编码
     */
    @ApiModelProperty(value = "erp编码")
    private String erpNo;

    /**
     * 商品分类
     */
    @ApiModelProperty(value = "商品分类")
    private String cateName;
    /**
     * 保质期
     */
    @ApiModelProperty(value = "保质期")
    private String shelflife;
    /**
     * 包装类型
     */
    @ApiModelProperty(value = "包装类型")
    private String isScatteredQuantitative;
    /**
     * 规格
     */
    @ApiModelProperty(value = "规格")
    private String goodsInfoSubtitle;
    /**
     * 库存
     */
    @ApiModelProperty(value = "库存")
    private String stock;

    /**
     * 条形码
     */
    @ApiModelProperty(value = "条形码")
    private String goodsInfoBarcode;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位")
    private String goodsInfoUnit;
    /**
     * 重量
     */
    @ApiModelProperty(value = "重量")
    private String goodsInfoWeight;
    /**
     * 体积
     */
    @ApiModelProperty(value = "体积")
    private String goodsInfoCubage;

    /**
     * 增加的步长
     */
    @ApiModelProperty(value = "步长")
    private BigDecimal addStep;
    /**
     * 最小单位
     */
    @ApiModelProperty(value = "最小单位")
    private String devanningUnit;
    /**
     * 运费模板ID
     */
    @ApiModelProperty(value = "运费模板")
    private String freightTemp;


    /**
     * 第三方商家规格
     */
    @ApiModelProperty(value = "第三方商家规格")
    private String specText;
}

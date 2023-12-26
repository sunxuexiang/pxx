package com.wanmi.sbc.goods.bean.vo;

import com.wanmi.sbc.goods.bean.enums.DrugType;
import com.wanmi.sbc.goods.bean.enums.MedicineType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品SKU实体类
 * Created by dyt on 2017/4/11.
 */
@ApiModel
@Data
public class SettingGoodsInfoVO implements Serializable {

    private static final long serialVersionUID = 3973312626817597962L;

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
     * 商品图片
     */
    @ApiModelProperty(value = "商品图片")
    private String goodsInfoImg;

    /**
     * 最新计算的会员价
     * 为空，以市场价为准
     */
    @ApiModelProperty(value = "最新计算的会员价", notes = "为空，以市场价为准")
    private BigDecimal salePrice;

    /**
     * 商品市场价
     */
    @ApiModelProperty(value = "商品市场价")
    private BigDecimal marketPrice;

    /**
     * 规格名称规格值 颜色:红色;大小:16G
     */
    @ApiModelProperty(value = "规格名称规格值", example = "颜色:红色;大小:16G")
    private String specText;

    /**
     * 预估佣金
     */
    @ApiModelProperty(value = "预估佣金")
    private BigDecimal distributionCommission;

    /**
     * 促销标签
     */
    @ApiModelProperty(value = "促销标签")
    private List<MarketingLabelVO> marketingLabels = new ArrayList<>();

    /**
     * 商品销量
     */
    @ApiModelProperty(value = "商品销量")
    private Long goodsSalesNum;

    private SettingGoodsVO goods;

    /**
     * 商品药品分类 1 药品  2 非药品
     */
    @ApiModelProperty(value = "商品药品分类", notes = "1 药品  2 非药品")
    private MedicineType medicineType;

    /**
     * 药品类型：0 处方，1 OTC(甲类)，2 OTC(乙类)
     */
    @ApiModelProperty(value = "药品类型：0 处方，1 OTC(甲类)，2 OTC(乙类)")
    private DrugType drugType;

}
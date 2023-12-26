package com.wanmi.sbc.goods.api.request.standard;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>商品库查询分页请求类</p>
 * author: sunkun
 * Date: 2018-11-07
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandardGoodsPageRequest extends BaseQueryRequest implements Serializable {

    private static final long serialVersionUID = -3693107556414202757L;

    /**
     * 批量SPU编号
     */
    @ApiModelProperty(value = "spu Id")
    private List<String> goodsIds;

    /**
     * 模糊条件-商品名称
     */
    @ApiModelProperty(value = "模糊条件-商品名称")
    private String likeGoodsName;

    /**
     * 模糊条件-商品名称
     */
    @ApiModelProperty(value = "模糊条件-供应商名称")
    private String likeProviderName;

    /**
     * 商品来源，0供应商，1商家
     */
    @ApiModelProperty(value = "商品来源，0供应商，1商家")
    private Integer goodsSource;

    /**
     * 商品分类
     */
    @ApiModelProperty(value = "商品分类")
    private Long cateId;

    /**
     * 批量商品分类
     */
    @ApiModelProperty(value = "批量商品分类")
    private List<Long> cateIds;

    /**
     * 品牌编号
     */
    @ApiModelProperty(value = "品牌编号")
    private Long brandId;

    /**
     * 批量品牌分类
     */
    @ApiModelProperty(value = "批量品牌分类")
    private List<Long> brandIds;

    /**
     * 批量品牌分类，可与NULL以or方式查询
     */
    @ApiModelProperty(value = "批量品牌分类，可与NULL以or方式查询")
    private List<Long> orNullBrandIds;

    /**
     * 删除标记
     */
    @ApiModelProperty(value = "删除标记", dataType = "com.wanmi.sbc.common.enums.DeleteFlag")
    private Integer delFlag;

    /**
     * 非GoodsId
     */
    @ApiModelProperty(value = "非GoodsId")
    private String notGoodsId;

    /**
     * 价格范围最小
     */
    @ApiModelProperty(value = "价格范围最小")
    private BigDecimal specialPriceFirst;

    /**
     * 价格范围最大
     */
    @ApiModelProperty(value = "价格范围最大")
    private BigDecimal specialPriceLast;

    /**
     * 仓库Id
     */
    @ApiModelProperty(value = "wareId")
    private Long wareId;

    /**
     * 商品销售类型 0批发，1零售
     */
    @ApiModelProperty(value = "goodsSaleType")
    private Integer goodsSaleType;

    /**
     * erp 编码集合
     */
    @ApiModelProperty(value = "erpNos")
    private List<String> erpNos;

    /**
     * erp 编码集合
     */

    private List<String> ffskus;
}

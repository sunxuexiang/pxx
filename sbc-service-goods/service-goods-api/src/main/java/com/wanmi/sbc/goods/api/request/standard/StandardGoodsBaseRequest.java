package com.wanmi.sbc.goods.api.request.standard;

import com.wanmi.sbc.goods.bean.dto.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: ZhangLingKe
 * @Description: 标准商品web操作请求基类
 * @Date: 2018-11-08 17:20
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class StandardGoodsBaseRequest implements Serializable {

    private static final long serialVersionUID = 4089553794483727571L;

    /**
     * 商品信息
     */
    @ApiModelProperty(value = "商品信息")
    @NotNull
    private StandardGoodsDTO goods;

    /**
     * 商品相关图片
     */
    @ApiModelProperty(value = "商品相关图片")
    private List<StandardImageDTO> images;

    /**
     * 商品属性列表
     */
    @ApiModelProperty(value = "商品属性列表")
    private List<StandardPropDetailRelDTO> goodsPropDetailRels;

    /**
     * 商品规格列表
     */
    @ApiModelProperty(value = "商品规格列表")
    private List<StandardSpecDTO> goodsSpecs;

    /**
     * 商品规格值列表
     */
    @ApiModelProperty(value = "商品规格值列表")
    private List<StandardSpecDetailDTO> goodsSpecDetails;

    /**
     * 商品SKU列表
     */
    @ApiModelProperty(value = "商品SKU列表")
    @NotNull
    private List<StandardSkuDTO> goodsInfos;

}

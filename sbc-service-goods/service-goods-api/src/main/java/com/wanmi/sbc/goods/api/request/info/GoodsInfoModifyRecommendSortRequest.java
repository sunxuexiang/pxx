package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description: 编辑推荐商品排序请求参数实体类
 * @author: XinJiang
 * @time: 2022/4/25 11:04
 */
@ApiModel
@Data
public class GoodsInfoModifyRecommendSortRequest implements Serializable {

    private static final long serialVersionUID = 2393272623433229235L;

    /**
     * 商品编号，采用UUID
     */
    @ApiModelProperty(value = "商品编号，采用UUID")
    @NotBlank
    private String goodsInfoId;

    /**
     * 商品排序序号
     */
    @ApiModelProperty(value = "商品排序序号")
    @NotNull
    private Integer recommendSort;

    @ApiModelProperty(value = "仓库id")
    private Long wareId;
}

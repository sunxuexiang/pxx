package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 根据条件查找需要参与囤货活动的商品
 * Created by daiyitian on 2022/9/20.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListByCondition4PileActivityRequest implements Serializable {

    private static final long serialVersionUID = 8137430039658689185L;

    @ApiModelProperty(value = "店铺Id")
    private Long storeId;

    @ApiModelProperty(value = "仓库Id")
    private Long wareId;

    @ApiModelProperty(value = "SPU编号")
    private String goodsId;

    @ApiModelProperty(value = "SKU编号")
    private String goodsInfoId;

    @ApiModelProperty(value = "模糊条件-erp编码")
    private String likeErpNo;

    @ApiModelProperty(value = "模糊条件-商品名称")
    private String likeGoodsName;

    @ApiModelProperty(value = "是否是特价商品: 1是 0否")
    private Integer goodsInfoType;

    @ApiModelProperty(value = "分类编号")
    private Long cateId;

    @ApiModelProperty(value = "分类编号集合")
    private List<Long> cateIds;

    @ApiModelProperty(value = "品牌编号")
    private Long brandId;
}

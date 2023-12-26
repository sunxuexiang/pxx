package com.wanmi.sbc.goods.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class GoodsBrandsQueryRequest extends BaseQueryRequest {

    /**
     * 一级分类编号
     */
    @ApiModelProperty(value = "分类编号")
    private Long cate1Id;

    /**
     * 二级分类编号
     */
    @ApiModelProperty(value = "分类编号")
    private Long cate2Id;

    /**
     * 三级分类编号
     */
    @ApiModelProperty(value = "分类编号")
    private Long cate3Id;

    /**
     * 称量类型
     */
    @ApiModelProperty(value = "称量类型")
    private Integer classifyType;

}

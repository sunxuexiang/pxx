package com.wanmi.sbc.xsite;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class GoodsInfoRequest {

    @ApiModelProperty(value = "分类编号")
    private Long catName;

    @ApiModelProperty(value = "第几页")
    private Integer pageNum;

    @ApiModelProperty(value = "每页显示多少条")
    private Integer pageSize;

    @ApiModelProperty(value = "模糊条件-商品名称")
    private String q;

    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "店铺建站Id")
    private Long storeId;

    @ApiModelProperty(value = "店铺搜索Id")
    private Long searchByStore;

}

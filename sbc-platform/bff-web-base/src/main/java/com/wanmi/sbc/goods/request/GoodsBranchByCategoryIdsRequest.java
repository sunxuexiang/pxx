package com.wanmi.sbc.goods.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
public class GoodsBranchByCategoryIdsRequest implements Serializable {
    /**
     * 分类编号集合
     */
    @ApiModelProperty(value = "三级分类编号集合")
    private List<Long> cateIds;
}

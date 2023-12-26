package com.wanmi.sbc.goods.api.request.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author: Like
 * @Created: 2023/4/5 16:24
 */
@ApiModel
@Data
public class GoodsBatchModifyCateRequest implements Serializable {

    private static final long serialVersionUID = -3502292806731244461L;

    @ApiModelProperty(value = "分类ID")
    private Long cateId;

    @ApiModelProperty(value = "商品ID集合")
    private List<String> goodsIds;
}

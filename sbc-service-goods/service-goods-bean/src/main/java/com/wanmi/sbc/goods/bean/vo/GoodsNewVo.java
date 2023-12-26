package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApiModel
@Data
public class GoodsNewVo implements Serializable {

    private static final long serialVersionUID = 8495660714593669784L;

    private String id;

    /**
     * 商品副标题
     */
    @ApiModelProperty(value = "商品fu标题")
    private String goodsSubtitle;

    /**
     * SKU信息
     */
    private List<GoodsInfoNewVO> goodsInfos;

    /**
     * 商品绑定的规格数据
     */
    @ApiModelProperty(value = "商品绑定的规格数据")
    private List<GoodsLabelVO> goodsLabels = new ArrayList<>();
}

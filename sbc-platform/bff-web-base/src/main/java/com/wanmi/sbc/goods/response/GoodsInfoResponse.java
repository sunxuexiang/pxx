package com.wanmi.sbc.goods.response;

import com.wanmi.sbc.goods.bean.vo.GoodsNewVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class GoodsInfoResponse implements Serializable {

    private static final long serialVersionUID = -2596395758191032675L;

    /**
     * 索引SKU
     */
    @ApiModelProperty(value = "索引SKU")
    private Page<GoodsNewVo> esGoods = new PageImpl<>(new ArrayList<>());

    /**
     * 默认
     */
    private List<GoodsNewVo> goodsData;

    /**
     * 商品分类 0：散称
     */
    @ApiModelProperty(value = "商品分类list 0：散称")
    private List<GoodsNewVo> categoriesZero;

    /**
     * 商品分类 1：定量
     */
    @ApiModelProperty(value = "商品分类list 1：定量")
    private List<GoodsNewVo> categoriesOne;
}

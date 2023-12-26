package com.wanmi.sbc.goods.response;

import com.wanmi.sbc.es.elastic.model.root.GoodsCateNest;
import com.wanmi.sbc.goods.bean.vo.GoodsNewVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
/**
 * 按商品分类查询商品信息
 */
public class GoodsInfoByCateIdResponse implements Serializable {
    private static final long serialVersionUID = 2308704300207191609L;

    private GoodsCateNest goodsCateNest;

    private List<GoodsNewVo> goodsData;

    /**
     * 商品分类 0：散称
     */
    @ApiModelProperty(value = "商品分类list 0：散称")
    private List<GoodsNewVo> categoriesZero;

    /**
     * 商品分类 1：定量
     */
    @ApiModelProperty(value = "商品分类list 0：散称")
    private List<GoodsNewVo> categoriesOne;





}

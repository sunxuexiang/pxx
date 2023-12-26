package com.wanmi.sbc.goods.api.response.goods;

import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author chenjun
 *
 */
@ApiModel
@Data
public class SpecialGoodsAddResponse implements Serializable {

    private static final long serialVersionUID = -4006377624007898240L;
    /**
     * 商品信息
     */
    @ApiModelProperty(value = "商品信息")
    private List<GoodsVO> goods;

}

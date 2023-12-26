package com.wanmi.sbc.goods.api.request.goods;

import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
public class SpecialGoodsSaveRequest implements Serializable {

    private static final long serialVersionUID = 5013340347092819260L;
    /**
     * spu
     */
    @ApiModelProperty(value = "spu")
    private List<GoodsVO> goodsVOs;
}

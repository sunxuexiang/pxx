package com.wanmi.sbc.goods.api.response.devanninggoodsinfo;

import com.wanmi.sbc.goods.bean.vo.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DevanningGoodsInfoResponse implements Serializable {

    private static final long serialVersionUID = -4370164109574914820L;


    /**
     * 拆箱商品信息
     */
    @ApiModelProperty(value = "拆箱商品信息")
    private List<DevanningGoodsInfoVO> devanningGoodsInfoVOS;

    /**
     * 商品信息
     */
    @ApiModelProperty(value = "拆箱商品信息")
    private List<GoodsVO> goodses;

}

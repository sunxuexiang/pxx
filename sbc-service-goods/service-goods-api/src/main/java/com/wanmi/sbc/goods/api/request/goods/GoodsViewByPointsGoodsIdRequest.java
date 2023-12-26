package com.wanmi.sbc.goods.api.request.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * 根据积分商品编号查询商品信息请求对象
 * @author yinxianzhi
 * @dateTime 2019/5/24 上午9:40
 */
@ApiModel
@Data
public class GoodsViewByPointsGoodsIdRequest implements Serializable {

    private static final long serialVersionUID = 1645640491540876788L;

    @ApiModelProperty(value = "积分商品Id")
    @NotBlank
    private String pointsGoodsId;

    @ApiModelProperty(value = "仓id")
    private Long wareId;

}

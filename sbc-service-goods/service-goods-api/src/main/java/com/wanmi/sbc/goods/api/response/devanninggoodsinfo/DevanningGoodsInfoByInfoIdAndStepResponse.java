package com.wanmi.sbc.goods.api.response.devanninggoodsinfo;

import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@ApiModel
@Data
public class DevanningGoodsInfoByInfoIdAndStepResponse implements Serializable {

    private static final long serialVersionUID = -4370164109574914820L;


    /**
     * 拆箱商品信息
     */
    @ApiModelProperty(value = "拆箱商品信息")
    private DevanningGoodsInfoVO devanningGoodsInfoVO;

}

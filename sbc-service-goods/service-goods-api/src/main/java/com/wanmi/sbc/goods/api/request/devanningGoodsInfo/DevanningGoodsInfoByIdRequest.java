package com.wanmi.sbc.goods.api.request.devanningGoodsInfo;

import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
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
public class DevanningGoodsInfoByIdRequest implements Serializable {


    private static final long serialVersionUID = 3595422734197974790L;
    /**
     * 拆箱主键id
     */
    @ApiModelProperty(value = "拆箱主键id")
    private Long devanningId;

    @ApiModelProperty
    private List<Long> devanningIds;
}

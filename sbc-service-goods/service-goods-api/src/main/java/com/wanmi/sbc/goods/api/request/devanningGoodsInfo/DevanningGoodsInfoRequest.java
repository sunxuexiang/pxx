package com.wanmi.sbc.goods.api.request.devanningGoodsInfo;

import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.GoodsInfoSelectStatus;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DevanningGoodsInfoRequest implements Serializable {


    private static final long serialVersionUID = 3595422734197974790L;
    private DevanningGoodsInfoVO devanningGoodsInfoVO;
}

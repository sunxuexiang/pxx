package com.wanmi.sbc.goods.api.request.devanningGoodsInfo;

import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import io.swagger.annotations.ApiModel;
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
public class DevanningGoodsInfoListRequest implements Serializable {


    private static final long serialVersionUID = 3595422734197974790L;
    private List<DevanningGoodsInfoVO> devanningGoodsInfoVOS;
}

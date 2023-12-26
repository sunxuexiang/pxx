package com.wanmi.sbc.goods.api.request.shortages;

import com.wanmi.sbc.goods.bean.vo.ShortagesGoodsInfoVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortagesGoodsInfoAddRequest implements Serializable {
    private static final long serialVersionUID = -7079252230943370049L;

    List<ShortagesGoodsInfoVO> goodsInfoVOList;
}

package com.wanmi.sbc.goods.api.response.info;

import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpecialGoodsModifyResponse implements Serializable {
    private static final long serialVersionUID = 3399913104710905763L;
    /**
     * sku商品列表
     */
    private List<GoodsInfoVO> goodsInfoList;
}

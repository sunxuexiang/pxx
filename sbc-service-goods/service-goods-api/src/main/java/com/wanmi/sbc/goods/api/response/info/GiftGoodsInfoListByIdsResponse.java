package com.wanmi.sbc.goods.api.response.info;

import com.wanmi.sbc.goods.bean.vo.GiftGoodsInfoVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 商品SKU视图响应
 * Created by daiyitian on 2017/3/24.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GiftGoodsInfoListByIdsResponse implements Serializable {


    private static final long serialVersionUID = -1038432343814374844L;
    /**
     * 商品SKU信息
     */
    private List<GiftGoodsInfoVO> goodsInfos;
}

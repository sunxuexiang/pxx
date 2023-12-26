package com.wanmi.sbc.shopcart.api.response.purchase;

import com.wanmi.sbc.goods.bean.vo.GoodsStoreGroupVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 我的囤货-多商家分组商品列表
 *
 * @author yitang
 * @version 1.0
 */
@Data
@Builder
public class PilePurchaseStoreResponse implements Serializable {

    /**
     * 返回
     */
    @ApiModelProperty(value = "我的囤货商品")
    private List<GoodsStoreGroupVO> storeGoodsList;

}

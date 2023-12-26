package com.wanmi.sbc.returnorder.api.response.purchase;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CustomerPilePurchaseListResponse implements Serializable {
    private static final long serialVersionUID = 2381448589099106567L;

    @ApiModelProperty(value = "有效可提货囤货商品")
    private MicroServicePage<GoodsInfoVO> goodsInfos;
}

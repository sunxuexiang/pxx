package com.wanmi.sbc.goods.api.request.info;

import lombok.Data;

@Data
public class GoodsInfoPurchaseNumDTO {
    private String goodsInfoId;
    private Long marketingId;
    private Long purchaseNum;
}

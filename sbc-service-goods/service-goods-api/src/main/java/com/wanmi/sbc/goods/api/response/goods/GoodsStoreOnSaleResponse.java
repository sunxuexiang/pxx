package com.wanmi.sbc.goods.api.response.goods;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsStoreOnSaleResponse implements Serializable {

    private static final long serialVersionUID = -3377526165125351300L;

    private Long storeId;

    private Integer onSaleNum;
}

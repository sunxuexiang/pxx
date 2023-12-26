package com.wanmi.sbc.customer.bean.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-12-18 14:26
 **/
@Data
public class StoreGoodsCatPublishGoodsDTO implements Serializable {

    private static final long serialVersionUID = -7214660961508054246L;

    private String goodsId;

    private Long storeId;
}

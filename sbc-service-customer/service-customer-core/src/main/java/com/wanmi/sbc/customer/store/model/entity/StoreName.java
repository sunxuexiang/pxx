package com.wanmi.sbc.customer.store.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: gaomuwei
 * @Date: Created In 下午2:19 2019/5/23
 * @Description:
 */
@Data
@AllArgsConstructor
public class StoreName {



    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

}

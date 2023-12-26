package com.wanmi.sbc.job.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: sbc-backgroud
 * @description: 客户商家商品表
 * @author: gdq
 * @create: 2023-06-16 15:29
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuyerSupplierItemNumVO implements Serializable {
    private static final long serialVersionUID = 7373235870168954584L;

    private String buyerId;

    private Long fee;

    private String storeId;
}

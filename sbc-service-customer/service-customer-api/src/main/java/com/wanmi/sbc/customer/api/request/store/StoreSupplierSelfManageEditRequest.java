package com.wanmi.sbc.customer.api.request.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-07-28 11:42
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreSupplierSelfManageEditRequest implements Serializable {

    private static final long serialVersionUID = 2026895186209306255L;
    private Long storeId;

    /**
     * 是否自营 0否 1是
     */
    private Integer selfManage;
}

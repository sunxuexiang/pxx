package com.wanmi.sbc.setting.api.request.imonlineservice;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>在线客服店铺关系</p>
 * @author zzg
 * @date 2023-10-05 16:10:28
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerServiceStoreRelationRequest implements Serializable {

    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 公司ID
     */
    private Long companyInfoId;

    /**
     * 店铺名字
     */
    private String storeName;

    /**
     * 子店铺列表
     */
    private List<CustomerServiceStoreRelationChildRequest> childList;

}

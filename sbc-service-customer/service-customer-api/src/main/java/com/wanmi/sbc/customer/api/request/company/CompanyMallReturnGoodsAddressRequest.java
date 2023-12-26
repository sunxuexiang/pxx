package com.wanmi.sbc.customer.api.request.company;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: sbc-backgroud
 * @description: aa
 * @author: gdq
 * @create: 2023-06-13 15:24
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMallReturnGoodsAddressRequest extends BaseQueryRequest {

    /**
     * 推荐Id
     */
    private List<Long> Ids;

    private Long companyInfoId;

    private Long storeId;

    private DeleteFlag deleteFlag;
}


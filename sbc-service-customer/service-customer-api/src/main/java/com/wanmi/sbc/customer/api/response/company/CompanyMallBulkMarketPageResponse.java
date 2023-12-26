package com.wanmi.sbc.customer.api.response.company;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.customer.bean.vo.CompanyMallBulkMarketVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-13 16:08
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMallBulkMarketPageResponse {
    private static final long serialVersionUID = 6492765528117007884L;
    /**
     * 公司信息列表
     */
    @ApiModelProperty(value = "公司批发市场列表")
    private MicroServicePage<CompanyMallBulkMarketVO> page;
}

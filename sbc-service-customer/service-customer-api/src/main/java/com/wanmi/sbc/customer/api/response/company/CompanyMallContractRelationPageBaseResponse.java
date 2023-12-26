package com.wanmi.sbc.customer.api.response.company;

import com.wanmi.sbc.customer.api.base.MicroServiceBasePage;
import com.wanmi.sbc.customer.api.vo.CompanyMallContractRelationBaseVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyMallContractRelationPageBaseResponse {

    private static final long serialVersionUID = 6492765528117007884L;
    /**
     * 公司信息列表
     */
    @ApiModelProperty(value = "公司商家签约信息")
    private MicroServiceBasePage<CompanyMallContractRelationBaseVO> page;
}

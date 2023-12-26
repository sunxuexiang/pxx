package com.wanmi.sbc.marketing.api.response.market;

import com.wanmi.sbc.marketing.bean.vo.MarketingForEndVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author: yang
 * @CreateDate: 2020/12/28 9:56
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketingGetByIdsForCustomerResponse {

    /**
     * 营销信息
     */
    @ApiModelProperty(value = "营销信息")
    private List<MarketingForEndVO> marketingForEndVOS;

}

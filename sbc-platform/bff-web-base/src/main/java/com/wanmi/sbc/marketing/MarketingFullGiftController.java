package com.wanmi.sbc.marketing;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.marketing.api.provider.gift.FullGiftQueryProvider;
import com.wanmi.sbc.marketing.api.request.gift.FullGiftLevelListByMarketingIdAndCustomerRequest;
import com.wanmi.sbc.marketing.api.response.gift.FullGiftLevelListByMarketingIdAndCustomerResponse;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Api(tags = "MarketingFullGiftController", description = "满赠营销服务API")
@RestController
@RequestMapping("/gift")
@Validated
public class MarketingFullGiftController {

    @Autowired
    private FullGiftQueryProvider fullGiftQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    /**
     * 根据营销Id获取赠品信息
     *
     * @param marketingId 活动ID
     * @return
     */
    @ApiOperation(value = "根据营销Id获取赠品信息")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "marketingId", value = "营销Id", required = true)
    @RequestMapping(value = "/{marketingId}", method = RequestMethod.GET)
    public BaseResponse<FullGiftLevelListByMarketingIdAndCustomerResponse> getGiftByMarketingId(@PathVariable("marketingId") Long marketingId
            ,Boolean matchWareHouseFlag) {
        CustomerVO customer = commonUtil.getCustomer();
        FullGiftLevelListByMarketingIdAndCustomerRequest fullgiftRequest = FullGiftLevelListByMarketingIdAndCustomerRequest
                .builder().build();
        if (Objects.nonNull(customer)){
            fullgiftRequest.setCustomer(KsBeanUtil.convert(commonUtil.getCustomer(), CustomerDTO.class));
            fullgiftRequest.setMatchWareHouseFlag(matchWareHouseFlag);
        }
        fullgiftRequest.setMarketingId(marketingId);
        return fullGiftQueryProvider.listGiftByMarketingIdAndCustomer(fullgiftRequest);
    }

    /**
     * 未登录时根据营销Id获取赠品信息
     * @param marketingId 活动ID
     * @return
     */
    @ApiOperation(value = "未登录时根据营销Id获取赠品信息")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "marketingId", value = "营销Id", required = true)
    @RequestMapping(value = "/unLogin/{marketingId}", method = RequestMethod.GET)
    public BaseResponse<FullGiftLevelListByMarketingIdAndCustomerResponse> getGiftByMarketingIdWithOutLogin(@PathVariable("marketingId")Long marketingId) {
        FullGiftLevelListByMarketingIdAndCustomerRequest fullgiftRequest = FullGiftLevelListByMarketingIdAndCustomerRequest.builder().build();
        fullgiftRequest.setMarketingId(marketingId);
        return fullGiftQueryProvider.listGiftByMarketingIdAndCustomer(fullgiftRequest);
    }
}

package com.wanmi.sbc.order.provider.impl.suit;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.order.api.provider.suit.SuitOrderTempProvider;
import com.wanmi.sbc.order.api.request.suit.SuitOrderTempQueryRequest;
import com.wanmi.sbc.order.suit.SuitOrderTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Validated
@RestController
public class SuitOrderTempController implements SuitOrderTempProvider {

    @Autowired
    private SuitOrderTempService suitOrderTempService;

    @Override
    public BaseResponse<Integer> getSuitBuyCountByCustomerAndMarketingId(SuitOrderTempQueryRequest request) {

        if(Objects.isNull(request)
                || Objects.isNull(request.getCustomerId())
                || Objects.isNull(request.getMarketingId())){
            return BaseResponse.success(0);
        }

        Integer count = suitOrderTempService.getSuitBuyCountByCustomerAndMarketingId(KsBeanUtil.convert(request, com.wanmi.sbc.order.suit.request.SuitOrderTempQueryRequest.class));
        return BaseResponse.success(Objects.isNull(count) ? 0 : count);
    }
}

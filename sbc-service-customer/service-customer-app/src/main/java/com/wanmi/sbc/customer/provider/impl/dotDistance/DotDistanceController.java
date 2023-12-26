package com.wanmi.sbc.customer.provider.impl.dotDistance;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.dotdistance.DotDistanceProvider;
import com.wanmi.sbc.customer.api.provider.email.CustomerEmailQueryProvider;
import com.wanmi.sbc.customer.api.request.email.NoDeleteCustomerEmailListByCustomerIdRequest;
import com.wanmi.sbc.customer.api.response.email.NoDeleteCustomerEmailListByCustomerIdResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerDeliveryAddressVO;
import com.wanmi.sbc.customer.bean.vo.CustomerEmailVO;
import com.wanmi.sbc.customer.dotdistance.service.DotDistanceService;
import com.wanmi.sbc.customer.email.model.root.CustomerEmail;
import com.wanmi.sbc.customer.email.service.CustomerEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
public class DotDistanceController implements DotDistanceProvider {

    @Autowired
    private DotDistanceService dotDistanceService;

    @Override
    public BaseResponse execut() {
        dotDistanceService.executAll();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse executOne(@RequestBody @Valid CustomerDeliveryAddressVO customerDeliveryAddressVO) {
          dotDistanceService.executOneAdress(customerDeliveryAddressVO);
        return BaseResponse.SUCCESSFUL();
    }
}

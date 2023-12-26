package com.wanmi.sbc.customer.api.provider.dotdistance;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.email.CustomerEmailAddRequest;
import com.wanmi.sbc.customer.api.request.email.CustomerEmailDeleteRequest;
import com.wanmi.sbc.customer.api.request.email.CustomerEmailModifyRequest;
import com.wanmi.sbc.customer.api.response.email.CustomerEmailAddResponse;
import com.wanmi.sbc.customer.api.response.email.CustomerEmailModifyResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerDeliveryAddressVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "DotDistanceProvider")
public interface DotDistanceProvider {

    /**
     * 批量执行定时任务方法
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/dotdistance/execut")
    BaseResponse execut();

    /**
     * 批量执行定时任务方法
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/dotdistance/executOne")
    BaseResponse executOne(@RequestBody @Valid CustomerDeliveryAddressVO customerDeliveryAddressVO);

}

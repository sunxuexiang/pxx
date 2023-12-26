package com.wanmi.sbc.pay.api.provider;

import javax.validation.Valid;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.pay.api.response.EcnyPayFormResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.wanmi.sbc.pay.api.request.PayExtraRequest;

/**
 * @program: service-pay
 * @description: 人民币相关接口
 * @create: 2021-06-13
 **/
@FeignClient(value = "${application.pay.name}", url="${feign.url.pay:#{null}}", contextId = "EcnyPayProvider")
public interface EcnyPayProvider {

    /**
     * 支付接口，返回数据字人民币支付表单前端自动提交重定向到收银台
     * @param request
     * @return
     */
    @PostMapping("/ecny/${application.pay.version}/get-payForm")
    BaseResponse<EcnyPayFormResponse> getPayForm(@RequestBody @Valid PayExtraRequest request);
    
    
  
    

}

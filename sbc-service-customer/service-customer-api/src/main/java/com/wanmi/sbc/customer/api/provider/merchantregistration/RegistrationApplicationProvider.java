package com.wanmi.sbc.customer.api.provider.merchantregistration;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.company.*;
import com.wanmi.sbc.customer.api.request.merchantregistration.MerchantRegistrationAddRequest;
import com.wanmi.sbc.customer.api.request.merchantregistration.MerchantRegistrationModifyRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoAddResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoModifyResponse;
import com.wanmi.sbc.customer.api.response.merchantregistration.MerchantRegistrationAddResponse;
import com.wanmi.sbc.customer.api.response.merchantregistration.MerchantRegistrationModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 商家入驻申请记录-入驻申请记录添加/修改/删除API
 * @Author: hudong
 * @CreateDate: 2023/6/17 08:25
 * @Version: 1.0
 */
@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}",contextId = "RegistrationApplicationProvider")
public interface RegistrationApplicationProvider {


    /**
     * 新增商家入驻申请信息
     *
     * @param request 新增商家入驻申请信息request {@link MerchantRegistrationAddRequest}
     * @return 商家入驻申请信息 {@link MerchantRegistrationAddResponse}
     */
    @PostMapping("/customer/${application.customer.version}/registration/add-application-info")
    BaseResponse<MerchantRegistrationAddResponse> addMerchantRegistration(@RequestBody MerchantRegistrationAddRequest request);

    /**
     * 修改商家入驻申请信息
     *
     * @param request 修改商家入驻申请信息request {@link MerchantRegistrationModifyRequest}
     * @return 商家入驻申请信息 {@link MerchantRegistrationModifyResponse}
     */
    @PostMapping("/customer/${application.customer.version}/registration/modify-application-info")
    BaseResponse<MerchantRegistrationModifyResponse> modifyMerchantRegistration(@RequestBody MerchantRegistrationModifyRequest request);


}

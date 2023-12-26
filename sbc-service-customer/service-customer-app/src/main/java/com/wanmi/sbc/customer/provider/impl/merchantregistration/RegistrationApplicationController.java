package com.wanmi.sbc.customer.provider.impl.merchantregistration;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.merchantregistration.RegistrationApplicationProvider;
import com.wanmi.sbc.customer.api.request.merchantregistration.MerchantRegistrationAddRequest;
import com.wanmi.sbc.customer.api.request.merchantregistration.MerchantRegistrationModifyRequest;
import com.wanmi.sbc.customer.api.request.merchantregistration.MerchantRegistrationSaveRequest;
import com.wanmi.sbc.customer.api.response.merchantregistration.MerchantRegistrationAddResponse;
import com.wanmi.sbc.customer.api.response.merchantregistration.MerchantRegistrationModifyResponse;
import com.wanmi.sbc.customer.merchantregistration.model.root.MerchantRegistrationApplication;
import com.wanmi.sbc.customer.merchantregistration.service.MerchantRegistrationApplicationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * <p>商家入驻申请操作接口实现</p>
 * @author hudong
 * @date 2023-06-17 11:13
 */
@RestController
@Validated
public class RegistrationApplicationController implements RegistrationApplicationProvider {

    @Autowired
    private MerchantRegistrationApplicationService merchantRegistrationApplicationService;


    @Override
    public BaseResponse<MerchantRegistrationAddResponse> addMerchantRegistration(@RequestBody MerchantRegistrationAddRequest request) {
        MerchantRegistrationSaveRequest saveRequest = new MerchantRegistrationSaveRequest();
        KsBeanUtil.copyPropertiesThird(request, saveRequest);
        MerchantRegistrationApplication oldMerchantRegistrationApplication = merchantRegistrationApplicationService.findByMerchantPhoneAndDelFlag(request.getMerchantPhone());
        if(!Objects.isNull(oldMerchantRegistrationApplication)) {
            throw new SbcRuntimeException("K800001","商家入驻申请的联系电话记录已存在,请勿重复操作");
        }
        //拼接完整地址
        saveRequest.setMerchantAddress(buildAddress(request));
        MerchantRegistrationApplication merchantRegistrationApplication = merchantRegistrationApplicationService.saveMerchantRegistrationApplication(saveRequest);
        MerchantRegistrationAddResponse response = new MerchantRegistrationAddResponse();
        KsBeanUtil.copyPropertiesThird(merchantRegistrationApplication, response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<MerchantRegistrationModifyResponse> modifyMerchantRegistration(@RequestBody MerchantRegistrationModifyRequest request) {
        MerchantRegistrationSaveRequest saveRequest = new MerchantRegistrationSaveRequest();
        KsBeanUtil.copyPropertiesThird(request, saveRequest);
        MerchantRegistrationApplication merchantRegistrationApplication = merchantRegistrationApplicationService.updateMerchantRegistrationApplication(saveRequest);
        MerchantRegistrationModifyResponse response = new MerchantRegistrationModifyResponse();
        KsBeanUtil.copyPropertiesThird(merchantRegistrationApplication, response);
        return BaseResponse.success(response);
    }

    /**
     * 拼接地址
     * @param request 请求参数
     * @return 返回拼接后的地址
     */
    private String buildAddress(MerchantRegistrationAddRequest request){
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(request.getProvinceName())) {
            sb.append(request.getProvinceName());
        }
        if (StringUtils.isNotEmpty(request.getCityName())) {
            sb.append(request.getCityName());
        }
        if (StringUtils.isNotEmpty(request.getAreaName())) {
            sb.append(request.getAreaName());
        }
        if (StringUtils.isNotEmpty(request.getDeliveryAddress())) {
            sb.append(request.getDeliveryAddress());
        }
        return sb.toString();
    }
}

package com.wanmi.sbc.merchantregistration;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.provider.merchantregistration.RegistrationApplicationProvider;
import com.wanmi.sbc.customer.api.request.merchantregistration.MerchantRegistrationAddRequest;
import com.wanmi.sbc.customer.api.response.merchantregistration.MerchantRegistrationAddResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 商家入驻申请APP
 * @author hudong
 * @date 2023-06-19
 */
@RestController
@RequestMapping("/merchant_registration")
@Api(tags = "RegistrationApplicationController", description = "S2B 平台端-商家入驻申请API")
public class RegistrationApplicationController {

    @Autowired
    private RegistrationApplicationProvider registrationApplicationProvider;

    @ApiOperation(value = "新增商家入驻申请")
    @PostMapping("/addMerchantApplication")
    public BaseResponse<MerchantRegistrationAddResponse> add(@RequestBody MerchantRegistrationAddRequest addReq) {
        //参数校验逻辑
        BaseResponse valid = validParam(addReq);
        if(!Objects.isNull(valid)){
            return valid;
        }
        return registrationApplicationProvider.addMerchantRegistration(addReq);
    }

    /**
     * 入参校验
     * @param addReq
     * @return
     */
    private BaseResponse validParam(MerchantRegistrationAddRequest addReq){
        if(StringUtils.isEmpty(addReq.getMerchantPhone())) {
            return BaseResponse.error("商家联系方式不能为空");
        }
        if(StringUtils.isEmpty(addReq.getMerchantName())){
            return BaseResponse.error("商家名称不能为空");
        }
        if(!isValidMobile(addReq.getMerchantPhone())){
            return BaseResponse.error("商家联系方式输入格式不正确");
        }
        return null;
    }

    private boolean isValidMobile(String mobile) {

        // 手机号正则表达式
        String regex = "^[1]\\d{10}$";

        // 编译正则表达式
        Pattern p = Pattern.compile(regex);

        // 匹配手机号
        Matcher m = p.matcher(mobile);

        // 判断是否匹配
        return m.matches();
    }



}

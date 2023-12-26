package com.wanmi.sbc.onlineservice;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.IpUtil;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceLoginProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceSignRequest;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author zhouzhenguo
 * @createDate 2023-09-07 10:49
 * @Description: 商家客服登录信息接口
 * @Version 1.0
 */
@Api(tags = "ImOnLineServiceController", description = "腾讯 im api")
@RestController
@RequestMapping("/customerAccount")
@Slf4j
@Validated
public class CustomerServiceLoginController {

    @Autowired
    private CustomerServiceLoginProvider customerServiceLoginProvider;

    @Autowired
    private CommonUtil commonUtil;

    @ApiOperation(value = "商家客服登录")
    @RequestMapping(value = "/loginSuccess", method = RequestMethod.POST)
    public BaseResponse loginSuccess (@RequestBody ImOnlineServiceSignRequest request, HttpServletRequest httpRequest) {
        request.setIpAddr(IpUtil.getIpAddr(httpRequest));
        return customerServiceLoginProvider.loginSuccess(request);
    }

}

package com.wanmi.sbc.pay;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.payswitch.AppPaySwitchProvider;
import com.wanmi.sbc.setting.api.request.payswitch.AppPaySwitchParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * app支付开关设置
 */
@RestController
@RequestMapping("/appPaySwitch")
@Slf4j
public class AppPaySwitchController {

    @Autowired
    private AppPaySwitchProvider appPaySwitchProvider;

    @RequestMapping(value = "/updateAppPaySwitch", method = RequestMethod.POST)
    public BaseResponse updateAppPaySwitch (@RequestBody List<AppPaySwitchParam> request) {
        if (ObjectUtils.isEmpty(request)) {
            return BaseResponse.error("参数错误");
        }
        return appPaySwitchProvider.updateAppPaySwitch(request);
    }

    @RequestMapping(value = "/getAppPaySwitch", method = RequestMethod.GET)
    public BaseResponse getAppPaySwitch () {
        return appPaySwitchProvider.getAppPaySwitch();
    }
}

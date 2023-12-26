package com.wanmi.sbc.imMessage;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceChatProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceChatRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceSignRequest;
import com.wanmi.sbc.setting.api.response.onlineservice.CustomerServiceInfoResponse;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "CustomerServiceChatController", description = "腾讯IM客服聊天接口")
@RestController
@RequestMapping("/serviceChat")
@Slf4j
@Validated
@Data
public class CustomerServiceChatController {

    @Autowired
    private CustomerServiceChatProvider customerServiceChatProvider;

    @Autowired
    private CommonUtil commonUtil;

    @ApiOperation(value = "账号管理列表")
    @RequestMapping(value = "/managerList", method = RequestMethod.GET)
    public BaseResponse<List<CustomerServiceInfoResponse>> getManagerList () {
        Long companyInfo = null;
        try {
            companyInfo = commonUtil.getCompanyInfoId();
        } catch (Exception e) {}
        if (companyInfo == null) {
            companyInfo = -1l;
        }
        return customerServiceChatProvider.getCustomerServiceManagerList(companyInfo);
    }

    @ApiModelProperty(value = "客户端手机及版本信息")
    @RequestMapping(value = "/getAppUserInfo", method = RequestMethod.POST)
    public BaseResponse getAppChatUserInfo (@RequestBody CustomerServiceChatRequest request) {
        return customerServiceChatProvider.getAppChatUserInfo(request);
    }


    @ApiModelProperty(value = "离线消息聊天列表")
    @RequestMapping(value = "/offlineList", method = RequestMethod.POST)
    public BaseResponse getOfflineList (@RequestBody CustomerServiceChatRequest request) {
        Long companyInfo = null;
        try {
            companyInfo = commonUtil.getCompanyInfoId();
        } catch (Exception e) {}
        if (companyInfo == null) {
            companyInfo = -1l;
        }
        request.setCompanyInfoId(companyInfo);
        return customerServiceChatProvider.getOfflineList(request);
    }
}

package com.wanmi.sbc.imMessage;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceChatMarkProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceChatMarkRequest;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "CustomerServiceChatMarkController", description = "聊天标星功能")
@RestController
@RequestMapping("/chatMark")
@Slf4j
@Validated
@Data
public class CustomerServiceChatMarkController {


    @Autowired
    private CustomerServiceChatMarkProvider customerServiceChatMarkProvider;

    @RequestMapping("/add")
    public BaseResponse add (@RequestBody CustomerServiceChatMarkRequest request) {
        if (StringUtils.isEmpty(request.getServerAccount())) {
            return BaseResponse.error("登录客服账号不能为空");
        }
        if (StringUtils.isEmpty(request.getImGroupId())) {
            return BaseResponse.error("群组ID不能为空");
        }
        return customerServiceChatMarkProvider.add(request);
    }

    @RequestMapping("/delete")
    public BaseResponse delete (@RequestBody CustomerServiceChatMarkRequest request) {
        if (StringUtils.isEmpty(request.getServerAccount())) {
            return BaseResponse.error("登录客服账号不能为空");
        }
        if (StringUtils.isEmpty(request.getImGroupId())) {
            return BaseResponse.error("群组ID不能为空");
        }
        return customerServiceChatMarkProvider.delete(request);
    }
}

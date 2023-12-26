package com.wanmi.sbc.imMessage;

import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceCommonMessageProvider;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceCommonMessageRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceCommonMessageGroupResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceCommonMessageResponse;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "CustomerServiceCommonMessageController", description = "客服常用消息聊天语API")
@RestController
@RequestMapping("/commonMessage")
@Slf4j
@Validated
public class CustomerServiceCommonMessageController {

    @Autowired
    private CustomerServiceCommonMessageProvider customerServiceCommonMessageProvider;

    @Autowired
    private CommonUtil commonUtil;

    @ApiOperation(value = "客服常用消息聊天语添加")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public BaseResponse addCustomerCommonMessage(@RequestBody CustomerServiceCommonMessageRequest messageRequest) {
        if (StringUtils.isEmpty(messageRequest.getMessage()) || messageRequest.getOneGroupId() == null) {
            return BaseResponse.error("参数错误");
        }
        if (messageRequest.getMessage().length() > 400) {
            return BaseResponse.error("消息太长");
        }
        try {
            messageRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
            messageRequest.setStoreId(commonUtil.getStoreId());
        }
        catch (Exception e) {}
        if (messageRequest.getCompanyInfoId() == null) {
            messageRequest.setCompanyInfoId(-1l);
            messageRequest.setStoreId(-1l);
        }
        return customerServiceCommonMessageProvider.addCustomerCommonMessage(messageRequest);
    }

    @ApiOperation(value = "客服常用消息聊天语修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public BaseResponse updateCustomerCommonMessage(@RequestBody CustomerServiceCommonMessageRequest messageRequest) {
        if (StringUtils.isEmpty(messageRequest.getMessage()) || messageRequest.getMsgId() == null) {
            return BaseResponse.error("参数错误");
        }
        if (messageRequest.getMessage().length() > 400) {
            return BaseResponse.error("消息太长");
        }
        setCompanyId(messageRequest);
        return customerServiceCommonMessageProvider.updateCustomerCommonMessage(messageRequest);
    }

    @ApiOperation(value = "客服常用消息聊天语列表")
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<CustomerServiceCommonMessageResponse>> getCustomerCommonMessageList(@RequestBody CustomerServiceCommonMessageRequest messageRequest){
        setCompanyId(messageRequest);
        return customerServiceCommonMessageProvider.getCustomerCommonMessageList(messageRequest);
    }

    private void setCompanyId(CustomerServiceCommonMessageRequest messageRequest) {
        try {
            messageRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
            messageRequest.setStoreId(commonUtil.getStoreId());
        }
        catch (Exception e) {}
        if (messageRequest.getCompanyInfoId() == null) {
            messageRequest.setCompanyInfoId(-1l);
            messageRequest.setStoreId(-1l);
        }
//        if (messageRequest.getCompanyInfoId() == null) {
//            messageRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
//        }
//        if (messageRequest.getStoreId() == null) {
//            messageRequest.setStoreId(commonUtil.getStoreId());
//        }
    }

    @ApiOperation(value = "客服常用消息聊天语删除")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public BaseResponse deleteCustomerCommonMessage(@RequestBody CustomerServiceCommonMessageRequest messageRequest) {
        if (messageRequest.getMsgId() == null) {
            return BaseResponse.error("参数错误");
        }
        return customerServiceCommonMessageProvider.deleteCustomerCommonMessage(messageRequest);
    }

    @ApiOperation(value = "客服常用消息聊天语搜索")
    @RequestMapping(value = "/searchMessage", method = RequestMethod.POST)
    public BaseResponse<List<CustomerServiceCommonMessageGroupResponse>> searchMessage (@RequestBody CustomerServiceCommonMessageRequest messageRequest) {
        setCompanyId(messageRequest);
        return customerServiceCommonMessageProvider.searchMessage(messageRequest);
    }

}

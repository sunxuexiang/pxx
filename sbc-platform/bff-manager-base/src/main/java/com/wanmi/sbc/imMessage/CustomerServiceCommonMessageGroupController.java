package com.wanmi.sbc.imMessage;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceCommonMessageGroupProvider;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceCommonMessageGroupRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceCommonMessageGroupResponse;
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

@Api(tags = "CustomerServiceCommonMessageGroupController", description = "客服常用消息聊天语分组API")
@RestController
@RequestMapping("/commonMessageGroup")
@Slf4j
@Validated
@Data
public class CustomerServiceCommonMessageGroupController {

    @Autowired
    private CustomerServiceCommonMessageGroupProvider customerServiceCommonMessageGroupProvider;

    @Autowired
    private CommonUtil commonUtil;

    @ApiOperation(value = "客服常用消息聊天语分组添加")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public BaseResponse addCustomerCommonMessageGroup(@RequestBody CustomerServiceCommonMessageGroupRequest messageRequest) {
        messageRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
        messageRequest.setStoreId(commonUtil.getStoreId());
        if (StringUtils.isEmpty(messageRequest.getGroupName())) {
            return BaseResponse.error("参数不能为空");
        }
        if (messageRequest.getGroupName().length() > 20) {
            return BaseResponse.error("名称太长");
        }
        return customerServiceCommonMessageGroupProvider.addCustomerCommonMessageGroup(messageRequest);
    }

    @ApiOperation(value = "客服常用消息聊天语分组刪除")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public BaseResponse deleteCustomerCommonMessageGroup(@RequestBody CustomerServiceCommonMessageGroupRequest messageRequest) {
        if (messageRequest.getGroupId() == null) {
            return BaseResponse.error("参数不能为空");
        }
        return customerServiceCommonMessageGroupProvider.deleteCustomerCommonMessageGroupById(messageRequest.getGroupId());
    }

    @ApiOperation(value = "客服常用消息聊天语分组查询全部")
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public BaseResponse<List<CustomerServiceCommonMessageGroupResponse>> getAllCustomerCommonMessageGroup(CustomerServiceCommonMessageGroupRequest messageRequest) {
        messageRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
        messageRequest.setStoreId(commonUtil.getStoreId());
        return customerServiceCommonMessageGroupProvider.getAllCustomerCommonMessageGroup(messageRequest);
    }

    @ApiOperation(value = "客服常用消息聊天语分组刪除")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public BaseResponse updateCustomerCommonMessageGroup(@RequestBody CustomerServiceCommonMessageGroupRequest messageRequest) {
        if (messageRequest.getGroupId() == null) {
            return BaseResponse.error("参数不能为空");
        }
        if (StringUtils.isEmpty(messageRequest.getGroupName())) {
            return BaseResponse.error("参数不能为空");
        }
        if (messageRequest.getGroupName().length() > 20) {
            return BaseResponse.error("名称太长");
        }
        return customerServiceCommonMessageGroupProvider.updateCustomerCommonMessageGroup(messageRequest);
    }
}

package com.wanmi.sbc.setting.imonlineservice.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceCommonMessageGroupRequest;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceCommonMessageRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceCommonMessageGroupResponse;
import com.wanmi.sbc.setting.imonlineservice.repository.CustomerServiceCommonMessageGroupRepository;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceCommonMessageGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>在线客服快捷回复常用语分组服务类</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@Service
public class CustomerServiceCommonMessageGroupService {

    @Autowired
    private CustomerServiceCommonMessageGroupRepository customerServiceCommonMessageGroupRepository;

    public CustomerServiceCommonMessageGroupResponse addCustomerCommonMessageGroup(CustomerServiceCommonMessageGroupRequest messageRequest) {
        CustomerServiceCommonMessageGroup group = new CustomerServiceCommonMessageGroup();
        KsBeanUtil.copyProperties(messageRequest, group);
        if (group.getParentGroupId() == null) {
            group.setParentGroupId(0l);
        }
        Integer maxSortNo = 0;
        if (messageRequest.getGroupLevel().equals(1)) {
            maxSortNo = customerServiceCommonMessageGroupRepository.getMaxSortNoByCompanyInfoId(messageRequest.getCompanyInfoId());
        }
        else {
            maxSortNo = customerServiceCommonMessageGroupRepository.getMaxSortNoByParentGroupId(messageRequest.getCompanyInfoId());
        }
        if (maxSortNo == null) maxSortNo = 0;
        group.setSortNum(++maxSortNo);
        customerServiceCommonMessageGroupRepository.save(group);
        return KsBeanUtil.convert(group, CustomerServiceCommonMessageGroupResponse.class);
    }

    public void deleteCustomerCommonMessageGroupById(Long groupId) {
        customerServiceCommonMessageGroupRepository.deleteById(groupId);
    }

    public List<CustomerServiceCommonMessageGroupResponse> getAllCustomerCommonMessageGroup(CustomerServiceCommonMessageGroupRequest messageRequest) {
        List<CustomerServiceCommonMessageGroup> list = customerServiceCommonMessageGroupRepository.findByCompanyInfoId(messageRequest.getCompanyInfoId());
        List<CustomerServiceCommonMessageGroupResponse> resultList = new ArrayList<>();
        if (ObjectUtils.isEmpty(list)) {
            return resultList;
        }
        List<CustomerServiceCommonMessageGroup> firstList = list.stream().filter(group -> group.getParentGroupId().equals(0l)).collect(Collectors.toList());
        firstList.forEach(group -> {
            CustomerServiceCommonMessageGroupResponse customerServiceCommonMessageGroupResponse = KsBeanUtil.convert(group, CustomerServiceCommonMessageGroupResponse.class);
            resultList.add(customerServiceCommonMessageGroupResponse);
            list.forEach(childGroup -> {
                if (!childGroup.getParentGroupId().equals(group.getGroupId())) {
                    return;
                }
                CustomerServiceCommonMessageGroupResponse subGroup = KsBeanUtil.convert(childGroup, CustomerServiceCommonMessageGroupResponse.class);
                customerServiceCommonMessageGroupResponse.getChildGroupList().add(subGroup);
            });
        });
        return resultList;
    }

    public boolean updateCustomerCommonMessageGroupById(CustomerServiceCommonMessageGroupRequest messageRequest) {
        CustomerServiceCommonMessageGroup customerServiceCommonMessageGroup = customerServiceCommonMessageGroupRepository.findById(messageRequest.getGroupId()).orElse(null);
        if (customerServiceCommonMessageGroup == null) {
            return false;
        }
        customerServiceCommonMessageGroup.setGroupName(messageRequest.getGroupName());
        if (messageRequest.getParentGroupId() != null) {
            customerServiceCommonMessageGroup.setParentGroupId(messageRequest.getParentGroupId());
        }
        if (customerServiceCommonMessageGroup.getParentGroupId() == null) {
            customerServiceCommonMessageGroup.setParentGroupId(0l);
        }
        customerServiceCommonMessageGroupRepository.save(customerServiceCommonMessageGroup);
        return true;
    }
}

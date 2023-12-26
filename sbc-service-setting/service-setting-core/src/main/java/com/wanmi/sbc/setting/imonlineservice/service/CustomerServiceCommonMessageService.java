package com.wanmi.sbc.setting.imonlineservice.service;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.setting.api.request.onlineserviceitem.CustomerServiceCommonMessageRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceCommonMessageGroupResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceCommonMessageResponse;
import com.wanmi.sbc.setting.imonlineservice.repository.CustomerServiceCommonMessageGroupRepository;
import com.wanmi.sbc.setting.imonlineservice.repository.CustomerServiceCommonMessageRepository;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceCommonMessage;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceCommonMessageGroup;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <p>客服快捷回复常用语服务类</p>
 * @author zhouzhenguo
 * @date 2023-08-31 16:10:28
 */
@Service
public class CustomerServiceCommonMessageService {

    @Autowired
    private CustomerServiceCommonMessageRepository customerServiceCommonMessageRepository;

    @Autowired
    private CustomerServiceCommonMessageGroupRepository customerServiceCommonMessageGroupRepository;

    public CustomerServiceCommonMessageResponse addCustomerCommonMessage(CustomerServiceCommonMessageRequest messageRequest) {
        CustomerServiceCommonMessage customerServiceCommonMessage = KsBeanUtil.convert(messageRequest, CustomerServiceCommonMessage.class);
        Integer maxSerNo = customerServiceCommonMessageRepository.findMaxSortNoByCompanyInfoId(messageRequest.getOneGroupId(), messageRequest.getSecondGroupId());
        if (maxSerNo == null) maxSerNo = 0;
        customerServiceCommonMessage.setSortNum(++maxSerNo);
        customerServiceCommonMessage.setUpdateTime(LocalDateTime.now());
        customerServiceCommonMessageRepository.save(customerServiceCommonMessage);
        return KsBeanUtil.convert(customerServiceCommonMessage, CustomerServiceCommonMessageResponse.class);
    }

    public boolean updateCustomerCommonMessage(CustomerServiceCommonMessageRequest messageRequest) {
        CustomerServiceCommonMessage dbMessage = customerServiceCommonMessageRepository.findById(messageRequest.getMsgId()).orElse(null);
        if (dbMessage == null) {
            return false;
        }
        dbMessage.setMessage(messageRequest.getMessage());
        dbMessage.setUpdateTime(LocalDateTime.now());
        customerServiceCommonMessageRepository.save(dbMessage);
        return true;
    }

    public MicroServicePage<CustomerServiceCommonMessageResponse> getCustomerCommonMessageList(CustomerServiceCommonMessageRequest messageRequest) {
        Page<CustomerServiceCommonMessage> messagePage = null;
        Sort sort = new Sort(Sort.Direction.ASC, "sort_num");
        Pageable pageable = PageRequest.of(messageRequest.getPageNum(), 10000, sort);
        if (messageRequest.getSecondGroupId() != null) {
            messagePage = customerServiceCommonMessageRepository.findBySecondGroupIdAndCompanyInfoId(messageRequest.getSecondGroupId(), messageRequest.getCompanyInfoId(), pageable);
        }
        else if (messageRequest.getOneGroupId() != null) {
            messagePage = customerServiceCommonMessageRepository.findByOneGroupIdAndCompanyInfoId(messageRequest.getOneGroupId(), messageRequest.getCompanyInfoId(), pageable);
        }
        else {
            messagePage = customerServiceCommonMessageRepository.findByCompanyInfoId(messageRequest.getCompanyInfoId(), pageable);
        }
        MicroServicePage<CustomerServiceCommonMessageResponse> microServicePage = KsBeanUtil.convertPage(messagePage, CustomerServiceCommonMessageResponse.class);
        if (ObjectUtils.isEmpty(microServicePage.getContent())) {
            return microServicePage;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        microServicePage.getContent().forEach(message -> message.setUpdateTimeStr(message.getUpdateTime().format(dateTimeFormatter)));
        return microServicePage;
    }

    public void deleteCustomerCommonMessageById(Long msgId) {
        customerServiceCommonMessageRepository.deleteById(msgId);
    }

    public List<CustomerServiceCommonMessageGroupResponse> searchMessage(CustomerServiceCommonMessageRequest messageRequest) {
        List<CustomerServiceCommonMessage> messageList = customerServiceCommonMessageRepository.findAll(getWhereCriteria(messageRequest));
        if (ObjectUtils.isEmpty(messageList)) {
            return new ArrayList<>();
        }
        List<CustomerServiceCommonMessageGroupResponse> resultList = new ArrayList<>();
        Map<Long, CustomerServiceCommonMessageGroupResponse> groupMap = new HashMap<>();
        messageList.forEach(msg -> {
            CustomerServiceCommonMessageGroupResponse group = groupMap.get(msg.getOneGroupId());
            if (group == null) {
                CustomerServiceCommonMessageGroup dbGroup = customerServiceCommonMessageGroupRepository.findById(msg.getOneGroupId()).orElse(null);
                if (dbGroup == null) {
                    return;
                }
                group = KsBeanUtil.convert(dbGroup, CustomerServiceCommonMessageGroupResponse.class);
                group.setMessage(group.getGroupName());
                groupMap.put(msg.getOneGroupId(), group);
                resultList.add(group);
            }
            group.getMessageList().add(KsBeanUtil.convert(msg, CustomerServiceCommonMessageResponse.class));
        });
        return resultList;
    }

    public Specification<CustomerServiceCommonMessage> getWhereCriteria(CustomerServiceCommonMessageRequest request) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(request.getMessage())) {
                predicates.add(cbuild.like(root.get("message"), buildLike(request.getMessage())));
            }
            if (!ObjectUtils.isEmpty(request.getCompanyInfoId())) {
                predicates.add(cbuild.equal(root.get("companyInfoId"), request.getCompanyInfoId()));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

    private static String buildLike(String field) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.append("%").append(XssUtils.replaceLikeWildcard(field)).append("%").toString();
    }
}

package com.wanmi.sbc.customer.provider.impl.log;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.customer.api.provider.log.CustomerLogProvider;
import com.wanmi.sbc.customer.api.request.log.CustomerLogAddRequest;
import com.wanmi.sbc.customer.api.request.log.CustomerLogPageRequest;
import com.wanmi.sbc.customer.api.response.log.CustomerLogPageResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerLogVO;
import com.wanmi.sbc.customer.log.model.root.CustomerLog;
import com.wanmi.sbc.customer.log.request.CustomerLogQueryRequest;
import com.wanmi.sbc.customer.log.service.CustomerLogService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description  
 * @author  shiy
 * @date    2023/4/7 11:22
 * @params  
 * @return  
*/
@RestController
@Validated
public class CustomerLogController implements CustomerLogProvider {
    @Autowired
    private CustomerLogService customerLogService;

    @Override
    public BaseResponse<CustomerLogPageResponse> page(CustomerLogPageRequest customerLogPageRequest) {
        CustomerLogQueryRequest queryRequest = new CustomerLogQueryRequest();
        KsBeanUtil.copyPropertiesThird(customerLogPageRequest, queryRequest);
        if(StringUtils.isNotBlank(customerLogPageRequest.getCreateTimeBegin())) {
            queryRequest.setCreateTimeBegin(DateUtil.parse(customerLogPageRequest.getCreateTimeBegin(), DateUtil.FMT_TIME_1));
        }
        if(StringUtils.isNotBlank(customerLogPageRequest.getCreateTimeEnd())) {
            queryRequest.setCreateTimeEnd(DateUtil.parse(customerLogPageRequest.getCreateTimeEnd(), DateUtil.FMT_TIME_1));
        }
        queryRequest.setLogType(1);
        setDefaultQueryTime(queryRequest);
        Page<CustomerLog> levelPage = customerLogService.page(queryRequest);
        List<CustomerLogVO> voList = wrapperVos(levelPage.getContent());
        if(CollectionUtils.isNotEmpty(voList)) {
            List<CustomerLog> versionUpdateLogList = customerLogService.findAllVersionRecord(queryRequest);
            if (CollectionUtils.isNotEmpty(versionUpdateLogList)) {
                for (CustomerLog versionUpdateLog : versionUpdateLogList) {
                    List<CustomerLogVO> logListByUserNo = voList.stream().filter(l
                            -> l.getUserNo().equals(versionUpdateLog.getUserNo())
                            &&l.getCreateTime().compareTo(versionUpdateLog.getCreateTime())>-1
                            &&null!=l.getAppType()&&l.getAppType().equals(versionUpdateLog.getAppType())
                            &&null!=l.getAppVersion()&&l.getAppVersion().equals(versionUpdateLog.getAppVersion())
                    ).collect(Collectors.toList());
                    if(CollectionUtils.isNotEmpty(logListByUserNo)){
                        logListByUserNo.forEach(l->{
                            l.setVersionUpdateTime(versionUpdateLog.getCreateTime());
                        });
                    }
                }
            }
        }
        CustomerLogPageResponse response = CustomerLogPageResponse.builder()
                .customerLogVOList(new MicroServicePage<>(voList, queryRequest.getPageable(), levelPage.getTotalElements()))
                .build();
        return BaseResponse.success(response);
    }

    private static void setDefaultQueryTime(CustomerLogQueryRequest queryRequest) {
        if(null== queryRequest.getCreateTimeBegin()){
            queryRequest.setCreateTimeBegin(LocalDateTime.of(2023,4,1,0,0,0));
        }
        if(null== queryRequest.getCreateTimeEnd()){
            queryRequest.setCreateTimeEnd(LocalDateTime.now());
        }
    }

    @Override
    public BaseResponse<CustomerLogPageResponse> findUpdateRecordByUserNo(CustomerLogPageRequest customerLogPageRequest) {
        CustomerLogQueryRequest queryRequest = new CustomerLogQueryRequest();
        KsBeanUtil.copyPropertiesThird(customerLogPageRequest, queryRequest);
        queryRequest.setLogType(2);
        Page<CustomerLog> levelPage = customerLogService.page(queryRequest);
        List<CustomerLogVO> voList = wrapperVos(levelPage.getContent());
        CustomerLogPageResponse response = CustomerLogPageResponse.builder()
                .customerLogVOList(new MicroServicePage<>(voList, queryRequest.getPageable(), levelPage.getTotalElements()))
                .build();
        return BaseResponse.success(response);
    }

    private List<CustomerLogVO> wrapperVos(List<CustomerLog> customerLogs) {
        if (CollectionUtils.isNotEmpty(customerLogs)) {
            return customerLogs.stream().map(customerLog -> {
                CustomerLogVO vo = new CustomerLogVO();
                KsBeanUtil.copyPropertiesThird(customerLog, vo);
                return vo;
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public BaseResponse add(CustomerLogAddRequest request) {
        CustomerLog customerLog = new CustomerLog();
        KsBeanUtil.copyPropertiesThird(request, customerLog);
        customerLog.setCreateTime(LocalDateTime.now());;
        customerLogService.add(customerLog);
        return BaseResponse.SUCCESSFUL();
    }
}


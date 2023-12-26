package com.wanmi.sbc.customer.log.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.log.model.root.CustomerLog;
import com.wanmi.sbc.customer.log.repository.CustomerLogRepository;
import com.wanmi.sbc.customer.log.request.CustomerLogQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>客户日志业务逻辑</p>
 *
 * @author shiy
 * @date 2019-02-27 19:51:30
 */
@Service("CustomerLogService")
public class CustomerLogService {
    @Autowired
    private CustomerLogRepository customerLogRepository;

    /**
     * 新增客户日志
     *
     * @author shiy
     */
    @Transactional
    public CustomerLog add(CustomerLog entity) {
        CustomerLog lastestRecord = customerLogRepository.findLatestLoginRecord(entity.getUserNo(), entity.getAppType());
        customerLogRepository.save(entity);
        if (null == lastestRecord || (lastestRecord.getLogType() != 2 && !entity.getAppVersion().equals(lastestRecord.getAppVersion()))) {
            CustomerLog newRecord = new CustomerLog();
            KsBeanUtil.copyPropertiesThird(entity, newRecord);
            newRecord.setId(null);
            newRecord.setLogType(2);
            customerLogRepository.save(newRecord);
        }
        return entity;
    }

    /**
     * 修改客户日志
     *
     * @author shiy
     */
    @Transactional
    public CustomerLog modify(CustomerLog entity) {
        CustomerLog save = customerLogRepository.save(entity);
        return save;
    }

    @Transactional
    public void modifyList(List<CustomerLog> CustomerLogs){
        customerLogRepository.saveAll(CustomerLogs);
    }

    /**
     * 单个删除客户日志
     *
     * @author shiy
     */
    @Transactional
    public void deleteById(Long CustomerLogId) {
        
    }

    /**
     * 单个查询客户日志
     *
     * @author shiy
     */
    public CustomerLog getById(Long CustomerLogId) {
        return customerLogRepository.findById(CustomerLogId).orElse(null);
    }



    /**
     * 列表查询客户日志
     *
     * @author shiy
     */
    public List<CustomerLog> list(CustomerLogQueryRequest queryReq) {
        return null;
    }


    /**
     * 分页
     *
     * @return
     */
    public Page<CustomerLog> page(CustomerLogQueryRequest customerLogQueryRequest) {
        return customerLogRepository.findAll(customerLogQueryRequest.getWhereCriteria(),
                customerLogQueryRequest.getPageRequest());
    }

    /**
     * 版本更新日期
     *
     * @return
     */
    public List<CustomerLog> findAllVersionRecord(CustomerLogQueryRequest queryRequest) {
        return customerLogRepository.findAllVersionUpdateLog(queryRequest.getCreateTimeBegin(),queryRequest.getCreateTimeEnd());
    }
}

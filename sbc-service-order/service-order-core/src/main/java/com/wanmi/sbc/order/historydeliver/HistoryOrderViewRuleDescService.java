package com.wanmi.sbc.order.historydeliver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryOrderViewRuleDescService {

    @Autowired
    private HistoryOrderViewRuleDescRepository historyOrderViewRuleDescRepository;

    public Long save(HistoryOrderViewRuleDesc bean) {
        int count = getCountByConsigneeIdAndApiId(bean);
        if(count>=3){
            return 0L;
        }
        bean = historyOrderViewRuleDescRepository.save(bean);
        return bean.getId();
    }

    public Integer getCountByConsigneeIdAndApiId(HistoryOrderViewRuleDesc bean){
        return historyOrderViewRuleDescRepository.getCountByConsigneeIdAndApiId(bean.getConsigneeId(),bean.getDeliverWay(),bean.getApiId());
    }
}

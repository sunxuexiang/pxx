package com.wanmi.sbc.returnorder.returnorder.service;

import com.wanmi.sbc.returnorder.api.request.returnorder.ReturnLogisticsRequest;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrderLogistics;
import com.wanmi.sbc.returnorder.returnorder.repository.ReturnOrderLogisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReturnOrderLogisticsService {

    @Autowired
    private ReturnOrderLogisticsRepository returnOrderLogisticsRepository;

    public ReturnOrderLogistics findReturnLogisticsByHistory(ReturnLogisticsRequest request) {
        return returnOrderLogisticsRepository.findByStoreIdAndCustomerIdOrderByCreateTimeDesc(request.getStoreId(), request.getCustomerId())
                .stream().findFirst().orElse(null);
    }
}

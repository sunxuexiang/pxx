package com.wanmi.sbc.pay.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.pay.model.root.CcbRefundRetry;
import com.wanmi.sbc.pay.repository.CcbRefundRetryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/8/10 10:05
 */
@Slf4j
@Service
public class CcbRefundRetryService {

    @Autowired
    private CcbRefundRetryRepository ccbRefundRetryRepository;


    @Async
    public void save(CcbRefundRetry ccbRefundRetry) {
        CcbRefundRetry retry = KsBeanUtil.convert(ccbRefundRetry, CcbRefundRetry.class);
        ccbRefundRetryRepository.saveAndFlush(retry);
    }

    public Boolean existRetryByRid(String refundBusinessId) {
        return ccbRefundRetryRepository.existsByRid(refundBusinessId);
    }

}

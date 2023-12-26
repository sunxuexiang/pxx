package com.wanmi.sbc.pay.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.pay.model.root.CcbLog;
import com.wanmi.sbc.pay.repository.CcbLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/7/29 9:32
 */
@Slf4j
@Service
public class CcbLogService {

    @Autowired
    private CcbLogRepository ccbLogRepository;

    @Async
    public void saveLog(CcbLog ccbLog) {
        CcbLog log = KsBeanUtil.convert(ccbLog, CcbLog.class);
        ccbLogRepository.saveAndFlush(log);
    }
}

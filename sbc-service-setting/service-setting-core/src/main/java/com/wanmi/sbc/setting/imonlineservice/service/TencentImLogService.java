package com.wanmi.sbc.setting.imonlineservice.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.imonlineservice.TencentImLogRequest;
import com.wanmi.sbc.setting.imonlineservice.repository.TencentImLogRepository;
import com.wanmi.sbc.setting.imonlineservice.root.TencentImLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TencentImLogService {

    @Autowired
    private TencentImLogRepository tencentImLogRepository;

    public void add(TencentImLogRequest request) {
        TencentImLog tencentImLog = KsBeanUtil.convert(request, TencentImLog.class);
        tencentImLogRepository.save(tencentImLog);
    }
}

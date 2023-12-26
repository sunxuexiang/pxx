package com.wanmi.sbc.returnorder.trade.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.bean.dto.PushFailLogDTO;
import com.wanmi.sbc.returnorder.trade.model.root.PushFailLog;
import com.wanmi.sbc.returnorder.trade.repository.PushWmsFailLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author lm
 * @date 2022/11/19 16:13
 */
@Service
@Slf4j
@Transactional
public class PushWmsFailLogService {


    @Autowired
    private PushWmsFailLogRepository pushWmsFailLogRepository;

    public LocalDateTime findLastPushTime() {
        String lastPushTime = pushWmsFailLogRepository.findLastPushTime();
        if(StringUtils.isNotBlank(lastPushTime)){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(lastPushTime,formatter);
        }
        return null;
    }

    public void savePushFailLog(List<PushFailLogDTO> pushFailLogDTOList) {
        List<PushFailLog> pushFailLogs = KsBeanUtil.convert(pushFailLogDTOList, PushFailLog.class);
        pushFailLogs.forEach(pushFailLog -> pushWmsFailLogRepository.saveAndFlush(pushFailLog));
    }
}

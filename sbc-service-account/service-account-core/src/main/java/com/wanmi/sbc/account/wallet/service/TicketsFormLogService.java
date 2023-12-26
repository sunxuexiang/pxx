package com.wanmi.sbc.account.wallet.service;

import com.wanmi.sbc.account.api.request.wallet.TicketsFormLogRequest;
import com.wanmi.sbc.account.bean.vo.TicketsFormLogVo;
import com.wanmi.sbc.account.wallet.model.root.TicketsFormLog;
import com.wanmi.sbc.account.wallet.repository.TicketsFormLogRepository;
import com.wanmi.sbc.common.util.KsBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 提现申请日志
 */
@Service
@Slf4j
public class TicketsFormLogService {

    @Autowired
    public TicketsFormLogRepository ticketsFormLogRepository;

    public List<TicketsFormLogVo> listByBusinessIds(TicketsFormLogRequest ticketsFormLogRequest){
        List<TicketsFormLog> ticketsFormLogs = ticketsFormLogRepository.findByBusinessIdIn(ticketsFormLogRequest.getBusinessIds());
        List<TicketsFormLogVo> convert = KsBeanUtil.convert(ticketsFormLogs, TicketsFormLogVo.class);
        return convert;
    }

    public List<TicketsFormLogVo> findByAuditTimeBetween(TicketsFormLogRequest ticketsFormLogRequest){
        List<TicketsFormLog> ticketsFormLogs = ticketsFormLogRepository.findByAuditTimeBetween(ticketsFormLogRequest.getAuditTimeStart(),ticketsFormLogRequest.getAuditTimeEnd());
        if(ticketsFormLogRequest.getAuditStaffType() != null){
            ticketsFormLogs = ticketsFormLogs.stream().filter(item -> item.getAuditStaffType() == ticketsFormLogRequest.getAuditStaffType()).collect(Collectors.toList());
        }
        List<TicketsFormLogVo> convert = KsBeanUtil.convert(ticketsFormLogs, TicketsFormLogVo.class);
        return convert;
    }

    /**
     * 添加提现审核日志
     * @param ticketsFormLogRequest
     * @return
     */
    public TicketsFormLog save(TicketsFormLogRequest ticketsFormLogRequest){
        TicketsFormLog ticketsFormLog = new TicketsFormLog();
        KsBeanUtil.copyProperties(ticketsFormLogRequest,ticketsFormLog);
        ticketsFormLog.setAuditTime(LocalDateTime.now());
        return ticketsFormLogRepository.save(ticketsFormLog);
    }
}

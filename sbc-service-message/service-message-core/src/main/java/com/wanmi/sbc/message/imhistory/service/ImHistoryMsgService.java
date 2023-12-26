package com.wanmi.sbc.message.imhistory.service;

import com.wanmi.sbc.message.imhistory.repository.ImHistoryMsgRepository;
import com.wanmi.sbc.message.imhistory.root.ImHistory;
import com.wanmi.sbc.message.imhistory.root.ImHistoryMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * <p>im历史</p>
 * @author sgy
 * @date 2023-07-03 15:49:24
 */
@Service("ImHistoryMsgService")
public class ImHistoryMsgService {
    @Autowired
    private ImHistoryMsgRepository imHistoryMsgRepository;

    @Transactional(rollbackFor = Exception.class)
    public ImHistoryMsg add(ImHistoryMsg entity) {
        ImHistoryMsg save = null;
        if (!Objects.isNull(entity)){
            save  = imHistoryMsgRepository.save(entity);
        }
        return save;
    }
}

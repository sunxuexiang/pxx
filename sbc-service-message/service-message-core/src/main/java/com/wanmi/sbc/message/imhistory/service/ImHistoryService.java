package com.wanmi.sbc.message.imhistory.service;

import com.wanmi.sbc.message.imhistory.repository.ImHistoryMsgRepository;
import com.wanmi.sbc.message.imhistory.repository.ImHistoryRepository;
import com.wanmi.sbc.message.imhistory.root.ImHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * <p>im历史</p>
 * @author sgy
 * @date 2023-07-03 15:49:24
 */
@Service("ImHistoryService")
public class ImHistoryService {
    @Autowired
    private ImHistoryRepository imHistoryRepository;
    @Transactional(rollbackFor = Exception.class)
    public ImHistory add(ImHistory entity) {
        ImHistory save = null;
        if (!Objects.isNull(entity)){
            save  = imHistoryRepository.save(entity);
        }
        return save;
    }
}

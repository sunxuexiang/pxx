package com.wanmi.sbc.live.stream.service;

import com.wanmi.sbc.live.stream.dao.LiveLanguageMapper;
import com.wanmi.sbc.live.stream.model.root.LiveLanguage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LiveLanguageService {

    @Autowired
    private LiveLanguageMapper liveLanguageMapper;

    public List<LiveLanguage> getAll() {
        return liveLanguageMapper.getAll();
    }
}

package com.wanmi.sbc.live.stream.dao;

import com.wanmi.sbc.live.stream.model.root.LiveLanguage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiveLanguageMapper {

    List<LiveLanguage> getAll();
}

package com.wanmi.sbc.setting.headline.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.headline.HeadLineSaveRequest;
import com.wanmi.sbc.setting.headline.model.root.HeadlineConfig;
import com.wanmi.sbc.setting.headline.repository.HeadlineConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HeadlineConfigService {

    @Autowired
    private HeadlineConfigRepository headlineConfigRepository;

    @Transactional(rollbackFor = Exception.class)
    public void save(List<HeadLineSaveRequest> request) {
        headlineConfigRepository.deleteAll();
        if (CollectionUtils.isEmpty(request)) {
            return;
        }
        List<HeadlineConfig> headlineConfigs = KsBeanUtil.convertList(request, HeadlineConfig.class);
        for (HeadlineConfig conf : headlineConfigs) {
            if (Objects.isNull(conf.getSpeeds())) {
                conf.setSpeeds(new BigDecimal("0.452"));
            }
        }
        headlineConfigRepository.saveAll(headlineConfigs);
    }

    public List<HeadlineConfig> findAllHeadline() {
        List<HeadlineConfig> all = headlineConfigRepository.findAll();
        return all.stream().sorted(Comparator.comparingInt(HeadlineConfig::getSortNum)).collect(Collectors.toList());
    }
}

package com.wanmi.sbc.setting.headline.repository;

import com.wanmi.sbc.setting.headline.model.root.HeadlineConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface HeadlineConfigRepository extends JpaRepository<HeadlineConfig, String>, JpaSpecificationExecutor<HeadlineConfig> {

}
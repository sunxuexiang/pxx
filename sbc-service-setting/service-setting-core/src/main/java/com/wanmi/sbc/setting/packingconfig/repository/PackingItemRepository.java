package com.wanmi.sbc.setting.packingconfig.repository;

import com.wanmi.sbc.setting.packingconfig.model.root.PackingConfigItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackingItemRepository extends JpaRepository<PackingConfigItem, Integer>,
        JpaSpecificationExecutor<PackingConfigItem> {

}

package com.wanmi.sbc.setting.retaildeliveryconfig.repository;

import com.wanmi.sbc.setting.packingconfig.model.root.PackingConfigItem;
import com.wanmi.sbc.setting.retaildeliveryconfig.model.root.RetailDeliveryConfigItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RetailDeliveryConfigRepository extends JpaRepository<RetailDeliveryConfigItem, Integer>,
        JpaSpecificationExecutor<RetailDeliveryConfigItem> {

}

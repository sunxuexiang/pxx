package com.wanmi.sbc.shopcart.purchase.repository;

import com.wanmi.sbc.shopcart.purchase.model.ProcurementConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 采购车配置
 *
 * @author yitang
 * @version 1.0
 */
public interface ProcurementConfigRepository extends JpaRepository<ProcurementConfig, Long>, JpaSpecificationExecutor<ProcurementConfig> {
}

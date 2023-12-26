package com.wanmi.sbc.pay.repository;

import com.wanmi.sbc.pay.bean.enums.PayGatewayEnum;
import com.wanmi.sbc.pay.model.root.PayGatewayConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sunkun on 2017/8/3.
 */
@Repository
public interface GatewayConfigRepository extends JpaRepository<PayGatewayConfig, Long> {

    @Query("select p from PayGatewayConfig p where p.payGateway.id = ?1 and p.storeId = ?2 ")
    PayGatewayConfig queryConfigByGatwayIdAndStoreId(Long gatwayId,Long storeId);

    @Query("select p from PayGatewayConfig p where p.payGateway.isOpen = 1 and p.storeId = ?1")
    List<PayGatewayConfig> queryConfigByOpenAndStoreId(Long storeId);

    @Query("select p from PayGatewayConfig p where p.payGateway.name=?1  and p.storeId = ?2 ")
    PayGatewayConfig queryConfigByNameAndStoreId(PayGatewayEnum payGatewayEnum,Long storeId);

    // todo 退款改造完 queryConfigByName 用 queryConfigByNameAndStoreId 代替
    @Query("select p from PayGatewayConfig p where p.payGateway.name=?1")
    PayGatewayConfig queryConfigByName(PayGatewayEnum payGatewayEnum);

}

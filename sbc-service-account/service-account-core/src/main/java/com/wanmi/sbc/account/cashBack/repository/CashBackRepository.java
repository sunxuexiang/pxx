package com.wanmi.sbc.account.cashBack.repository;
import java.util.List;

import com.wanmi.sbc.account.cashBack.model.root.CashBack;
import com.wanmi.sbc.account.funds.model.root.CustomerFunds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
/**
 * @description 返现打款表单
 * @author drs
 * @date 2022-09-02
 */
@Repository
public interface CashBackRepository extends JpaRepository<CashBack, String>, JpaSpecificationExecutor<CashBack> {


    @Query("update CashBack c set c.returnStatus =:returnStatus where  c.id =:id")
    @Modifying
    int updateCashBackByIds(@Param("id") Integer id,@Param("returnStatus") Integer returnStatus);
}

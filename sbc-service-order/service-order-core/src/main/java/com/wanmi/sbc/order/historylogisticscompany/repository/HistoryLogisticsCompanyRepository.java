package com.wanmi.sbc.order.historylogisticscompany.repository;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.order.historylogisticscompany.model.root.HistoryLogisticsCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.wanmi.sbc.common.enums.DeleteFlag;
import java.util.Optional;
import java.util.List;

/**
 * <p>物流公司历史记录DAO</p>
 * @author fcq
 * @date 2020-11-09 17:32:23
 */
@Repository
public interface HistoryLogisticsCompanyRepository extends JpaRepository<HistoryLogisticsCompany, String>,
        JpaSpecificationExecutor<HistoryLogisticsCompany> {

    /**
     * 单个删除物流公司历史记录
     * @author fcq
     */
    @Override
    @Modifying
    @Query("update HistoryLogisticsCompany set delFlag = 1 where id = ?1")
    void deleteById(String id);

    /**
     * 批量删除物流公司历史记录
     * @author fcq
     */
    @Modifying
    @Query("update HistoryLogisticsCompany set delFlag = 1 where id in ?1")
    void deleteByIdList(List<String> idList);

    Optional<HistoryLogisticsCompany> findByIdAndDelFlag(String id, DeleteFlag delFlag);

    /**
     *
     * @param customerId
     * @return
     */
    @Query(value ="select * from history_logistics_company where del_flag = 0 and self_flag =0 and customer_id = ?1  " +
            "order by create_time desc limit 1 ", nativeQuery = true)
    HistoryLogisticsCompany findByByCustomerId(String customerId );

    @Query(value ="select * from history_logistics_company where del_flag = 0 and self_flag =0 and customer_id = ?1 and logistics_type=?2 " +
            "order by create_time desc limit 1 ", nativeQuery = true)
    HistoryLogisticsCompany findByByCustomerId(String customerId,Integer logisticsType);

    @Query(value ="select * from history_logistics_company where del_flag = 0 and self_flag =0 and customer_id = ?1 and logistics_type=?2 and market_id=?3 " +
            "order by create_time desc limit 1 ", nativeQuery = true)
    HistoryLogisticsCompany findByByCustomerIdAndMarketId(String customerId,Integer logisticsType,Long marketId);
}

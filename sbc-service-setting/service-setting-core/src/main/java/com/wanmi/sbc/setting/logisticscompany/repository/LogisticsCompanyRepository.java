package com.wanmi.sbc.setting.logisticscompany.repository;

import com.wanmi.sbc.setting.logisticscompany.model.root.LogisticsCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.wanmi.sbc.common.enums.DeleteFlag;
import java.util.Optional;
import java.util.List;

/**
 * <p>物流公司DAO</p>
 * @author fcq
 * @date 2020-11-06 13:37:51
 */
@Repository
public interface LogisticsCompanyRepository extends JpaRepository<LogisticsCompany, Long>,
        JpaSpecificationExecutor<LogisticsCompany> {

    /**
     * 批量删除物流公司
     * @author fcq
     */
    @Modifying
    @Query("update LogisticsCompany set delFlag = 1 where id in ?1")
    void deleteByIdList(List<Long> idList);

    Optional<LogisticsCompany> findByIdAndDelFlag(Long id, DeleteFlag delFlag);

    @Query("SELECT max(id) FROM LogisticsCompany")
    Long selectMaxId();

    /**
     * 查询数据库是否存在
     * @param logisticsCompanyNumber
     * @return
     */
    @Query("SELECT max(id) FROM LogisticsCompany where delFlag=0 and  companyNumber = ?1 and storeId= ?2 and logisticsType=?3")
    Long selectLogisticsCompanyNumber(String logisticsCompanyNumber,Long storeId,Integer logisticsType);

    @Query("SELECT max(id) FROM LogisticsCompany where delFlag=0 and  logisticsName = ?1 and storeId= ?2 and logisticsType=?3")
    Long selectLogisticsName(String logisticsName,Long storeId,Integer logisticsType);

    /**
     * 查询数据库是否存在
     * @param logisticsCompanyNumber
     * @return
     */
    @Query("SELECT max(id) FROM LogisticsCompany where delFlag=0 and  companyNumber = ?1 and marketId= ?2 and logisticsType=?3")
    Long selectLogisticsCompanyNumberByMarketId(String logisticsCompanyNumber,Long marketId,Integer logisticsType);

    @Query("SELECT max(id) FROM LogisticsCompany where delFlag=0 and  logisticsName = ?1 and marketId= ?2 and logisticsType=?3")
    Long selectLogisticsNameByMarketId(String logisticsName,Long marketId,Integer logisticsType);

    @Query("FROM LogisticsCompany where delFlag=0 and  logisticsName = ?1 and marketId= ?2 and logisticsType=?3")
    List<LogisticsCompany> selectLogisticsListByMarketIdAndLogisticsName(String logisticsName,Long marketId,Integer logisticsType);

    /**
     * 根据物流公司编号查询数据库是否存在
     * @param
     * @return
     */
    @Query("select companyNumber FROM LogisticsCompany")
    List<String> selectLogisticsCompanyNumbers();

    @Query("select logisticsName FROM LogisticsCompany")
    List<String> selectLogisticsNames();

    /**
     * 根据物流公司编号查询数据库是否存在
     * @param
     * @return
     */
    @Query("select companyNumber FROM LogisticsCompany where delFlag=0 and  storeId= ?1 and logisticsType=?2")
    List<String> selectLogisticsCompanyNumbers(Long storeId,Integer logisticsType);

    @Query("select logisticsName FROM LogisticsCompany where delFlag=0 and  storeId= ?1 and logisticsType=?2")
    List<String> selectLogisticsNames(Long storeId,Integer logisticsType);

    /**
     * 根据物流公司编号查询数据库是否存在
     * @param
     * @return
     */
    @Query("select companyNumber FROM LogisticsCompany where delFlag=0 and  marketId= ?1 and logisticsType=?2")
    List<String> selectLogisticsNumbersByMarketId(Long marketId,Integer logisticsType);

    @Query("select logisticsName FROM LogisticsCompany where delFlag=0 and  marketId= ?1 and logisticsType=?2")
    List<String> selectLogisticsNamesByMarketId(Long marketId,Integer logisticsType);
}

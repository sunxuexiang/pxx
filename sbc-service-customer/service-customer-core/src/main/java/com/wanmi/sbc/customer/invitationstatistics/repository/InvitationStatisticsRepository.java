package com.wanmi.sbc.customer.invitationstatistics.repository;

import com.wanmi.sbc.customer.invitationstatistics.model.root.InvitationStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * <p>邀新统计DAO</p>
 * @author lvheng
 * @date 2021-04-23 10:57:45
 */
@Repository
public interface InvitationStatisticsRepository extends JpaRepository<InvitationStatistics, String>,
        JpaSpecificationExecutor<InvitationStatistics> {


    /**
     * 根据ID查询
     * @param employeeId
     * @param data
     * @return
     */
    @Query(value = "select * from invitation_statistics ist where ist.employee_id = ?1 and ist.date = ?2 ",nativeQuery = true)
    Optional<InvitationStatistics> getById(String employeeId, String data);

    /**
     * 根据id查询信息是否存在
     * @param employeeId
     * @param date
     * @return
     */
    @Query(value = "select count(1) from InvitationStatistics where employeeId = ?1 and date = ?2")
    Long getCountById(String employeeId,String date);

    /**
     * 根据id查询
     * @param employeeId
     * @param data
     * @return
     */
    @Query(value = "from InvitationStatistics ist where ist.employeeId = ?1 and ist.date = ?2 ")
    InvitationStatistics getByEmployeeIdAndDate(String employeeId, String data);

    /**
     * 指定ID的注册数+1
     * @param employeeId
     * @param data
     */
    @Modifying
    @Query(value = "update invitation_statistics ist set ist.results_count = results_count +1 where ist.employee_id = ?1 and" +
            " ist.date = ?2",nativeQuery = true)
    void updateCount(String employeeId, String data);

    /**
     * 指定的id增加订单统计信息
     * @param employeeId
     * @param data
     * @param tracePrice
     * @param goodsCount
     */
    @Modifying
    @Query(value = "update invitation_statistics ist set " +
            "ist.trade_price_total = ist.trade_price_total + ?3, " +
            "ist.trade_goods_total = ist.trade_goods_total + ?4, " +
            "ist.trade_total = ist.trade_total + 1" +
            " where ist.employee_Id = ?1 and ist.date = ?2",nativeQuery = true)
    void updateTradeInfo(String employeeId, String data, BigDecimal tracePrice, Long goodsCount);

    @Query(value ="SELECT * FROM invitation_statistics WHERE DATE_FORMAT(date, '%Y%m' ) = DATE_FORMAT(CURDATE(), " +
            "'%Y%m') AND employee_id = ?1",nativeQuery = true)
    List<InvitationStatistics> getMonthByEmployeeId(String employeeId);


    @Query(value ="SELECT * FROM invitation_statistics WHERE date = ?1",nativeQuery = true)
    List<InvitationStatistics> getToday(String date);
}

package com.wanmi.sbc.pay.repository;

import com.wanmi.sbc.pay.model.root.PayWithDraw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lm
 * @date 2022/10/21 9:33
 */
@Repository
public interface PayWithDrawRepository extends JpaRepository<PayWithDraw, Integer>, JpaSpecificationExecutor<PayWithDraw> {

    /**
     * 查询所有付款账户-鲸币提现
     * @return
     */
    @Query("from PayWithDraw pw where pw.delFlag = 0")
    List<PayWithDraw> findAllWithDraw();
}

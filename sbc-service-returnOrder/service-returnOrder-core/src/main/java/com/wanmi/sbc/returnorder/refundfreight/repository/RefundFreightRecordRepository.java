package com.wanmi.sbc.returnorder.refundfreight.repository;

import com.wanmi.sbc.returnorder.refundfreight.model.root.RefundFreightRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/30 14:13
 */
@Repository
public interface RefundFreightRecordRepository extends JpaRepository<RefundFreightRecord,Long>, JpaSpecificationExecutor<RefundFreightRecord> {
    RefundFreightRecord findByRid(String rid);
    RefundFreightRecord findByTid(String tid);

}

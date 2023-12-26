package com.wanmi.sbc.wms.pushwmslog.repository;

import com.wanmi.sbc.wms.pushwmslog.model.root.PushWmsLog;
import com.wanmi.sbc.wms.record.model.root.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PushWmsLogRepository extends JpaRepository<PushWmsLog, Long>,
        JpaSpecificationExecutor<PushWmsLog> {

    @Query(value = "select * from push_wms_log t1 where t1.statues != 1",nativeQuery = true)
    List<PushWmsLog> getErrorDate();

}

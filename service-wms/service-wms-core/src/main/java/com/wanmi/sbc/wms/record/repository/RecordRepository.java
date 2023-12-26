package com.wanmi.sbc.wms.record.repository;

import com.wanmi.sbc.wms.record.model.root.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * <p>请求记录DAO</p>
 * @author baijz
 * @date 2020-05-06 19:23:45
 */
@Repository
public interface RecordRepository extends JpaRepository<Record, Long>,
        JpaSpecificationExecutor<Record> {

}

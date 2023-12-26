package com.wanmi.sbc.goods.info.repository;

import com.wanmi.sbc.goods.info.model.root.GoodsInfoPresellRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsInfoPresellRecordRepository extends JpaRepository<GoodsInfoPresellRecord, String>,JpaSpecificationExecutor<GoodsInfoPresellRecord>{

}

package com.wanmi.sbc.customer.print.repository;

import com.wanmi.sbc.customer.print.model.root.PrintSetting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrintSettingRepository extends MongoRepository<PrintSetting, String> {

    PrintSetting queryByStoreId(Long storeId);

}
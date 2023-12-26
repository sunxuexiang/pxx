package com.wanmi.sbc.account.wallet.repository;

import com.wanmi.sbc.account.wallet.model.root.BlanceSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BlanceSettingReposiyory extends JpaRepository<BlanceSetting,Long>, JpaSpecificationExecutor<BlanceSetting> {

    BlanceSetting findBySettingKey(String settingKey);
}

package com.wanmi.sbc.setting.imonlineservice.repository;

import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceLimitWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerServiceLimitWordRepository extends JpaRepository<CustomerServiceLimitWord, Long>,
        JpaSpecificationExecutor<CustomerServiceLimitWord> {

    List<CustomerServiceLimitWord> findByWordType(Integer wordType);
}

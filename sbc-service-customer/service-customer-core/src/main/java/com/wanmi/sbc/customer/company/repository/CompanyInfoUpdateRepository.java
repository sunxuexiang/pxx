package com.wanmi.sbc.customer.company.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.company.model.root.CompanyInfo;
import com.wanmi.sbc.customer.company.model.root.CompanyInfoUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 公司信息数据源
 * Created by CHENLI on 2017/5/12.
 */
@Repository
public interface CompanyInfoUpdateRepository extends JpaRepository<CompanyInfoUpdate, Long>, JpaSpecificationExecutor<CompanyInfoUpdate> {



}

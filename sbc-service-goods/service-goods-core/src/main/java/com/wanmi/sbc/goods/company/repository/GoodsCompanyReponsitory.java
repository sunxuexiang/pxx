package com.wanmi.sbc.goods.company.repository;

import com.wanmi.sbc.goods.company.model.root.GoodsCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsCompanyReponsitory extends JpaRepository<GoodsCompany, Long>, JpaSpecificationExecutor<GoodsCompany> {
}

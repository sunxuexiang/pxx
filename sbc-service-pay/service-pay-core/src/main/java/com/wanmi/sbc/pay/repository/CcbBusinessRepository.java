package com.wanmi.sbc.pay.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.pay.bean.enums.CcbDelFlag;
import com.wanmi.sbc.pay.model.root.CcbBusiness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/6/14 10:53
 */
@Repository
public interface CcbBusinessRepository extends JpaRepository<CcbBusiness, Long>, JpaSpecificationExecutor<CcbBusiness> {
    List<CcbBusiness> findByMktMrchNmAndDelFlagOrderByCreateTimeDesc(String mktMrchNm, CcbDelFlag delFlag);
    boolean existsByMktMrchIdAndDelFlag(String mktMrchId, CcbDelFlag delFlag);
    CcbBusiness findByMktMrchIdAndDelFlag(String mktMrchId, DeleteFlag delFlag);
    CcbBusiness findByMktMrchId(String mktMrchId);

}

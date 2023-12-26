package com.wanmi.sbc.account.wallet.repository;

import com.wanmi.sbc.account.wallet.model.root.TicketsFormImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketsFormImgRepository extends JpaRepository<TicketsFormImg, Long>, JpaSpecificationExecutor<TicketsFormImg> {

    List<TicketsFormImg> findTicketsFormImgsByTicketsFormImgIdIn(List<Long> ticketsFormImgIdList);

    List<TicketsFormImg> findTicketsFormImgsByDelFlagAndFormId(Integer delFlag, Long formId);

    List<TicketsFormImg> findTicketsFormImgsByDelFlagAndFormIdIn(Integer delFlag, List<Long> formIdList);
}

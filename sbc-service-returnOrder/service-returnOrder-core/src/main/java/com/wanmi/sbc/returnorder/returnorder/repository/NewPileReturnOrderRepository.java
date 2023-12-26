package com.wanmi.sbc.returnorder.returnorder.repository;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.returnorder.bean.enums.NewPileReturnFlowState;
import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.model.root.NewPileReturnOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface NewPileReturnOrderRepository extends MongoRepository<NewPileReturnOrder, String> {

    /**
     * 根据订单号查询退单列表
     * @param tid
     * @return
     */
    List<NewPileReturnOrder> findByTid(String tid);


    List<NewPileReturnOrder> findByIdIn(List<String> ids);

    /**
     * 根据订单号查询退单列表
     * @param tid
     * @return
     */
    List<NewPileReturnOrder> findByTidAndApplyChannl(String tid, DefaultFlag applyChannl);

    /**
     * 查询账期内的退单信息
     * @param storeId
     * @param startTime
     * @param endTime
     * @return
     */
    Page<NewPileReturnOrder> findByCompany_StoreIdAndFinishTimeBetweenAndReturnFlowState(Long storeId, Date startTime, Date
            endTime, Pageable pageRequest, ReturnFlowState returnFlowState);

    List<NewPileReturnOrder> findAllByReturnFlowStateNot(NewPileReturnFlowState flowState);

    List<NewPileReturnOrder> findByIdInAndReturnFlowStateNot(List<String> newPileReturnIds, NewPileReturnFlowState aVoid);
}

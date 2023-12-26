package com.wanmi.sbc.returnorder.returnorder.repository;

import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 退货repository
 * Created by jinwei on 20/4/2017.
 */
public interface ReturnOrderRepository extends MongoRepository<ReturnOrder, String> {

    /**
     * 根据订单号查询退单列表
     * @param tid
     * @return
     */
    List<ReturnOrder> findByTid(String tid);

//    /**
//     * 根据订单id查询
//     * @param id
//     * @return
//     */
//    ReturnOrder findById(String id);

    /**
     * 查询账期内的退单信息
     * @param storeId
     * @param startTime
     * @param endTime
     * @return
     */
    Page<ReturnOrder> findByCompany_StoreIdAndFinishTimeBetweenAndReturnFlowState(Long storeId, Date startTime, Date
            endTime, Pageable pageRequest, ReturnFlowState returnFlowState);


    Optional<ReturnOrder> findByCompany_StoreIdAndId(Long storeId, String tid);

    List<ReturnOrder> findByTidStartingWith(String tidPrefix);

    List<ReturnOrder> findByTidStartingWithAndReturnFlowStateNot(String tidPrefix, ReturnFlowState flowState);

    List<ReturnOrder> findByIdInAndReturnFlowStateNot(List<String> newPickReturnIds, ReturnFlowState flowState);

    List<ReturnOrder> findByTidAndWmsStats(String tid, boolean wmsStats);
}

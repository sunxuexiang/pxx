package com.wanmi.sbc.order.returnorder.repository;

import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.returnorder.model.root.ReturnPileOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 退货单
 * @author marsjiang
 * @date 20210928
 */
public interface ReturnPileOrderRepository extends MongoRepository<ReturnPileOrder, String> {

    /**
     * 根据订单号查询退单列表
     * @param tid
     * @return
     */
    List<ReturnPileOrder> findByTid(String tid);

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
    Page<ReturnPileOrder> findByCompany_StoreIdAndFinishTimeBetweenAndReturnFlowState(Long storeId, Date startTime, Date
            endTime, Pageable pageRequest, ReturnFlowState returnFlowState);


    Optional<ReturnPileOrder> findByCompany_StoreIdAndId(Long storeId, String tid);

}

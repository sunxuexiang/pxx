package com.wanmi.sbc.returnorder.returnorder.repository;

import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrderTransfer;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 退货单
 * @author marsjiang
 * @date 20210928
 */
public interface ReturnPileOrderTransferRepository extends MongoRepository<ReturnPileOrderTransfer, String> {

    /**
     * 根据用户id查询
     *
     * @param cid
     * @return
     */
    ReturnPileOrderTransfer findReturnOrderTransferByBuyerId(String cid);

    /**
     * 根据用户id删除
     *
     * @param cid
     */
    void deleteReturnOrderTransferByBuyerId(String cid);

    /**
     * 根据用订单id查找
     *
     * @param tid
     */
    ReturnPileOrderTransfer findReturnOrderTransferByTid(String tid);

    /**
     * 根据用订单id删除
     *
     * @param tid
     */
    void deleteReturnOrderTransferByTid(String tid);




}

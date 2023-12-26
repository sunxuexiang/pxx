package com.wanmi.sbc.returnorder.returnorder.service;

import com.wanmi.sbc.mongo.annotation.MongoRollback;
import com.wanmi.sbc.mongo.core.Operation;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnPileOrderTransfer;
import com.wanmi.sbc.returnorder.returnorder.repository.ReturnPileOrderTransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2019-03-28 17:32
 */
@Service
public class ReturnPileOrderTransferService {

    @Autowired
    private ReturnPileOrderTransferRepository returnPileOrderTransferRepository;

    /**
     * 新增文档
     * 专门用于数据新增服务,不允许数据修改的时候调用
     *
     * @param returnPileOrderTransfer
     */
    @MongoRollback(persistence = ReturnPileOrderTransfer.class, operation = Operation.ADD)
    public void addReturnPileOrderTransfer(ReturnPileOrderTransfer returnPileOrderTransfer) {
        returnPileOrderTransferRepository.save(returnPileOrderTransfer);
    }

    /**
     * 修改文档
     * 专门用于数据修改服务,不允许数据新增的时候调用
     *
     * @param returnPileOrderTransfer
     */
    @MongoRollback(persistence = ReturnPileOrderTransfer.class, operation = Operation.UPDATE)
    public void updateReturnPileOrderTransfer(ReturnPileOrderTransfer returnPileOrderTransfer) {
        returnPileOrderTransferRepository.save(returnPileOrderTransfer);
    }

    /**
     * 删除文档
     *
     * @param id
     */
    @MongoRollback(persistence = ReturnPileOrderTransfer.class, idExpress = "id",operation = Operation.UPDATE)
    public void deleteReturnPileOrderTransfer(String id) {
        returnPileOrderTransferRepository.deleteById(id);
    }


}

package com.wanmi.sbc.order.pilepurchaseaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description: TODO
 * @author: XinJiang
 * @time: 2021/12/20 15:36
 */
@Service
@Slf4j
public class PilePurchaseActionService {

    @Autowired
    private PilePurchaseActionRepository pilePurchaseActionRepository;

    /**
     * 查询囤货明细总数
     * @param request
     * @return
     */
    @Transactional(readOnly = true,timeout = 10)
    public Long countPilePurchaseAction(PilePurchaseActionRequest request) {
       return pilePurchaseActionRepository.count(request.getWhereCriteria());
    }

    /**
     * 分页查询囤货明细
     * @param request
     * @return
     */
    @Transactional(readOnly = true,timeout = 10)
    public Page<PilePurchaseAction> page(PilePurchaseActionRequest request) {
        Page<PilePurchaseAction> pilePurchaseActionPage = pilePurchaseActionRepository.findAll(request.getWhereCriteria(),request.getPageRequest());
        return pilePurchaseActionPage;
    }

    /**
     * 批量保存信息
     * @param request
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchSavePilePurchaseAction(PilePurchaseActionRequest request) {
        pilePurchaseActionRepository.saveAll(request.getPurchaseActionList());
    }
}

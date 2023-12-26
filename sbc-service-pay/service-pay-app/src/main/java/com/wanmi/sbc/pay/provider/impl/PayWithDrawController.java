package com.wanmi.sbc.pay.provider.impl;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.pay.api.provider.PayWithDrawProvider;
import com.wanmi.sbc.pay.api.response.PayWithDrawListResponse;
import com.wanmi.sbc.pay.api.response.PayWithDrawResponse;
import com.wanmi.sbc.pay.bean.dto.PayWithDrawDTO;
import com.wanmi.sbc.pay.bean.dto.PayWithDrawIdDTO;
import com.wanmi.sbc.pay.service.PayWithDrawService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lm
 * @date 2022/10/21 9:42
 */
@RestController
@Validated
@Slf4j
public class PayWithDrawController implements PayWithDrawProvider {

    @Autowired
    private PayWithDrawService payWithDrawService;

    @Override
    public BaseResponse<PayWithDrawListResponse> listWithDraw() {
        PayWithDrawListResponse withDrawListResponse = payWithDrawService.listWithDraw();
        return BaseResponse.success(withDrawListResponse);
    }

    /**
     * 新增鲸币提现账户
     *
     * @param payWithDrawDTO
     */
    @Override
    public BaseResponse addWithDraw(PayWithDrawDTO payWithDrawDTO) {
        log.info("类名：PayWithDrawController,方法名：addWithDraw，params：{}", JSON.toJSONString(payWithDrawDTO));
        payWithDrawService.addWithDraw(payWithDrawDTO);
        log.info("类名：PayWithDrawController,方法名：addWithDraw，success");
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改鲸币提现账户
     *
     * @param payWithDrawDTO
     */
    @Override
    public BaseResponse updateWithDraw(PayWithDrawDTO payWithDrawDTO) {
        log.info("类名：PayWithDrawController,方法名：updateWithDraw，params：{}", JSON.toJSONString(payWithDrawDTO));
        payWithDrawService.updateWithDraw(payWithDrawDTO);
        log.info("类名：PayWithDrawController,方法名：updateWithDraw，success");
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 根据ID查询鲸币提现账户
     *
     * @param payWithDrawIdDTO
     * @return
     */
    @Override
    public BaseResponse<PayWithDrawResponse> findWithDrawById(PayWithDrawIdDTO payWithDrawIdDTO) {
        log.info("类名：PayWithDrawController,方法名：findWithDrawById，params：{}", JSON.toJSONString(payWithDrawIdDTO));
        return BaseResponse.success(payWithDrawService.findWithDrawById(payWithDrawIdDTO.getWithdrawId()));
    }

    @Override
    public BaseResponse deletePayWithDraw(PayWithDrawIdDTO withDrawIdDTO) {
        log.info("类名：PayWithDrawController,方法名：deletePayWithDraw，params：{}", withDrawIdDTO.getWithdrawId());
        payWithDrawService.deletePayWithDraw(withDrawIdDTO.getWithdrawId());
        return BaseResponse.SUCCESSFUL();
    }
}

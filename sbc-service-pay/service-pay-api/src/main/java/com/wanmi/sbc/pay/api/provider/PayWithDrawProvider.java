package com.wanmi.sbc.pay.api.provider;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.pay.api.request.PayWithDrawRequest;
import com.wanmi.sbc.pay.api.response.PayWithDrawListResponse;
import com.wanmi.sbc.pay.api.response.PayWithDrawResponse;
import com.wanmi.sbc.pay.bean.dto.PayWithDrawDTO;
import com.wanmi.sbc.pay.bean.dto.PayWithDrawIdDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author lm
 * @date 2022/10/21 9:41
 */
@FeignClient(value = "${application.pay.name}", url="${feign.url.pay:#{null}}", contextId = "PayWithDrawProvider")
public interface PayWithDrawProvider {
    /**
     * 鲸币提现账户列表
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/listWithDraw")
    BaseResponse<PayWithDrawListResponse> listWithDraw();

    /**
     * 新增鲸币提现账户
     */
    @PostMapping("/pay/${application.pay.version}/addWithDraw")
    BaseResponse addWithDraw(@RequestBody @Valid PayWithDrawDTO payWithDrawDTO);


    /**
     * 修改鲸币提现账户
     */
    @PostMapping("/pay/${application.pay.version}/updateWithDraw")
    BaseResponse updateWithDraw(@RequestBody @Valid PayWithDrawDTO payWithDrawDTO);


    /**
     * 根据ID查询鲸币提现账户
     * @param payWithDrawIdDTO
     * @return
     */
    @PostMapping("/pay/${application.pay.version}/findWithDrawById")
    BaseResponse<PayWithDrawResponse> findWithDrawById(@RequestBody @Valid PayWithDrawIdDTO payWithDrawIdDTO);

    /**
     * 删除提现账户
     * @param withDrawIdDTO
     */
    @PostMapping("/pay/${application.pay.version}/deletePayWithDraw")
    BaseResponse deletePayWithDraw(@RequestBody @Valid PayWithDrawIdDTO withDrawIdDTO);
}

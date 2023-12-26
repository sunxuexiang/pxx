package com.wanmi.sbc.returnorder.provider.impl.refundfreight;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.provider.refundfreight.RefundFreightProvider;
import com.wanmi.sbc.returnorder.api.request.refundfreight.RefundFreightCallbackRequest;
import com.wanmi.sbc.returnorder.api.request.refundfreight.RefundFreightRequest;
import com.wanmi.sbc.returnorder.refundfreight.service.RefundFreightService;
import com.wanmi.sbc.returnorder.returnextra.service.RefundExtraRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/30 14:29
 */
@Validated
@RestController
public class RefundFreightController implements RefundFreightProvider {

    @Autowired
    private RefundFreightService refundFreightService;

    @Autowired
    private RefundExtraRecordService extraRecordService;

    @Override
    public BaseResponse add(RefundFreightRequest request) {
        refundFreightService.add(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse callback(RefundFreightCallbackRequest request) {
        refundFreightService.callback(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse extraCallback(RefundFreightCallbackRequest request) {
        extraRecordService.callback(request);
        return BaseResponse.SUCCESSFUL();
    }
}

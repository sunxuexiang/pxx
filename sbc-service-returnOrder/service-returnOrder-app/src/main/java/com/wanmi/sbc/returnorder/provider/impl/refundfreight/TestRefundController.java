package com.wanmi.sbc.returnorder.provider.impl.refundfreight;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.returnextra.service.RefundExtraRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/12/14 14:27
 */
@RestController
@RequestMapping("/test")
public class TestRefundController {

    @Autowired
    private RefundExtraRecordService refundExtraRecordService;

    @GetMapping("/refund/extra")
    public BaseResponse testRefundExtra(String tid, String rid) {
        refundExtraRecordService.refundExtra(tid, rid);
        return BaseResponse.SUCCESSFUL();
    }
}

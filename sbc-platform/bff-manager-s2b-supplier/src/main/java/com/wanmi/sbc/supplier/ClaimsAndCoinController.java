package com.wanmi.sbc.supplier;

import com.wanmi.sbc.aop.EmployeeCheck;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.marketing.api.provider.coupon.CoinActivityProvider;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDto;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderProvider;
import com.wanmi.sbc.order.bean.vo.RefundForClaimsApplyVO;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "ClaimsAndCoinController")
@RestController
@RequestMapping("/claims-coin")
@Slf4j
public class ClaimsAndCoinController {

    @Autowired
    private RefundOrderProvider refundOrderProvider;
    @Autowired
    private CoinActivityProvider coinActivityProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @GetMapping("/getApplyDetail/{applyNo}")
    @ApiOperation(value = "理赔申请详情")
    @EmployeeCheck
    public BaseResponse<RefundForClaimsApplyVO> getApplyDetail(@PathVariable(value = "applyNo") String applyNo) {
        operateLogMQUtil.convertAndSend("理赔", "理赔申请详情", "理赔申请详情");
        return refundOrderProvider.getApplyDetail(applyNo);
    }


    @ApiOperation(value = "根据订单查记录详情")
    @GetMapping(value = "/record/{orderId}")
    public BaseResponse<CoinActivityRecordDto> recordByOrderId(@PathVariable String orderId) {
        return coinActivityProvider.recordByOrderId(orderId);
    }


}

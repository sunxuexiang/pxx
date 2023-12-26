package com.wanmi.sbc.account;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.job.PayCallBackJobHandle;
import com.wanmi.sbc.mq.ReturnOrderConsumerService;
import com.wanmi.sbc.order.api.request.refund.RefundOrderNotAuditProducerRequest;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * boss测试
 * Created by sunkun on 2017/11/2.
 */
@RestController("BossTestController")
@RequestMapping("/boss-test")
@Api(tags = "BossTestController", description = "boss测试")
public class BossTestController {

    @Autowired
    private ReturnOrderConsumerService returnOrderConsumerService;

    @Autowired
    private PayCallBackJobHandle payCallBackJobHandle;

    @Autowired
    OperateLogMQUtil operateLogMQUtil;

    /**
     * 获取商家结算银行账户
     *
     * @return
     */
    @ApiOperation(value = "S2B 平台端-处理新囤货订单的提货退款")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "companyInfoId", value = "商家的公司id", required = true)
    @PostMapping("/processReturnOrderFromNewPile")
    public BaseResponse<String> list(@RequestBody @Valid Map<String,Object> map) {
        RefundOrderNotAuditProducerRequest refundRequest = new RefundOrderNotAuditProducerRequest();
        refundRequest.setRId(map.get("rId").toString());
        returnOrderConsumerService.processReturnOrderFromNewPile(refundRequest);
        operateLogMQUtil.convertAndSend("运营端系统", "boss测试", "S2B 平台端-处理新囤货订单的提货退款：rId" + refundRequest.getRId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 获取商家结算银行账户
     *
     * @return
     */
    @ApiOperation(value = "S2B 平台端-处理新囤货订单的提货退款")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "companyInfoId", value = "商家的公司id", required = true)
    @PostMapping("/processReturnOrderFromNewPile2")
    public BaseResponse<String> list2(@RequestBody @Valid RefundOrderNotAuditProducerRequest refundRequest) {
        returnOrderConsumerService.processReturnOrderFromNewPile(refundRequest);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 获取商家结算银行账户
     *
     * @return
     */
    @ApiOperation(value = "S2B 平台端-支付回调测试")
    @PostMapping("/processPayCallback")
    public BaseResponse<String> processPayCallback(@RequestParam(value = "param") String param) {
        try {
            payCallBackJobHandle.execute("4&"+param);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return BaseResponse.SUCCESSFUL();
    }
}

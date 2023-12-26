package com.wanmi.sbc.refundfreight;

import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.returnorder.api.provider.refundfreight.RefundFreightProvider;
import com.wanmi.sbc.returnorder.api.request.refundfreight.RefundFreightRequest;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/30 16:52
 */
@Api(tags = "手动退运费")
@RestController
@RequestMapping(value = "/refund/freight")
public class RefundFreightController {

    @Autowired
    private RefundFreightProvider refundFreightProvider;

    @Autowired
    private CommonUtil commonUtil;

    @ApiOperation(value = "创建订单手动退款")
    @PostMapping(value = "/add")
    @MultiSubmit
    public BaseResponse add(@RequestBody @Valid RefundFreightRequest request) {
        Operator operator = commonUtil.getOperator();
        request.setOperator(operator);
        return refundFreightProvider.add(request);
    }
}

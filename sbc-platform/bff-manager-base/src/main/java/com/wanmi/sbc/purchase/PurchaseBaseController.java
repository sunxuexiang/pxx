package com.wanmi.sbc.purchase;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.SiteResultCode;
import com.wanmi.sbc.shopcart.api.provider.order.PurchaseApiRequest;
import com.wanmi.sbc.shopcart.api.provider.purchase.PurchaseProvider;
import com.wanmi.sbc.order.api.request.purchase.PurchaseDeleteRequest;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "PurchaseBaseController", description = "采购单服务API")
@RestController
@RequestMapping("/supplier")
@Validated
public class PurchaseBaseController {


    @Autowired
    private PurchaseProvider purchaseProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 删除采购单
     *
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "删除采购单")
    @RequestMapping(value = "/purchase", method = RequestMethod.DELETE)
    public BaseResponse delete(@RequestBody PurchaseApiRequest request) {

        request.setCustomerId(request.getCustomerId());
        if (CollectionUtils.isEmpty(request.getGoodsInfoIds()) || StringUtils.isBlank(request.getCustomerId())) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_000009);
        }

        //分销员暂时填写0
        request.setInviteeId("0");
        purchaseProvider.delete(request);
        operateLogMQUtil.convertAndSend("采购单服务", "删除采购单", "操作成功：用户id" + request.getCustomerId());
        return BaseResponse.SUCCESSFUL();
    }
}

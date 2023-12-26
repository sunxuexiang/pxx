package com.wanmi.sbc.wms.provider.impl.requestwms;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.wms.api.provider.wms.RequestWMSOrderProvider;
import com.wanmi.sbc.wms.api.request.wms.WMSOrderCancelRequest;
import com.wanmi.sbc.wms.api.request.wms.WMSChargeBackRequest;
import com.wanmi.sbc.wms.api.request.wms.WMSPushOrderRequest;
import com.wanmi.sbc.wms.api.response.wms.ResponseWMSReturnResponse;
import com.wanmi.sbc.wms.bean.vo.ResponseWMSReturnVO;
import com.wanmi.sbc.wms.requestwms.model.WMSOrderCancel;
import com.wanmi.sbc.wms.requestwms.model.WMSChargeBack;
import com.wanmi.sbc.wms.requestwms.model.WMSPushOrder;
import com.wanmi.sbc.wms.requestwms.model.response.ResponseWMSReturn;
import com.wanmi.sbc.wms.requestwms.service.WMSOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @ClassName: RequestWMSOrderController
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/8 10:21
 * @Version: 1.0
 */
@RestController
@Slf4j
public class RequestWMSOrderController implements RequestWMSOrderProvider {

    @Lazy
    @Autowired
    private WMSOrderService WMSOrderService;

    @Override
    public BaseResponse<ResponseWMSReturnResponse> cancelOrder(@RequestBody @Valid WMSOrderCancelRequest WMSOrderCancelRequest) {
        WMSOrderCancel WMSOrderCancel = new WMSOrderCancel();
        KsBeanUtil.copyPropertiesThird(WMSOrderCancelRequest, WMSOrderCancel);
        ResponseWMSReturn ResponseWMSReturn = WMSOrderService.cancelOrder(WMSOrderCancel);

        ResponseWMSReturnVO responseWMSReturnVO = new ResponseWMSReturnVO();
        KsBeanUtil.copyPropertiesThird(ResponseWMSReturn, responseWMSReturnVO);
        return BaseResponse.success(new ResponseWMSReturnResponse(responseWMSReturnVO));
    }

    @Override
    public BaseResponse<ResponseWMSReturnResponse> putASN(@RequestBody @Valid WMSChargeBackRequest WMSChargeBackRequest) {
        WMSChargeBack order=KsBeanUtil.convert(WMSChargeBackRequest, WMSChargeBack.class);
        ResponseWMSReturn ResponseWMSReturn = WMSOrderService.putASN(order);

        ResponseWMSReturnVO responseWMSReturnVO = new ResponseWMSReturnVO();
        KsBeanUtil.copyPropertiesThird(ResponseWMSReturn, responseWMSReturnVO);
        return BaseResponse.success(new ResponseWMSReturnResponse(responseWMSReturnVO));
    }


    @Override
    public BaseResponse<ResponseWMSReturnResponse> putSalesOrder(@RequestBody @Valid WMSPushOrderRequest pushOrderRequest) {
        WMSOrderService.putSalesOrder(pushOrderRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<ResponseWMSReturnResponse> confirmSalesOrder(@RequestBody @Valid WMSOrderCancelRequest WMSOrderCancelRequest) {
        WMSOrderCancel WMSOrderCancel = new WMSOrderCancel();
        KsBeanUtil.copyPropertiesThird(WMSOrderCancelRequest, WMSOrderCancel);
        ResponseWMSReturn ResponseWMSReturn = WMSOrderService.confirmSalesOrder(WMSOrderCancel,null);

        ResponseWMSReturnVO responseWMSReturnVO = new ResponseWMSReturnVO();
        KsBeanUtil.copyPropertiesThird(ResponseWMSReturn, responseWMSReturnVO);
        return BaseResponse.success(new ResponseWMSReturnResponse(responseWMSReturnVO));
    }
}

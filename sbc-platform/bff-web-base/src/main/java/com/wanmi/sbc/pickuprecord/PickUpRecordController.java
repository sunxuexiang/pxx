package com.wanmi.sbc.pickuprecord;


import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.order.api.provider.pickuprecord.PickUpRecordProvider;
import com.wanmi.sbc.order.api.provider.pickuprecord.PickUpRecordQueryProvider;
import com.wanmi.sbc.order.api.request.pickuprecord.PickUpRecordByTradeIdRequest;
import com.wanmi.sbc.order.api.request.pickuprecord.PickUpRecordUpdateByTradeIdRequest;
import com.wanmi.sbc.order.api.response.pickuprecord.PickUpRecordByIdResponse;
import com.wanmi.sbc.order.bean.vo.PickUpRecordVO;
import com.wanmi.sbc.pickuprecord.request.PickUpCommitNoCodeRequest;
import com.wanmi.sbc.pickuprecord.request.PickUpCommitRequest;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Api(description = "自提码控制层", tags = "PickUpRecordController")
@RestController
@RequestMapping(value = "/pickuprecord")
public class PickUpRecordController {

    @Autowired
    private PickUpRecordQueryProvider pickUpRecordQueryProvider;

    @Autowired
    private PickUpRecordProvider pickUpRecordProvider;

    @Autowired
    private CommonUtil commonUtil;

    /**
     * 获取自提订单自提信息
     * @return
     */
    @RequestMapping(value = "/get-by-trade-id/{tradeId}", method = RequestMethod.GET)
    @ApiOperation("获取自提订单自提信息")
    public BaseResponse<PickUpRecordByIdResponse> getOneByTradeId(@PathVariable("tradeId") String tradeId){
        if (tradeId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        return pickUpRecordQueryProvider.getOneByTradeId(PickUpRecordByTradeIdRequest.builder().tradeId(tradeId).build());
    }



    /**
     * 兑换自提订单
     * @return
     */
    @RequestMapping(value = "/updatePickUpNoCode", method = RequestMethod.PUT)
    @ApiOperation("兑换自提订单（无需自提码）")
    @MultiSubmit
    public BaseResponse update(@RequestBody @Valid PickUpCommitNoCodeRequest request){
        PickUpRecordVO pickUpRecordVO = pickUpRecordQueryProvider.getOneByTradeId(PickUpRecordByTradeIdRequest.builder()
                .tradeId(request.getTradeId()).build()).getContext().getPickUpRecordVO();
        if (pickUpRecordVO==null){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"自提订单不存在");
        }
        if (pickUpRecordVO.getPickUpFlag().equals(DefaultFlag.YES)){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"该订单已自提");
        }
        Operator operator = commonUtil.getOperator();
        pickUpRecordProvider.updatePickUp(PickUpRecordUpdateByTradeIdRequest.builder()
                .tradeId(request.getTradeId()).operator(operator).phone(pickUpRecordVO.getContactPhone()).build());
        return BaseResponse.SUCCESSFUL();
    }
}

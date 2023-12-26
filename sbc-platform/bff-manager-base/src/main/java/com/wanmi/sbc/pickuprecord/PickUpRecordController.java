package com.wanmi.sbc.pickuprecord;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.order.api.provider.pickuprecord.PickUpRecordProvider;
import com.wanmi.sbc.order.api.provider.pickuprecord.PickUpRecordQueryProvider;
import com.wanmi.sbc.order.api.request.pickuprecord.PickUpRecordByTradeIdRequest;
import com.wanmi.sbc.order.api.request.pickuprecord.PickUpRecordUpdateByTradeIdRequest;
import com.wanmi.sbc.order.bean.vo.PickUpRecordVO;
import com.wanmi.sbc.pickuprecord.request.PickUpCommitRequest;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    /**
     * 兑换自提订单
     * @return
     */
    @RequestMapping(value = "/updatePickUpCode", method = RequestMethod.PUT)
    @ApiOperation("兑换自提订单")
    public BaseResponse update(@RequestBody @Valid PickUpCommitRequest request){
        PickUpRecordVO pickUpRecordVO = pickUpRecordQueryProvider.getOneByTradeId(PickUpRecordByTradeIdRequest.builder()
                .tradeId(request.getTradeId()).build()).getContext().getPickUpRecordVO();
        if (pickUpRecordVO==null){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"自提码不存在");
        }
        if (pickUpRecordVO.getPickUpFlag().equals(DefaultFlag.YES)){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"该订单已自提");
        }
        if (!pickUpRecordVO.getPickUpCode().equals(request.getPickUpCode())){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"自提码不正确");
        }
        Operator operator = commonUtil.getOperator();
        pickUpRecordProvider.updatePickUp(PickUpRecordUpdateByTradeIdRequest.builder()
                .tradeId(request.getTradeId()).operator(operator).phone(pickUpRecordVO.getContactPhone()).build());

        operateLogMQUtil.convertAndSend("自提码控制层", "兑换自提订单", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }
}

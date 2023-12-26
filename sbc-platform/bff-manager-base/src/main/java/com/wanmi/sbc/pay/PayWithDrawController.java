package com.wanmi.sbc.pay;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.EnableStatus;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.pay.api.provider.PayWithDrawProvider;
import com.wanmi.sbc.pay.api.request.PayWithDrawRequest;
import com.wanmi.sbc.pay.api.response.PayWithDrawListResponse;
import com.wanmi.sbc.pay.api.response.PayWithDrawResponse;
import com.wanmi.sbc.pay.bean.dto.PayWithDrawDTO;
import com.wanmi.sbc.pay.bean.dto.PayWithDrawIdDTO;
import com.wanmi.sbc.pay.bean.vo.PayWithDrawVO;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

/**
 * @author lm
 * @date 2022/10/21 15:20
 */

@Api(tags = "PayWithDrawController", description = "鲸币提现收款账户 Api")
@RestController
@Validated
@Slf4j
public class PayWithDrawController {

    @Autowired
    private PayWithDrawProvider payWithDrawProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation("获取鲸币提现收款账户列表")
    @GetMapping("/payWithDraw/list")
    public BaseResponse<PayWithDrawListResponse> list(){
        return payWithDrawProvider.listWithDraw();
    }

    @ApiOperation("添加鲸币提现收款账户")
    @PostMapping("/payWithDraw/add")
    public BaseResponse add(@RequestBody @Valid PayWithDrawRequest payWithDrawRequest){
        payWithDrawRequest.setWithdrawId(null);
        // log.info("修改鲸币提现收款账户,path:/payWithDraw/add,param: {}", JSON.toJSONString(payWithDrawRequest));
        PayWithDrawDTO payWithDrawDTO = KsBeanUtil.convert(payWithDrawRequest, PayWithDrawDTO.class);
        payWithDrawProvider.addWithDraw(payWithDrawDTO);

        // 记录操作日志
        operateLogMQUtil.convertAndSend("财务","收款账户","添加鲸币提现付款账户,账号："+payWithDrawRequest.getBankAccount());
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation("修改鲸币提现收款账户")
    @PutMapping("/payWithDraw/update")
    public BaseResponse update(@RequestBody @Valid PayWithDrawRequest payWithDrawRequest){
        // log.info("修改鲸币提现收款账户,path:/payWithDraw/update,param: {}", JSON.toJSONString(payWithDrawRequest));
        PayWithDrawDTO payWithDrawDTO = KsBeanUtil.convert(payWithDrawRequest, PayWithDrawDTO.class);
        payWithDrawDTO.setEnableStatus(EnableStatus.ENABLE);
        payWithDrawProvider.updateWithDraw(payWithDrawDTO);

        // 记录操作日志
        operateLogMQUtil.convertAndSend("财务","收款账户","修改鲸币提现付款账户,账号："+payWithDrawRequest.getBankAccount());
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation("启用或禁用鲸币提现收款账户")
    @PutMapping(("/payWithDraw/enable/{id}"))
    public BaseResponse enable(@PathVariable("id") Integer withDrawId){
        log.info("启用或禁用鲸币提现收款账户,path:/payWithDraw/enable,param: {}", withDrawId);
        PayWithDrawIdDTO withDrawIdDTO = PayWithDrawIdDTO.builder().withdrawId(withDrawId).build();
        PayWithDrawResponse drawResponse = payWithDrawProvider.findWithDrawById(withDrawIdDTO).getContext();
        if(Objects.isNull(drawResponse) || Objects.isNull(drawResponse.getPayWithDrawVO())){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        PayWithDrawVO payWithDrawVO = drawResponse.getPayWithDrawVO();

        PayWithDrawDTO payWithDrawDTO = KsBeanUtil.convert(payWithDrawVO, PayWithDrawDTO.class);
        String message = "启用";
        if(EnableStatus.ENABLE == payWithDrawDTO.getEnableStatus()){
            message = "禁用";
            payWithDrawDTO.setEnableStatus(EnableStatus.DISABLE);
        }else{
            payWithDrawDTO.setEnableStatus(EnableStatus.ENABLE);
        }

        payWithDrawProvider.updateWithDraw(payWithDrawDTO);

        // 记录操作日志
        operateLogMQUtil.convertAndSend("财务","收款账户",message+"鲸币提现付款账户,账号："+payWithDrawVO.getBankAccount());
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation("删除鲸币提现收款账户")
    @DeleteMapping("/payWithDraw/delete/{id}")
    public BaseResponse deletePayWithDraw(@PathVariable("id") Integer withDrawId){
        log.info("删除鲸币提现收款账户,path:/payWithDraw/enable,param: {}", withDrawId);
        PayWithDrawIdDTO withDrawIdDTO = PayWithDrawIdDTO.builder().withdrawId(withDrawId).build();
        payWithDrawProvider.deletePayWithDraw(withDrawIdDTO);

        // 记录操作日志
        operateLogMQUtil.convertAndSend("财务","收款账户","删除鲸币提现收款账户,ID："+withDrawId);
        return BaseResponse.SUCCESSFUL();
    }

}

package com.wanmi.sbc.manualrefund;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.provider.manualrefund.ManualRefundImgProvider;
import com.wanmi.sbc.order.api.request.manualrefund.ManualRefundImgRequest;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "ManualRefundImgController", description = "Api")
@RestController
@RequestMapping("/manualRefundImg")
@Slf4j
@Validated
public class ManualRefundImgController {

    @Autowired
    private ManualRefundImgProvider manualRefundImgProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    /**
     * 保存(新增)退货退款订单财务上传已打款凭证
     *
     * @param manualRefundImgRequest
     * @return
     */
    @ApiOperation(value = "保存(新增)退货退款订单财务上传已打款凭证")
    @PostMapping("/addManualRefundImgs")
    public BaseResponse addManualRefundImgs(@RequestBody ManualRefundImgRequest manualRefundImgRequest) {
        BaseResponse baseResponse = null;
        try {
            baseResponse = manualRefundImgProvider.addManualRefundImgs(manualRefundImgRequest);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        //操作日志记录
        operateLogMQUtil.convertAndSend("退货退款订单", "保存(新增)退货退款订单财务上传已打款凭证", "操作成功");
        return baseResponse;
    }

    /**
     * 删除退货退款订单财务上传已打款凭证
     *
     * @param manualRefundImgRequest
     * @return
     */
    @ApiOperation(value = "删除退货退款订单财务上传已打款凭证")
    @PostMapping("/deleteManualRefundImgs")
    public BaseResponse deleteManualRefundImgs(@RequestBody ManualRefundImgRequest manualRefundImgRequest) {
        BaseResponse baseResponse = null;
        try {
            baseResponse = manualRefundImgProvider.deleteManualRefundImgs(manualRefundImgRequest);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        //操作日志记录
        operateLogMQUtil.convertAndSend("退货退款订单", "删除退货退款订单财务上传已打款凭证", "操作成功");
        return baseResponse;
    }

    /**
     * @description  
     * @author  shiy
     * @date    2023/3/22 10:37
     * @params  [com.wanmi.sbc.order.api.request.manualrefund.ManualRefundImgRequest]
     * @return  com.wanmi.sbc.common.base.BaseResponse
    */
    @ApiOperation(value = "退货退款订单财务上传已打款凭证")
    @PostMapping("/listManualRefundImgs")
    public BaseResponse listManualRefundImgs(@RequestBody ManualRefundImgRequest manualRefundImgRequest) {
        BaseResponse baseResponse = null;
        try {
            baseResponse = manualRefundImgProvider.listManualRefundImgs(manualRefundImgRequest);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        //操作日志记录
        operateLogMQUtil.convertAndSend("退货退款订单", "退货退款订单财务上传已打款凭证", "操作成功");
        return baseResponse;
    }


    @ApiOperation(value = "初始化化数据")
    @PostMapping("/initOrderImgsFlag")
    public BaseResponse initOrderImgsFlag(@RequestBody ManualRefundImgRequest manualRefundImgRequest) {
        BaseResponse baseResponse = null;
        try {
            baseResponse = manualRefundImgProvider.initOrderImgsFlag(manualRefundImgRequest);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        //操作日志记录
        operateLogMQUtil.convertAndSend("退货退款订单", "初始化数据", "操作成功");
        return baseResponse;
    }
}

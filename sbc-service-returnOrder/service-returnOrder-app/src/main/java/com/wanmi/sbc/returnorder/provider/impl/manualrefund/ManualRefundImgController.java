package com.wanmi.sbc.returnorder.provider.impl.manualrefund;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.returnorder.api.provider.manualrefund.ManualRefundImgProvider;
import com.wanmi.sbc.returnorder.api.request.manualrefund.ManualRefundImgRequest;
import com.wanmi.sbc.returnorder.api.response.manualrefund.ManualRefundImgResponse;
import com.wanmi.sbc.returnorder.manualrefund.service.ManualRefundImgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Slf4j
public class ManualRefundImgController implements ManualRefundImgProvider {

    @Autowired
    private ManualRefundImgService manualRefundImgService;

    /**
     * 保存(新增)退货退款订单财务上传已打款凭证
     *
     * @param manualRefundImgRequest
     * @return
     */
    @Override
    public BaseResponse addManualRefundImgs(ManualRefundImgRequest manualRefundImgRequest) {
        ManualRefundImgResponse manualRefundImgResponse = null;
        try {
            manualRefundImgResponse = manualRefundImgService.addManualRefundImgs(manualRefundImgRequest);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return BaseResponse.success(manualRefundImgResponse);
    }

    /**
     * 删除退货退款订单财务上传已打款凭证
     *
     * @param manualRefundImgRequest
     * @return
     */
    @Override
    public BaseResponse deleteManualRefundImgs(ManualRefundImgRequest manualRefundImgRequest) {
        try {
            manualRefundImgService.deleteManualRefundImgs(manualRefundImgRequest);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 查看退货退款订单财务上传已打款凭证
     *
     * @param manualRefundImgRequest
     * @return
     */
    @Override
    public BaseResponse listManualRefundImgs(ManualRefundImgRequest manualRefundImgRequest) {
        ManualRefundImgResponse manualRefundImgResponse = manualRefundImgService.listManualRefundImgs(manualRefundImgRequest);
        return BaseResponse.success(manualRefundImgResponse);
    }

    @Override
    public BaseResponse initOrderImgsFlag(ManualRefundImgRequest manualRefundImgRequest) {
        return manualRefundImgService.initOrderRefundImgsFlag();
    }
}

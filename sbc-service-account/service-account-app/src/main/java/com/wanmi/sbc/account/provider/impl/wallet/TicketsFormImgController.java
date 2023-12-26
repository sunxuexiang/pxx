package com.wanmi.sbc.account.provider.impl.wallet;

import com.wanmi.sbc.account.api.provider.wallet.TicketsFormImgProvider;
import com.wanmi.sbc.account.api.request.wallet.TicketsFormImgRequest;
import com.wanmi.sbc.account.api.response.wallet.TicketsFormImgResponse;
import com.wanmi.sbc.account.wallet.service.TicketsFormImgService;
import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class TicketsFormImgController implements TicketsFormImgProvider {

    @Autowired
    private TicketsFormImgService ticketsFormImgService;

    /**
     * 保存(新增)鲸币提现财务上传已打款凭证
     *
     * @param ticketsFormImgRequest
     * @return
     */
    @Override
    public BaseResponse addTicketsFormImgs(TicketsFormImgRequest ticketsFormImgRequest) {
        ticketsFormImgService.addTicketsFormImgs(ticketsFormImgRequest);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除鲸币提现财务上传已打款凭证
     *
     * @param ticketsFormImgRequest
     * @return
     */
    @Override
    public BaseResponse deleteTicketsFormImgs(TicketsFormImgRequest ticketsFormImgRequest) {
        ticketsFormImgService.deleteTicketsFormImgs(ticketsFormImgRequest);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 查看鲸币提现财务上传已打款凭证
     *
     * @param ticketsFormImgRequest
     * @return
     */
    @Override
    public BaseResponse listTicketsFormImgs(TicketsFormImgRequest ticketsFormImgRequest) {
        TicketsFormImgResponse ticketsFormImgResponse = ticketsFormImgService.listTicketsFormImgs(ticketsFormImgRequest);
        return BaseResponse.success(ticketsFormImgResponse);
    }
}

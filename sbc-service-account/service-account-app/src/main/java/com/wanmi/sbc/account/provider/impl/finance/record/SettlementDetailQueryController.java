package com.wanmi.sbc.account.provider.impl.finance.record;

import com.wanmi.sbc.account.api.provider.finance.record.SettlementDetailQueryProvider;
import com.wanmi.sbc.account.api.request.finance.record.SettlementDetailByParamRequest;
import com.wanmi.sbc.account.api.request.finance.record.SettlementDetailListBySettleUuidRequest;
import com.wanmi.sbc.account.api.response.finance.record.SettlementDetailByParamResponse;
import com.wanmi.sbc.account.api.response.finance.record.SettlementDetailListBySettleUuidResponse;
import com.wanmi.sbc.account.bean.vo.SettlementDetailVO;
import com.wanmi.sbc.account.finance.record.model.root.SettlementDetail;
import com.wanmi.sbc.account.finance.record.service.SettlementDetailService;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 结算明细
 */
@RestController
@Validated
public class SettlementDetailQueryController implements SettlementDetailQueryProvider {

    @Autowired
    private SettlementDetailService settlementDetailService;

    /**
     * 根据条件查询单条结算明细
     *
     * @param settlementDetailByParamRequest 查询条件 {@link SettlementDetailByParamRequest}
     * @return 结算明细返回结构 {@link SettlementDetailByParamResponse}
     */

    @Override
    public BaseResponse<SettlementDetailByParamResponse> getByParam(@RequestBody @Valid SettlementDetailByParamRequest settlementDetailByParamRequest) {
        SettlementDetail settlementDetail = settlementDetailService.getByTradeId(settlementDetailByParamRequest.getTradeId(),
                settlementDetailByParamRequest.getStartDate(),settlementDetailByParamRequest.getEndDate()).orElse( null);
        SettlementDetailVO settlementDetailVO = null;
        if(null != settlementDetail){
            settlementDetailVO  =  KsBeanUtil.convert(settlementDetail,SettlementDetailVO.class);
        }
        return BaseResponse.success(new SettlementDetailByParamResponse(settlementDetailVO));
    }

    /**
     * 根据结算单id查询结算明细列表
     *
     * @param settlementDetailListBySettleUuidRequest 包含结算单id的查询条件 {@link SettlementDetailListBySettleUuidRequest}
     * @return 返回的计算明细列表 {@link SettlementDetailListBySettleUuidResponse}
     */

    @Override
    public BaseResponse<SettlementDetailListBySettleUuidResponse> listBySettleUuid(@RequestBody @Valid SettlementDetailListBySettleUuidRequest settlementDetailListBySettleUuidRequest) {
        List<SettlementDetail> settlementDetailList = settlementDetailService.getSettlementDetail(settlementDetailListBySettleUuidRequest.getSettleUuid());
        List<SettlementDetailVO> settlementDetailVOList = KsBeanUtil.convert(settlementDetailList,SettlementDetailVO.class);
        return BaseResponse.success(new SettlementDetailListBySettleUuidResponse(settlementDetailVOList));
    }
}

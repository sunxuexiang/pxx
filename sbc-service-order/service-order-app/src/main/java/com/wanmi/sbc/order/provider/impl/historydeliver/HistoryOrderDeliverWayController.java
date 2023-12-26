package com.wanmi.sbc.order.provider.impl.historydeliver;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.provider.historydeliver.HistoryOrderDeliverWayProvider;
import com.wanmi.sbc.order.bean.vo.HistoryOrderDeliverWayQueryVO;
import com.wanmi.sbc.order.bean.vo.HistoryOrderDeliverWayVO;
import com.wanmi.sbc.order.historydeliver.HistoryOrderDeliverWayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
public class HistoryOrderDeliverWayController implements HistoryOrderDeliverWayProvider {

    @Autowired
    private HistoryOrderDeliverWayService historyOrderDeliverWayService;


    @Override
    public BaseResponse add(HistoryOrderDeliverWayVO historyOrderDeliverWayVO) {
        historyOrderDeliverWayService.save(historyOrderDeliverWayVO);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<List<HistoryOrderDeliverWayVO>> queryDeliverWayByStoreIdAndCustomerId(HistoryOrderDeliverWayQueryVO historyOrderDeliverWayQueryVO) {
        List<HistoryOrderDeliverWayVO> historyOrderDeliverWayVOList = historyOrderDeliverWayService.queryDeliverWayByStoreIdAndCustomerId(historyOrderDeliverWayQueryVO);
        return BaseResponse.success(historyOrderDeliverWayVOList);
    }
}

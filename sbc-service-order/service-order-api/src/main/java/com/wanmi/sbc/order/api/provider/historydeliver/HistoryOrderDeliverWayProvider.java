package com.wanmi.sbc.order.api.provider.historydeliver;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.request.historylogisticscompany.HistoryLogisticsCompanyAddRequest;
import com.wanmi.sbc.order.api.request.historylogisticscompany.HistoryLogisticsCompanyDelByIdListRequest;
import com.wanmi.sbc.order.api.request.historylogisticscompany.HistoryLogisticsCompanyDelByIdRequest;
import com.wanmi.sbc.order.api.request.historylogisticscompany.HistoryLogisticsCompanyModifyRequest;
import com.wanmi.sbc.order.api.response.historylogisticscompany.HistoryLogisticsCompanyAddResponse;
import com.wanmi.sbc.order.api.response.historylogisticscompany.HistoryLogisticsCompanyModifyResponse;
import com.wanmi.sbc.order.bean.vo.HistoryOrderDeliverWayQueryVO;
import com.wanmi.sbc.order.bean.vo.HistoryOrderDeliverWayVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @desc  
 * @author shiy  2023/12/16 9:47
*/
@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "HistoryOrderDeliverWayProvider")
public interface HistoryOrderDeliverWayProvider {
	@PostMapping("/order/${application.order.version}/historyOrderDeliverWay/add")
	BaseResponse add(@RequestBody HistoryOrderDeliverWayVO historyOrderDeliverWayVO);

	@PostMapping("/order/${application.order.version}/historyOrderDeliverWay/queryDeliverWayByStoreIdAndCustomerId")
	BaseResponse<List<HistoryOrderDeliverWayVO>> queryDeliverWayByStoreIdAndCustomerId(@RequestBody HistoryOrderDeliverWayQueryVO historyOrderDeliverWayQueryVO);

}


package com.wanmi.ares.provider;

import com.wanmi.ares.base.BaseResponse;
import com.wanmi.ares.request.BuyRankingListRequest;
import com.wanmi.ares.view.trade.BuyRankingListView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "${application.ares.name}", url="${feign.url.ares:#{null}}",contextId = "BugRankingListProvider")
public interface BuyRankingListProvider {

    @PostMapping("/ares/${application.ares.version}/bugRankingList/queryCustomerPhone")
    BaseResponse<List<BuyRankingListView>> getBuyRankingList(@RequestBody BuyRankingListRequest req);
}

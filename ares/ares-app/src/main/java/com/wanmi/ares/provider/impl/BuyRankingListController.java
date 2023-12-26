package com.wanmi.ares.provider.impl;

import com.wanmi.ares.base.BaseResponse;
import com.wanmi.ares.provider.BuyRankingListProvider;
import com.wanmi.ares.report.trade.model.request.BuyRankingListCollect;
import com.wanmi.ares.report.trade.service.BuyRankingListService;
import com.wanmi.ares.request.BuyRankingListRequest;
import com.wanmi.ares.view.trade.BuyRankingListView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BuyRankingListController implements BuyRankingListProvider {

    @Autowired
    private BuyRankingListService buyRankingListService;

    @Override
    public BaseResponse<List<BuyRankingListView>> getBuyRankingList(@RequestBody BuyRankingListRequest req) {
        BuyRankingListCollect buyRankingListCollect = new BuyRankingListCollect();
        BeanUtils.copyProperties(req,buyRankingListCollect);
        List<BuyRankingListView> buyRankingList = buyRankingListService.getBuyRankingList(buyRankingListCollect);
        return BaseResponse.success(buyRankingList);
    }
}

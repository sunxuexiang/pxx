package com.wanmi.ares.report.trade.service;

import com.wanmi.ares.report.goods.model.root.BuyRankingList;
import com.wanmi.ares.report.trade.dao.TradeReportMapper;
import com.wanmi.ares.report.trade.model.request.BuyRankingListCollect;
import com.wanmi.ares.view.trade.BuyRankingListView;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BuyRankingListService {

    @Autowired
    private TradeReportMapper tradeReportMapper;

    public List<BuyRankingListView> getBuyRankingList(BuyRankingListCollect request) {
        List<BuyRankingListView> result = new ArrayList<>();

        List<BuyRankingList> buyRankingList = tradeReportMapper.getBuyRankingList(request);
        if (CollectionUtils.isNotEmpty(buyRankingList)) {

            buyRankingList.forEach(var -> {
                BuyRankingListView view = new BuyRankingListView();
                BeanUtils.copyProperties(var,view);
                result.add(view);
            });
        }
        return result;
    }
}

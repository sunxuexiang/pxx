package com.wanmi.sbc.job.util;

import com.wanmi.ares.base.BaseResponse;
import com.wanmi.ares.request.replay.ReplayTradeBuyerItemNumQuery;
import com.wanmi.sbc.job.BuyerTradeItemNumService;
import com.wanmi.sbc.job.MallPlatformCustomerTradeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-21 15:19
 **/
@RestController(value = "/job/test")
public class JobTestController {

    @Autowired
    private MallPlatformCustomerTradeHandler mallPlatformCustomerTradeHandler;

    @Autowired
    private BuyerTradeItemNumService buyerTradeItemNumService;

    @RequestMapping(method = RequestMethod.GET)
    public void test() throws Exception {
        mallPlatformCustomerTradeHandler.execute("");
    }


    @RequestMapping(value = "buyerGoodsNum", method = RequestMethod.POST)
    public BaseResponse<Integer> buyerGoodsNum(@RequestBody ReplayTradeBuyerItemNumQuery query) {
        return BaseResponse.success(buyerTradeItemNumService.execute(query));
    }
}

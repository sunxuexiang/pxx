package com.wanmi.sbc.order;

import com.wanmi.sbc.order.trade.service.newPileTrade.NewPileTradeDataHandleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class NewPileTradeServiceTest extends BaseTest {

    @Autowired
    NewPileTradeDataHandleService newPileTradeDataHandleService;

    /**
     * 处理南昌仓囤货数据
     */
    @Test
    public void handleNanChangPileData() {
        newPileTradeDataHandleService.handleNanChangPileData("18073180055");
    }

    @Test
    public void handleNanChangTakeData() {
//        newPileTradeDataHandleService.handleNanChangTakeData();
    }

}

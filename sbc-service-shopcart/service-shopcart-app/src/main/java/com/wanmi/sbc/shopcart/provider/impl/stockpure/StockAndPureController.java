package com.wanmi.sbc.shopcart.provider.impl.stockpure;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.shopcart.api.provider.stockpure.StockAndPureProvider;
import com.wanmi.sbc.shopcart.api.request.purchase.*;
import com.wanmi.sbc.shopcart.api.response.purchase.*;
import com.wanmi.sbc.shopcart.bean.dto.PurchaseQueryDTO;
import com.wanmi.sbc.shopcart.cart.ShopCartService;
import com.wanmi.sbc.shopcart.cart.request.ShopCartRequest;
import com.wanmi.sbc.shopcart.stockpure.StockAndPureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc  
 * @author shiy  2023/12/4 9:25
*/
@Slf4j
@Validated
@RestController
public class StockAndPureController implements StockAndPureProvider {

    @Autowired
    private StockAndPureService stockAndPureService;


    /**
     * @param request
     * @desc
     * @author shiy  2023/12/4 9:24
     */
    @Override
    public BaseResponse checkStockPure(StockAndPureChainNodeRequeest request) {
         stockAndPureService.checkStockPure(request);
         return BaseResponse.SUCCESSFUL();
    }
}

package com.wanmi.sbc.shopcart.api.provider.stockpure;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.shopcart.api.request.purchase.*;
import com.wanmi.sbc.shopcart.api.response.purchase.*;
import com.wanmi.sbc.shopcart.bean.dto.PurchaseQueryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @desc  
 * @author shiy  2023/12/4 9:23
*/
@FeignClient(value = "${application.shopcart.name}", url="${feign.url.cart:#{null}}", contextId = "CartStockAndPureProvider")
public interface StockAndPureProvider {

  /**
   * @desc  
   * @author shiy  2023/12/4 9:28
  */
    @PostMapping("/cart/${application.shopcart.version}/stockpure/checkStockPure")
    BaseResponse checkStockPure(@RequestBody @Valid StockAndPureChainNodeRequeest request);

}


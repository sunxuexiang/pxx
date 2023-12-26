package com.wanmi.sbc.couponactivity;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.marketing.api.provider.coupon.CoinActivityProvider;
import com.wanmi.sbc.marketing.api.response.coupon.CoinActivityDetailResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Administrator
 */
@Api(tags = "CoinActivityController")
@RestController
@RequestMapping("/coinActivity")
public class CoinActivityController {

    @Autowired
    private CoinActivityProvider coinActivityProvider;

    @ApiOperation(value = "获取详情")
    @GetMapping("/{id}")
    public BaseResponse<CoinActivityDetailResponse> detail(@PathVariable String id) {
        CoinActivityDetailResponse response = coinActivityProvider.detail(id).getContext();
        return BaseResponse.success(response);
    }
    
    @ApiOperation(value = "订单返鲸币提示")
    @PostMapping("/orderCoinTips")
    public BaseResponse<String> orderCoinTips() {
    	BaseResponse<String> orderCoinTips = coinActivityProvider.orderCoinTips();
        return orderCoinTips;
    }

}

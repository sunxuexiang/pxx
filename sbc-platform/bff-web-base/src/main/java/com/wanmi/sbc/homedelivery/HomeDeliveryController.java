package com.wanmi.sbc.homedelivery;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.api.provider.homedelivery.HomeDeliveryQueryProvider;
import com.wanmi.sbc.setting.api.request.homedelivery.HomeDeliveryListRequest;
import com.wanmi.sbc.setting.api.response.homedelivery.HomeDeliveryListResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: HomeDeliveryController
 * @Description: TODO
 * @Date: 2020/8/4 16:09
 * @Version: 1.0
 */
@Api(description = "配送到家管理API", tags = "HomeDeliveryController")
@RestController
@RequestMapping(value = "/homedelivery")
public class HomeDeliveryController {

    @Autowired
    private HomeDeliveryQueryProvider homeDeliveryQueryProvider;


    @ApiOperation(value = "列表查询配送到家")
    @GetMapping("/list")

    public BaseResponse<HomeDeliveryListResponse> getList() {
        return homeDeliveryQueryProvider.list(HomeDeliveryListRequest.builder().delFlag(DeleteFlag.NO).build());
    }

    @ApiOperation(value = "列表查询配送到家")
    @GetMapping("/list/{storeId}/{companyType}")
    public BaseResponse<HomeDeliveryListResponse> getList(@PathVariable Long storeId,@PathVariable Integer companyType) {
        return homeDeliveryQueryProvider.list(HomeDeliveryListRequest.builder().delFlag(DeleteFlag.NO).storeId(storeId).companyType(companyType).build());
    }
}

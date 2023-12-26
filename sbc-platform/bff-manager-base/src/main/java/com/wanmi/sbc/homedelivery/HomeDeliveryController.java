package com.wanmi.sbc.homedelivery;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.api.provider.homedelivery.HomeDeliveryProvider;
import com.wanmi.sbc.setting.api.provider.homedelivery.HomeDeliveryQueryProvider;
import com.wanmi.sbc.setting.api.request.homedelivery.HomeDeliveryListRequest;
import com.wanmi.sbc.setting.api.request.homedelivery.HomeDeliveryModifyRequest;
import com.wanmi.sbc.setting.api.response.homedelivery.HomeDeliveryByIdResponse;
import com.wanmi.sbc.setting.api.response.homedelivery.HomeDeliveryListResponse;
import com.wanmi.sbc.setting.api.response.homedelivery.HomeDeliveryModifyResponse;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;


@Api(description = "配送到家管理API", tags = "HomeDeliveryController")
@RestController
@RequestMapping(value = "/homedelivery")
public class HomeDeliveryController {

    @Autowired
    private HomeDeliveryQueryProvider homeDeliveryQueryProvider;

    @Autowired
    private HomeDeliveryProvider homeDeliveryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    @ApiOperation(value = "列表查询配送到家")
    @GetMapping("/list")
    public BaseResponse<HomeDeliveryListResponse> getList() {
        return homeDeliveryQueryProvider.list(HomeDeliveryListRequest.builder().delFlag(DeleteFlag.NO).storeId(commonUtil.getStoreIdWithDefault()).companyType(commonUtil.getCompanyType()).build());
    }

    @ApiOperation(value = "修改配送到家")
    @PutMapping("/modify")
    public BaseResponse<HomeDeliveryModifyResponse> modify(@RequestBody @Valid HomeDeliveryModifyRequest modifyReq) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("配送文案管理", "配送文案", "修改配送文案"
                + (Objects.nonNull(modifyReq) &&    Objects.nonNull(modifyReq.getContent()) ?  modifyReq.getContent() : ""));
        if(Objects.isNull(modifyReq.getStoreId())){
            modifyReq.setStoreId(commonUtil.getStoreIdWithDefault());
        }
        return homeDeliveryProvider.modify(modifyReq);
    }

    @ApiOperation(value = "修改配送到店文案")
    @PutMapping("/modifyDeliveryToStoreContent")
    public BaseResponse<HomeDeliveryModifyResponse> modifyDeliveryToStoreContent(@RequestBody @Valid HomeDeliveryModifyRequest modifyReq) {
        if(Objects.isNull(modifyReq.getStoreId())){
            modifyReq.setStoreId(commonUtil.getStoreIdWithDefault());
        }
        return homeDeliveryProvider.modifyByDeliverType(HomeDeliveryModifyRequest.builder().storeId(modifyReq.getStoreId()).deliveryType(7).content(modifyReq.getDeliveryToStoreContent()).build());
    }


    @ApiOperation(value = "列表查询配送到家")
    @GetMapping("/findFirst")
    public BaseResponse<HomeDeliveryByIdResponse> findFirst() {
        HomeDeliveryListResponse response = homeDeliveryQueryProvider
                .list(HomeDeliveryListRequest.builder().delFlag(DeleteFlag.NO).storeId(commonUtil.getStoreIdWithDefault()).build()).getContext();
        return BaseResponse.success(HomeDeliveryByIdResponse.builder()
                .homeDeliveryVO(response.getHomeDeliveryVOList().get(0))
                .build());
    }

    @GetMapping("/findFirst/{storeId}")
    public BaseResponse<HomeDeliveryByIdResponse> findFirstByStoreId(@PathVariable Long storeId) {
        if(null==storeId){
            storeId = commonUtil.getStoreIdWithDefault();
        }
        HomeDeliveryListResponse response = homeDeliveryQueryProvider
                .list(HomeDeliveryListRequest.builder().delFlag(DeleteFlag.NO).storeId(storeId).build()).getContext();
        return BaseResponse.success(HomeDeliveryByIdResponse.builder()
                .homeDeliveryVO(response.getHomeDeliveryVOList().get(0))
                .build());
    }

    @GetMapping("/findFirst/{storeId}/{menuId}")
    public BaseResponse<String> findFirstByStoreId(@PathVariable Long storeId,@PathVariable Integer menuId) {
        if(null==storeId){
            storeId = commonUtil.getStoreIdWithDefault();
        }
        if(menuId==11){
            menuId=2;
        }
        return homeDeliveryQueryProvider.getByMenuId(HomeDeliveryListRequest.builder().delFlag(DeleteFlag.NO).storeId(storeId).deliveryType(menuId).build());
    }

}

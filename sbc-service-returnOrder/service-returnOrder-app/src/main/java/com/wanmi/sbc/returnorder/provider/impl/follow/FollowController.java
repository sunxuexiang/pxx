package com.wanmi.sbc.returnorder.provider.impl.follow;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.api.provider.follow.FollowProvider;
import com.wanmi.sbc.returnorder.api.request.follow.FollowDeleteRequest;
import com.wanmi.sbc.returnorder.api.request.follow.FollowSaveRequest;
import com.wanmi.sbc.returnorder.api.request.follow.InvalidGoodsDeleteRequest;
import com.wanmi.sbc.returnorder.follow.request.GoodsCustomerFollowRequest;
import com.wanmi.sbc.returnorder.follow.service.GoodsCustomerFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
public class FollowController implements FollowProvider {

    @Autowired
    private GoodsCustomerFollowService service;

    @Override
    public BaseResponse save(@RequestBody @Valid FollowSaveRequest request) {

        GoodsCustomerFollowRequest serviceRequest = KsBeanUtil.convert(request,GoodsCustomerFollowRequest.class);

        service.save(serviceRequest);

        return BaseResponse.success(request);
    }

    @Override
    public BaseResponse delete(@RequestBody @Valid FollowDeleteRequest request) {

        GoodsCustomerFollowRequest serviceRequest = KsBeanUtil.convert(request,GoodsCustomerFollowRequest.class);

        service.delete(serviceRequest);

        return BaseResponse.success(request);
    }

    @Override
    public BaseResponse deleteInvalidGoods(@RequestBody @Valid InvalidGoodsDeleteRequest request) {

        GoodsCustomerFollowRequest serviceRequest = KsBeanUtil.convert(request,GoodsCustomerFollowRequest.class);

        service.deleteInvalidGoods(serviceRequest);

        return BaseResponse.success(request);
    }
}

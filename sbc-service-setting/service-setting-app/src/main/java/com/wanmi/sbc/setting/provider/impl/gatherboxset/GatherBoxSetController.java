package com.wanmi.sbc.setting.provider.impl.gatherboxset;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.setting.api.provider.gatherboxset.GatherBoxSetProvider;
import com.wanmi.sbc.setting.api.request.gatherboxset.GatherBoxSetAddRequest;
import com.wanmi.sbc.setting.api.response.gatherboxset.GatherBoxSetBannerResponse;
import com.wanmi.sbc.setting.api.response.gatherboxset.GatherBoxSetInfoResponse;
import com.wanmi.sbc.setting.gatherboxset.model.root.GatherBoxSet;
import com.wanmi.sbc.setting.gatherboxset.service.GatherBoxSetService;
import com.wanmi.sbc.setting.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@Validated
public class GatherBoxSetController implements GatherBoxSetProvider {

    @Autowired
    private GatherBoxSetService gatherBoxSetService;

    @Autowired
    private RedisService redisService;
    @Override
    public BaseResponse add(@Valid GatherBoxSetAddRequest request) {
        GatherBoxSet gatherBoxSet=gatherBoxSetService.getGatherBoxSet();
        if(Objects.isNull(gatherBoxSet)){
            gatherBoxSet=new GatherBoxSet();
        }
        gatherBoxSet.setCreateTime(LocalDateTime.now());
        gatherBoxSet.setUpdateTime(LocalDateTime.now());
        if(Objects.nonNull(request.getSkuNum())) {
            gatherBoxSet.setSkuNum(request.getSkuNum());
            redisService.setString(CacheKeyConstant.GATHER_BOX_SET, gatherBoxSet.getSkuNum().toString());
        }else{
            gatherBoxSet.setBanner(request.getBanner());
        }
        gatherBoxSetService.modify(gatherBoxSet);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<GatherBoxSetInfoResponse> getGatherBoxSetInfo() {
        GatherBoxSetInfoResponse gatherBoxSetInfoResponse=new GatherBoxSetInfoResponse();
        if (redisService.hasKey(CacheKeyConstant.GATHER_BOX_SET)) {
            gatherBoxSetInfoResponse.setSkuNum(Long.parseLong(redisService.getString(CacheKeyConstant.GATHER_BOX_SET)));
            return BaseResponse.success(gatherBoxSetInfoResponse);
        }
        GatherBoxSet gatherBoxSet=gatherBoxSetService.getGatherBoxSet();
        if(Objects.nonNull(gatherBoxSet)){
            gatherBoxSetInfoResponse.setSkuNum(gatherBoxSet.getSkuNum());
        }
       return BaseResponse.success(gatherBoxSetInfoResponse);
    }

    @Override
    public BaseResponse<GatherBoxSetBannerResponse> getBulkBanner() {
        GatherBoxSetBannerResponse gatherBoxSetBannerResponse=new GatherBoxSetBannerResponse();
        GatherBoxSet gatherBoxSet=gatherBoxSetService.getGatherBoxSet();
        if(Objects.nonNull(gatherBoxSet)){
            gatherBoxSetBannerResponse.setBanner(gatherBoxSet.getBanner());
        }
        return BaseResponse.success(gatherBoxSetBannerResponse);
    }
}

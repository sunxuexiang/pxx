package com.wanmi.sbc.setting.provider.impl.hotstylemoments;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.hotstylemoments.HotStyleMomentsProvider;
import com.wanmi.sbc.setting.api.request.hotstylemoments.*;
import com.wanmi.sbc.setting.hotstylemoments.request.HotStyleMomentsSaveRequest;
import com.wanmi.sbc.setting.hotstylemoments.service.HotStyleMomentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 爆款时刻操作服务实现类
 * @author: XinJiang
 * @time: 2022/5/9 21:47
 */
@Validated
@RestController
public class HotStyleMomentsController implements HotStyleMomentsProvider {

    @Autowired
    private HotStyleMomentsService hotStyleMomentsService;

    @Override
    public BaseResponse add(HotStyleMomentsAddRequest request) {
        hotStyleMomentsService.addHotStyleMoments(KsBeanUtil.convert(request, HotStyleMomentsSaveRequest.class));
        this.fillRedis();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse modify(HotStyleMomentsModifyRequest request) {
        hotStyleMomentsService.modifyHotStyleMoments(KsBeanUtil.convert(request, HotStyleMomentsSaveRequest.class));
        this.fillRedis();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse delById(HotStyleMomentsDelByIdRequest request) {
        hotStyleMomentsService.delById(request.getDeletePerson(), request.getDeleteTime(), request.getHotId());
        this.fillRedis();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse delByIds(HotStyleMomentsDelByIdRequest request) {
        hotStyleMomentsService.delByIds(request.getDeletePerson(), request.getDeleteTime(), request.getHotIds());
        this.fillRedis();
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 刷新redis缓存
     */
    @Override
    public BaseResponse fillRedis(){
        hotStyleMomentsService.fillRedis();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse terminationById(HotStyleMomentsTerminationRequest request) {
        hotStyleMomentsService.terminationHotStyleMoments(request.getHotId());
        this.fillRedis();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse pauseById(HotStyleMomentsPauseRequest request) {
        hotStyleMomentsService.pauseHotStyleMoments(request.getHotId(), request.getIsPause());
        this.fillRedis();
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse earlyStart(Long hotId) {
        hotStyleMomentsService.earlyStart(hotId);
        this.fillRedis();
        return BaseResponse.SUCCESSFUL();
    }
}

package com.wanmi.sbc.setting.provider.impl.hotstylemoments;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.hotstylemoments.HotStyleMomentsQueryProvider;
import com.wanmi.sbc.setting.api.request.hotstylemoments.HotStyleMomentsCheckTimeRequest;
import com.wanmi.sbc.setting.api.request.hotstylemoments.HotStyleMomentsGetByIdRequest;
import com.wanmi.sbc.setting.api.request.hotstylemoments.HotStyleMomentsQueryRequest;
import com.wanmi.sbc.setting.api.response.hotstylemoments.HotStyleMomentsListResponse;
import com.wanmi.sbc.setting.api.response.hotstylemoments.HotStyleMomentsPageResponse;
import com.wanmi.sbc.setting.api.response.hotstylemoments.HotStyleMomentsResponse;
import com.wanmi.sbc.setting.bean.vo.HotStyleMomentsVO;
import com.wanmi.sbc.setting.hotstylemoments.model.root.HotStyleMoments;
import com.wanmi.sbc.setting.hotstylemoments.service.HotStyleMomentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 爆款时刻查询接口实现类
 * @author: XinJiang
 * @time: 2022/5/9 21:47
 */
@Validated
@RestController
public class HotStyleMomentsQueryController implements HotStyleMomentsQueryProvider {

    @Autowired
    private HotStyleMomentsService hotStyleMomentsService;

    @Override
    public BaseResponse<HotStyleMomentsResponse> getById(HotStyleMomentsGetByIdRequest request) {
        return BaseResponse.success(KsBeanUtil.convert(hotStyleMomentsService.getById(request.getHotId()), HotStyleMomentsResponse.class));
    }

    @Override
    public BaseResponse<HotStyleMomentsListResponse> getList(HotStyleMomentsQueryRequest request) {
        return BaseResponse.success(HotStyleMomentsListResponse
                .builder()
                .hotStyleMomentsVOS(KsBeanUtil.convert(hotStyleMomentsService.getList(request), HotStyleMomentsVO.class))
                .build());
    }

    @Override
    public BaseResponse<HotStyleMomentsPageResponse> getPage(HotStyleMomentsQueryRequest request) {
        return BaseResponse.success(HotStyleMomentsPageResponse
                .builder()
                .hotStyleMomentsPage(KsBeanUtil.convertPage(hotStyleMomentsService.getPage(request), HotStyleMomentsVO.class))
                .build());
    }

    @Override
    public BaseResponse<HotStyleMomentsListResponse> checkTime(HotStyleMomentsCheckTimeRequest request) {
        return BaseResponse.success(HotStyleMomentsListResponse
                .builder()
                .hotStyleMomentsVOS(KsBeanUtil.convert(hotStyleMomentsService.checkTime(request), HotStyleMomentsVO.class))
                .build());
    }
}

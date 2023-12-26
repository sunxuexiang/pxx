package com.wanmi.sbc.live.provider.impl.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.provider.goods.LiveStreamGoodsQueryProvider;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsListRequest;
import com.wanmi.sbc.live.api.response.goods.LiveStreamGoodsInfoListResponse;
import com.wanmi.sbc.live.api.response.goods.LiveStreamGoodsListResponse;
import com.wanmi.sbc.live.bean.vo.LiveGoodsVO;
import com.wanmi.sbc.live.bean.vo.LiveStreamGoodsVO;
import com.wanmi.sbc.live.goods.service.LiveStreamGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>直播商品查询服务接口实现</p>
 */
@RestController
@Validated
public class LiveStreamGoodsQueryController implements LiveStreamGoodsQueryProvider {
    @Autowired
    private LiveStreamGoodsService liveStreamGoodsService;
    @Override
    public BaseResponse<LiveStreamGoodsListResponse> list(@RequestBody LiveStreamGoodsListRequest liveStreamGoodsListRequest) {
        List<LiveStreamGoodsVO> liveStreamGoodsVOList=liveStreamGoodsService.getStreamGoodsByReq(liveStreamGoodsListRequest);
        return BaseResponse.success(new LiveStreamGoodsListResponse(liveStreamGoodsVOList));
    }

    @Override
    public BaseResponse<LiveStreamGoodsInfoListResponse> listInfo(LiveStreamGoodsListRequest liveStreamGoodsListRequest) {
        List<LiveGoodsVO> liveStreamGoodsVOList=liveStreamGoodsService.getStreamGoodsInfoByReq(liveStreamGoodsListRequest);
        return BaseResponse.success(new LiveStreamGoodsInfoListResponse(liveStreamGoodsVOList));
    }
}

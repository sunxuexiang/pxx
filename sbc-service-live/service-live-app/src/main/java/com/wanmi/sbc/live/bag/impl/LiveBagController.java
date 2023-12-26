package com.wanmi.sbc.live.bag.impl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.provider.bag.LiveBagProvider;
import com.wanmi.sbc.live.api.request.bag.LiveBagAddRequest;
import com.wanmi.sbc.live.api.request.bag.LiveBagListRequest;
import com.wanmi.sbc.live.api.request.bag.LiveBagModifyRequest;
import com.wanmi.sbc.live.api.request.bag.LiveBagPageRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamPageRequest;
import com.wanmi.sbc.live.api.response.bag.LiveBagInfoResponse;
import com.wanmi.sbc.live.api.response.bag.LiveBagListResponse;
import com.wanmi.sbc.live.bag.model.root.LiveBag;
import com.wanmi.sbc.live.bag.service.LiveBagService;
import com.wanmi.sbc.live.bean.vo.LiveBagVO;

import io.swagger.annotations.Api;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>福袋服务接口实现</p>
 *
 * @author 刘丹（ldalc.com） Automatic Generator
 * @version 1.0
 * @date 2022-09-20 13:04:21
 * @package com.wanmi.sbc.live.provider.impl.bag
 */
@Api(description = "直播间福袋管理", tags = "LiveBagController")
@RestController
@Validated
public class LiveBagController implements LiveBagProvider {

    @Autowired
    private LiveBagService liveBagService;

    @Override
    public BaseResponse add(@Valid @RequestBody LiveBagAddRequest addRequest) {

        liveBagService.add(addRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse modify(@Valid @RequestBody LiveBagModifyRequest modifyRequest) {
        liveBagService.modify(modifyRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<LiveBagInfoResponse> getInfo( @RequestBody Long liveBagId) {
        LiveBag liveBag = liveBagService.getInfo(liveBagId);
        if (null == liveBag) {
            return BaseResponse.error("福袋找不到");
        }
        LiveBagInfoResponse liveBagInfoResponse = new LiveBagInfoResponse();
        BeanUtils.copyProperties(liveBag, liveBagInfoResponse);
        return BaseResponse.success(liveBagInfoResponse);
    }

    @Override
    public BaseResponse getPage(@Valid @RequestBody LiveBagPageRequest pageRequest) {
        Page<LiveBagVO> page = liveBagService.getPage(pageRequest);
        return BaseResponse.success(page);
    }

    @Override
    public BaseResponse liveBagRecordList(@RequestBody LiveBagListRequest request) {
        List<LiveBagVO> liveBagVOS=liveBagService.liveBagRecordList(request);
        return BaseResponse.success(liveBagVOS);
    }

    @Override
    public BaseResponse<LiveBagListResponse> liveBagRoomList(LiveBagListRequest request) {
        LiveBagListResponse liveBagListResponse=new LiveBagListResponse();
        List<LiveBagVO> liveBagVOS=liveBagService.liveBagRoomList(request);
        liveBagListResponse.setLiveBagVOS(liveBagVOS);
        return BaseResponse.success(liveBagListResponse);
    }

//    @Override
//    public BaseResponse reset(Long liveBagId) {
//
//        LiveBag liveBag = liveBagService.getInfo(liveBagId);
//        LiveBagAddRequest addRequest = new LiveBagAddRequest();
//        BeanUtils.copyProperties(liveBag, addRequest);
//        addRequest.setLiveBagId(null);
//        add(addRequest);
//        return BaseResponse.SUCCESSFUL();
//    }
//
//    @Override
//    public BaseResponse draw(Long liveBagId) {
//        return null;
//    }
}
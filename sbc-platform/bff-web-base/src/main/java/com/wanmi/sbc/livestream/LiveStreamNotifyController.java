package com.wanmi.sbc.livestream;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.provider.stream.LiveStreamProvider;
import com.wanmi.sbc.live.api.provider.stream.LiveStreamQueryProvider;
import com.wanmi.sbc.live.api.request.stream.LiveStreamAddRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamInfoRequest;
import com.wanmi.sbc.live.api.request.stream.LiveStreamSetRequest;
import com.wanmi.sbc.live.api.response.stream.LiveStreamInfoResponse;
import com.wanmi.sbc.live.bean.vo.LiveStreamVO;
import com.wanmi.sbc.livestream.response.LiveStreamNotifyRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Api(description = "新版直播管理API", tags = "LiveStreamNotifyController")
@RestController
@Slf4j
@RequestMapping(value = "/liveStream")
public class LiveStreamNotifyController {
    @Autowired
    private LiveStreamQueryProvider liveStreamQueryProvider;
    @Autowired
    private LiveStreamProvider liveStreamProvider;
    @ApiOperation(value = "录制回调")
    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public BaseResponse notify(@RequestBody LiveStreamNotifyRequest notifyRequest) {
        log.info("录制回调开始------");
        if(Objects.nonNull(notifyRequest)){
            log.info("录制回调数据------"+notifyRequest.toString());
            if(Objects.nonNull(notifyRequest.getStream_id())){
                LiveStreamInfoRequest liveStreamInfoReq=new LiveStreamInfoRequest();
                liveStreamInfoReq.setStreamName(notifyRequest.getStream_id());
                LiveStreamInfoResponse streamInfoResponse=liveStreamQueryProvider.streamInfo(liveStreamInfoReq).getContext();
                if(Objects.nonNull(streamInfoResponse.getContent())){
                    LiveStreamVO content=streamInfoResponse.getContent();
                    if(Objects.nonNull(content)){
                        LiveStreamAddRequest mobileAddReq=new LiveStreamAddRequest();
                        mobileAddReq.setLiveId(content.getLiveId());
                        mobileAddReq.setMediaUrl(notifyRequest.getVideo_url());
                        liveStreamProvider.updateRoom(mobileAddReq);
                        log.info("录制回调数据结束，流名------"+notifyRequest.getStream_id());
                    }

                }
            }
        }
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "推断流回调")
    @RequestMapping(value = "/cutoffStream", method = RequestMethod.POST)
    public BaseResponse cutoffStream(@RequestBody LiveStreamNotifyRequest notifyRequest) {

        log.info("推断流回调开始------");
        if(Objects.nonNull(notifyRequest)) {
            log.info("推断流回调数据------" + notifyRequest.toString());
            if (Objects.nonNull(notifyRequest.getStream_id())) {
                LiveStreamInfoRequest liveStreamInfoReq=new LiveStreamInfoRequest();
                liveStreamInfoReq.setStreamName(notifyRequest.getStream_id());
                LiveStreamInfoResponse streamInfoResponse=liveStreamQueryProvider.streamInfo(liveStreamInfoReq).getContext();
                if(Objects.nonNull(streamInfoResponse.getContent())){
                    LiveStreamVO content=streamInfoResponse.getContent();
                    if(Objects.nonNull(content)){
                        LiveStreamAddRequest mobileAddReq=new LiveStreamAddRequest();
                        mobileAddReq.setLiveId(content.getLiveId());
                        mobileAddReq.setEventType(notifyRequest.getEvent_type());
                        mobileAddReq.setErrcode(notifyRequest.getErrcode());
                        mobileAddReq.setErrmsg(notifyRequest.getErrmsg());
                        mobileAddReq.setEventTime(notifyRequest.getEvent_time());
                        liveStreamProvider.updateRoom(mobileAddReq);
                        log.info("推断流回调数据数据结束，流名------"+notifyRequest.getStream_id());

                        if(notifyRequest.getEvent_type()==0){
                            //断流通知
                            LiveStreamSetRequest streamSetRequest=new LiveStreamSetRequest();
                            streamSetRequest.setLiveId(content.getLiveId());
                            liveStreamProvider.cutoffStream(streamSetRequest);
                        }
                    }

                }
            }
        }
        return BaseResponse.SUCCESSFUL();
    }
}

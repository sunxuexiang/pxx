package com.wanmi.sbc.job;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.bean.enums.LiveRoomStatus;
import com.wanmi.sbc.live.api.provider.stream.LiveStreamProvider;
import com.wanmi.sbc.live.api.provider.stream.LiveStreamQueryProvider;
import com.wanmi.sbc.live.api.request.stream.LiveStreamPageRequest;
import com.wanmi.sbc.live.api.response.stream.LiveStreamPageResponse;
import com.wanmi.sbc.live.bean.vo.LiveStreamVO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 直播间在线人数更新任务
 */
@Component
@Slf4j
@JobHandler(value = "liveRoomPeopleJobHandler")
public class LiveRoomPeopleJobHandler extends IJobHandler {

    @Autowired
    private LiveStreamProvider liveStreamProvider;

    @Autowired
    private LiveStreamQueryProvider liveStreamQueryProvider;

    private boolean isExecuting = false;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        log.info("更新直播中直播间在线人数任务开始...");
        if (isExecuting) {
            return SUCCESS;
        }
        isExecuting = true;
        LiveStreamPageRequest queryParam = new LiveStreamPageRequest();
        queryParam.setPageNum(0);
        queryParam.setPageSize(100);
        // 按直播状态查询：1=查询正在直播的列表；  直播状态0: 未开始 1: 直播中 2: 已结束
        queryParam.setLiveStatus(1);
        BaseResponse<LiveStreamPageResponse> response = null;
        try {
            response = liveStreamQueryProvider.listPage(queryParam);
        }
        catch (Exception e) {
            log.error("liveRoomPeopleJobHandler 查询在播直播间列表异常");
        }
        if (response == null || response.getContext() == null || ObjectUtils.isEmpty(response.getContext().getContent())) {
            isExecuting = false;
            return SUCCESS;
        }
        List<LiveStreamVO> liveList = response.getContext().getContent();
        liveList.stream().forEach(live -> {
            try {
                liveStreamProvider.updateLiveStreamPeopleNum(live);
            }
            catch (Exception e) {
                log.error("liveRoomPeopleJobHandler 更新直播间在线人数失败", e);
            }
        });
        isExecuting = false;
        return SUCCESS;
    }
}

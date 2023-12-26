package com.wanmi.sbc.setting.videomanagement.service;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.videomanagement.VideoManagementPageRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceCommonMessageResponse;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoSearchRecordResponse;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceCommonMessage;
import com.wanmi.sbc.setting.videomanagement.model.root.VideoSearchRecord;
import com.wanmi.sbc.setting.videomanagement.repository.VideoSearchRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 短视频搜索服务类
 */
@Slf4j
@Service
public class VideoSearchRecordService {

    @Autowired
    private VideoSearchRecordRepository videoSearchRecordRepository;

    public void saveSearchRecord(VideoManagementPageRequest videoManagementPageReq) {
        if (StringUtils.isEmpty(videoManagementPageReq.getCustomerId()) || StringUtils.isEmpty(videoManagementPageReq.getVideoName())) {
            return;
        }
        List<VideoSearchRecord> dbVideoList = videoSearchRecordRepository.findByCustomerIdAndContent(videoManagementPageReq.getCustomerId(), videoManagementPageReq.getVideoName());
        if (!ObjectUtils.isEmpty(dbVideoList)) {
            VideoSearchRecord dbVideo = dbVideoList.remove(0);
            dbVideo.setSearchTime(System.currentTimeMillis() / 1000);
            videoSearchRecordRepository.save(dbVideo);

            dbVideoList.forEach(video -> {
                videoSearchRecordRepository.delete(video);
            });
        }
        else {
            VideoSearchRecord dbVideo = new VideoSearchRecord();
            dbVideo.setCustomerId(videoManagementPageReq.getCustomerId());
            dbVideo.setSearchTime(System.currentTimeMillis() / 1000);
            dbVideo.setContent(videoManagementPageReq.getVideoName());
            videoSearchRecordRepository.save(dbVideo);
        }
    }

    public MicroServicePage<VideoSearchRecordResponse> getUserSearchList (VideoManagementPageRequest request) {
        Sort sort = new Sort(Sort.Direction.DESC, "search_time");
        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize(), sort);
        Page<VideoSearchRecord> videoSearchRecordPage = videoSearchRecordRepository.findByCustomerId(request.getCustomerId(), pageable);
        MicroServicePage<VideoSearchRecordResponse> microServicePage = KsBeanUtil.convertPage(videoSearchRecordPage, VideoSearchRecordResponse.class);
        return microServicePage;
    }
}

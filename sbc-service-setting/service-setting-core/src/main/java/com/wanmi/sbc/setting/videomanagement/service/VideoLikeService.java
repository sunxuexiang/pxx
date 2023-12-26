package com.wanmi.sbc.setting.videomanagement.service;

import com.wanmi.sbc.setting.api.request.videomanagement.VideoLikeQueryRequest;
import com.wanmi.sbc.setting.api.request.videomanagement.VideoManagementQueryRequest;
import com.wanmi.sbc.setting.videomanagement.model.root.VideoLike;
import com.wanmi.sbc.setting.videomanagement.model.root.VideoManagement;
import com.wanmi.sbc.setting.videomanagement.repository.VideoLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>视频点赞</p>
 *
 * @author zhaowei
 * @date 2021-04-19 14:24:26
 */
@Service("VideoLikeService")
public class VideoLikeService {
    @Autowired
    private VideoLikeRepository videoLikeRepository;

    /**
     * 点赞
     *
     * @author zhaowei
     */
    @Transactional
    public VideoLike add(VideoLike entity) {
        videoLikeRepository.saveVideoLike(entity.getVideoId(),entity.getCustomerId());
        return entity;
    }

    /**
     * 取消点赞
     *
     * @author zhaowei
     */
    @Transactional
    public void delete(VideoLike entity) {
        videoLikeRepository.deleteVideoLike(entity.getVideoId(),entity.getCustomerId());
    }

    /**
     * 获取视频点赞数
     *
     * @param
     * @return: java.lang.Long
     */
    public Long getVideoLikeNumById(Long videoId) {
       return videoLikeRepository.countByVideoId(videoId);
    }

    public Integer queryByIdOrCustomerId(Long  videoId,String customerId){
        return videoLikeRepository.queryByIdOrCustomerId(videoId,customerId);
    }


    /**
     * 分页查询视频管理
     *
     * @author zhaowei
     */
    public Page<VideoLike> page(VideoLikeQueryRequest queryReq) {
        return videoLikeRepository.findAll(
                VideoLikeWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }

}


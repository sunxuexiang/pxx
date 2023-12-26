package com.wanmi.sbc.setting.videomanagement.service;

import com.wanmi.sbc.setting.api.request.videomanagement.VideoFollowQueryRequest;
import com.wanmi.sbc.setting.api.request.videomanagement.VideoLikeQueryRequest;
import com.wanmi.sbc.setting.videomanagement.model.root.VideoFollow;
import com.wanmi.sbc.setting.videomanagement.model.root.VideoLike;
import com.wanmi.sbc.setting.videomanagement.repository.VideoFollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("VideoFollowService")
public class VideoFollowService {

    @Autowired
    private VideoFollowRepository videoFollowRepository;


    /**
     * 获取我关注的人
     * @return
     */
    public Page<VideoFollow> getVideoFollow(VideoFollowQueryRequest followQueryRequest){
        Page<VideoFollow> all = videoFollowRepository.findAll(
                VideoFollowWhereCriteriaBuilder.build(followQueryRequest),
                followQueryRequest.getPageRequest());
        return all;
    }

    public Integer getVideoFollowByFollowCustomerIdAndCoverFollowCustomerId(VideoFollowQueryRequest followQueryRequest){
        return videoFollowRepository.getVideoFollowByFollowCustomerIdAndCoverFollowCustomerId(followQueryRequest.getFollowCustomerId(),followQueryRequest.getStoreId());
    }

    public List<Long> getVideoFollowByFollowCustomerId(VideoFollowQueryRequest followQueryRequest){
        return videoFollowRepository.getVideoFollowByFollowCustomerId(followQueryRequest.getFollowCustomerId());
    }

    @Transactional
    public int saveVideoFollow(VideoFollow videoFollow){
       return videoFollowRepository.saveVideoFollow(videoFollow.getFollowCustomerId(),videoFollow.getCoverFollowCustomerId(), videoFollow.getStoreId());
    }

    @Transactional
    public void deleteVideoFollow(VideoFollow videoFollow){
        videoFollowRepository.deleteVideoFollow(videoFollow.getFollowCustomerId(),videoFollow.getStoreId());
    }

    public void save(VideoFollow videoFollow){
        videoFollowRepository.save(videoFollow);
    }
}

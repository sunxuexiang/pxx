package com.wanmi.sbc.setting.videomanagement.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.videomanagement.VideoManagementQueryRequest;
import com.wanmi.sbc.setting.bean.vo.VideoManagementVO;
import com.wanmi.sbc.setting.videomanagement.model.root.VideoManagement;
import com.wanmi.sbc.setting.videomanagement.repository.VideoFollowRepository;
import com.wanmi.sbc.setting.videomanagement.repository.VideoManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <p>视频管理业务逻辑</p>
 *
 * @author zhaowei
 * @date 2021-04-17 17:47:22
 */
@Service("VideoManagementService")
public class VideoManagementService {

    @Autowired
    private VideoManagementRepository videoManagementRepository;

    @Autowired
    private VideoFollowRepository videoFollowRepository;

    /**
     * 新增视频管理
     *
     * @author zhaowei
     */
    @Transactional
    public VideoManagement add(VideoManagement entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setPlayFew(0l);
        videoManagementRepository.save(entity);
        return entity;
    }

    /**
     * 修改视频管理
     *
     * @author zhaowei
     */
    @Transactional
    public void modify(VideoManagement entity) {
        VideoManagement videoManagement = videoManagementRepository.findById(entity.getVideoId()).orElse(null);
        if (Objects.nonNull(videoManagement)) {
            videoManagement.setVideoName(entity.getVideoName());
            videoManagement.setResourceKey(entity.getResourceKey());
            videoManagement.setArtworkUrl(entity.getArtworkUrl());
            videoManagement.setUpdateTime(LocalDateTime.now());
            videoManagement.setCoverImg(entity.getCoverImg());
            videoManagement.setGoodsId(entity.getGoodsId());
            videoManagement.setGoodsInfoId(entity.getGoodsInfoId());
            videoManagement.setGoodsLink(entity.getGoodsLink());
            videoManagementRepository.save(videoManagement);
        }
    }

    @Transactional
    public void updateStateById(VideoManagement entity) {
        VideoManagement videoManagement = videoManagementRepository.findById(entity.getVideoId()).orElse(null);
        if (Objects.nonNull(videoManagement)) {
            videoManagement.setState(entity.getState());
            videoManagement.setUpdateTime(LocalDateTime.now());
            videoManagementRepository.save(videoManagement);
        }
    }


    /**
     * 单个删除视频管理
     *
     * @author zhaowei
     */
    @Transactional
    public void deleteById(VideoManagement entity) {
        videoManagementRepository.deleteById(entity.getVideoId());
    }

    /**
     * 播放量增加
     *
     * @author zhaowei
     */
    @Transactional
    public void playFewById(VideoManagement entity) {
        videoManagementRepository.playFewById(entity.getVideoId());
    }


    /**
     * 批量删除视频管理
     *
     * @author zhaowei
     */
    @Transactional
    public void deleteByIdList(List<VideoManagement> infos) {
        videoManagementRepository.saveAll(infos);
    }

    /**
     * 单个查询视频管理
     *
     * @author zhaowei
     */
    public VideoManagement getOne(Long id) {
        return videoManagementRepository.findByVideoIdAndDelFlag(id, DeleteFlag.NO)
                .orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "视频管理不存在"));
    }

    /**
     * 分页查询视频管理
     *
     * @author zhaowei
     */
    public Page<VideoManagement> page(VideoManagementQueryRequest queryReq) {
        queryReq.setSortColumn("createTime");
        queryReq.setSortRole("desc");
        return videoManagementRepository.findAll(
                VideoManagementWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }

    /**
     * 列表查询视频管理
     *
     * @author zhaowei
     */
    public List<VideoManagement> list(VideoManagementQueryRequest queryReq) {
        queryReq.setSortColumn("createTime");
        queryReq.setSortRole("desc");
        return videoManagementRepository.findAll(VideoManagementWhereCriteriaBuilder.build(queryReq));
    }

    @Transactional
    @LcnTransaction
    public Object saveAll(List<VideoManagement> list){
        return videoManagementRepository.saveAll(list);
    }

    /**
     * 将实体包装成VO
     *
     * @author zhaowei
     */
    public VideoManagementVO wrapperVo(VideoManagement videoManagement) {
        if (videoManagement != null) {
            VideoManagementVO videoManagementVO = KsBeanUtil.convert(videoManagement, VideoManagementVO.class);
            return videoManagementVO;
        }
        return null;
    }
}


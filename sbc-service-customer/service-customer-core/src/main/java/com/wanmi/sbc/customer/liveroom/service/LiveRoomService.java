package com.wanmi.sbc.customer.liveroom.service;


import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.util.LiveErrCodeUtil;
import com.wanmi.sbc.common.util.MediaIdUtil;
import com.wanmi.sbc.customer.api.request.liveroom.LiveRoomUpdateRequest;
import com.wanmi.sbc.customer.api.response.liveroom.LiveRoomCreateResponse;
import com.wanmi.sbc.customer.bean.enums.LiveRoomStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.wanmi.sbc.customer.liveroom.repository.LiveRoomRepository;
import com.wanmi.sbc.customer.liveroom.model.root.LiveRoom;
import com.wanmi.sbc.customer.api.request.liveroom.LiveRoomQueryRequest;
import com.wanmi.sbc.customer.bean.vo.LiveRoomVO;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.enums.DeleteFlag;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>直播间业务逻辑</p>
 *
 * @author zwb
 * @date 2020-06-06 18:28:57
 */
@Service("LiveRoomService")
public class LiveRoomService {
    @Autowired
    private LiveRoomRepository liveRoomRepository;

    private static final Logger log = LoggerFactory.getLogger(LiveRoomService.class);

    private String createLiveRoomListUrl = "https://api.weixin.qq.com/wxaapi/broadcast/room/create?access_token=";

    @Autowired
    private RestTemplate restTemplate;




    /**
     * 创建直播间
     *
     * @author zwb
     */
    @Transactional
    public LiveRoom add(LiveRoom entity, String accessToken) {
        //拼接Url
        String url = createLiveRoomListUrl+accessToken;
        Map<String, Object> map = new HashMap<>();
        map.put("name", entity.getName());
        //时间转换成秒
        map.put("startTime", entity.getStartTime().toEpochSecond(ZoneOffset.of("+8")));
        map.put("endTime", entity.getEndTime().toEpochSecond(ZoneOffset.of("+8")));
        map.put("anchorName", entity.getAnchorName());
        map.put("anchorWechat", entity.getAnchorWechat());
        map.put("screenType", entity.getScreenType());
        map.put("type", entity.getType());
        map.put("closeLike", entity.getCloseLike());
        map.put("closeGoods", entity.getCloseGoods());
        map.put("closeComment", entity.getCloseComment());
        //调用微信接口上传文件，查询mediaId
        String coverImg = null;
        String shareImg = null;
        try {
            //将图片保存到本地，再根据图片的路径去获取media_id
            String coverImgUrl = MediaIdUtil.uploadURL(entity.getCoverImg());
            String shareImgUrl = MediaIdUtil.uploadURL(entity.getShareImg());
            coverImg = MediaIdUtil.uploadFile(coverImgUrl, accessToken, "image");
            shareImg = MediaIdUtil.uploadFile(shareImgUrl, accessToken, "image");
            //删除本地图片
           File file = new File(coverImgUrl);
            if (file.exists()){
                file.delete();
            }
            File shareImgFile = new File(shareImgUrl);
            if (shareImgFile.exists()){
                shareImgFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("shareImg", shareImg);
        map.put("coverImg", coverImg);
        map.put("closeReplay", 0);
        // feedsImg字段官方改为必填的，取shareImg字段值
        map.put("feedsImg", shareImg);

        //请求微信创建直播间接口
        String result = restTemplate.postForObject(url, map, String.class);
        LiveRoomCreateResponse resp = JSONObject.parseObject(result, LiveRoomCreateResponse.class);
        if (resp.getErrcode() != 0) {
            log.error("微信创建直播间异常，返回信息：" + resp.toString());
            throw new SbcRuntimeException(resp.getErrcode().toString(), LiveErrCodeUtil.getErrCodeMessage(resp.getErrcode()));
        }
        entity.setLiveStatus(LiveRoomStatus.THREE);
        entity.setRoomId(resp.getRoomId());
        entity.setRecommend(0);
        liveRoomRepository.save(entity);
        return entity;
    }

    /**
     * 修改直播间
     *
     * @author zwb
     */
    @Transactional
    public LiveRoom modify(LiveRoom entity) {
        liveRoomRepository.save(entity);
        return entity;
    }

    /**
     * 定时任务修改直播间状态
     *
     * @author zwb
     */
    @Transactional
    public void update(LiveRoomUpdateRequest request) {
        Map<LiveRoomStatus, List<LiveRoomUpdateRequest>> liveRoomList = request.getLiveRoomList();
        for (LiveRoomStatus liveRoomStatus : liveRoomList.keySet()) {
            List<Long> roomsIdList = liveRoomList.get(liveRoomStatus).stream()
                    .map(LiveRoomUpdateRequest::getRoomId)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(roomsIdList)) {
                liveRoomRepository.updateStatusByRoomIdList(liveRoomStatus, roomsIdList);
            }
        }


    }


    /**
     * 单个删除直播间
     *
     * @author zwb
     */
    @Transactional
    public void deleteById(LiveRoom entity) {
        liveRoomRepository.save(entity);
    }

    /**
     * 批量删除直播间
     *
     * @author zwb
     */
    @Transactional
    public void deleteByIdList(List<LiveRoom> infos) {
        liveRoomRepository.saveAll(infos);
    }

    /**
     * 单个查询直播间
     *
     * @author zwb
     */
    public LiveRoom getOne(Long id) {
        return liveRoomRepository.findById(id)
                .orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "直播间不存在"));
    }
    /**
     * 单个查询直播间
     *
     * @author zwb
     */
    public LiveRoom getOneByRoomId(Long id) {
        return liveRoomRepository.findByRoomIdAndDelFlag(id, DeleteFlag.NO)
                .orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "直播间不存在"));
    }

    /**
     * 分页查询直播间
     *
     * @author zwb
     */
    public Page<LiveRoom> page(LiveRoomQueryRequest queryReq) {
        return liveRoomRepository.findAll(
                LiveRoomWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }

    /**
     * 列表查询直播间
     *
     * @author zwb
     */
    public List<LiveRoom> list(LiveRoomQueryRequest queryReq) {
        return liveRoomRepository.findAll(LiveRoomWhereCriteriaBuilder.build(queryReq));
    }

    /**
     * 将实体包装成VO
     *
     * @author zwb
     */
    public LiveRoomVO wrapperVo(LiveRoom liveRoom) {
        if (liveRoom != null) {
            LiveRoomVO liveRoomVO = KsBeanUtil.convert(liveRoom, LiveRoomVO.class);
            return liveRoomVO;
        }
        return null;
    }

    /**
     * 修改直播间是否推荐
     * @param recommend
     * @param roomId
     */
    @Transactional
    public void recommend(Integer recommend, Long roomId) {
        liveRoomRepository.updateRecommendByRoomId(recommend,roomId);
    }
}


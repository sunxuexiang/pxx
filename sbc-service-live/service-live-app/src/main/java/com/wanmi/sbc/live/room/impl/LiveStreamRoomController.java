package com.wanmi.sbc.live.room.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.wanmi.sbc.live.api.response.room.*;
import com.wanmi.sbc.live.bean.vo.LiveStreamVO;
import com.wanmi.sbc.live.roomrela.model.root.LiveAccountNum;
import com.wanmi.sbc.live.stream.model.root.LiveStream;
import com.wanmi.sbc.live.stream.service.LiveStreamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.live.api.provider.room.LiveStreamRoomProvider;
import com.wanmi.sbc.live.api.request.room.LiveRoomAddRequest;
import com.wanmi.sbc.live.api.request.room.LiveRoomModifyRequest;
import com.wanmi.sbc.live.api.request.room.LiveRoomPageRequest;
import com.wanmi.sbc.live.api.request.room.LiveStreamRoomListRequest;
import com.wanmi.sbc.live.api.request.roomrela.LiveRoomRelaPageRequest;
import com.wanmi.sbc.live.bean.vo.LiveRoomRelaVO;
import com.wanmi.sbc.live.bean.vo.LiveRoomVO;
import com.wanmi.sbc.live.room.model.root.LiveRoom;
import com.wanmi.sbc.live.room.service.LiveRoomService;
import com.wanmi.sbc.live.roomrela.model.root.LiveRoomRela;
import com.wanmi.sbc.live.roomrela.service.LiveRoomRelaService;

import lombok.Data;

@Slf4j
@RestController
@Validated
@Data
public class LiveStreamRoomController implements LiveStreamRoomProvider {

    @Autowired
    private LiveRoomService liveRoomService;

    @Autowired
    private LiveRoomRelaService liveRoomRelaService;

    @Autowired
    private LiveStreamService liveStreamService;

//    @Autowired
//    GoodsBrandQueryProvider goodsBrandQueryProvider;

    @Override
    public BaseResponse<List<LiveAccountNumResponse>> modify(@Valid @RequestBody LiveRoomModifyRequest modifyRequest) {
        LiveRoom liveRoom = liveRoomService.getInfo(modifyRequest.getLiveRoomId());
        if (null == liveRoom) {
            return  BaseResponse.error("直播间不存在");
        }
        if ("1".equals(modifyRequest.getDelFlag())  ) {
            //若该主播信息下已关联账号，那么不允许删除
            LiveRoomRelaPageRequest request = new LiveRoomRelaPageRequest();
            request.setLiveRoomId(liveRoom.getLiveRoomId());
            request.setRelaType(LiveRoomRela.RELATYPE_ACCOUNT);
            Page<LiveRoomRelaVO> page = liveRoomRelaService.getPage(request);

            if (null == page || CollectionUtils.isEmpty(page.getContent() )) {
                return  BaseResponse.error("该直播间下已关联直播账号，不允许删除，请取消关联后再删除");
            }

        }
        List<LiveAccountNum> accountNumList = liveRoomService.modify(modifyRequest);
        List<LiveAccountNumResponse> resultList= KsBeanUtil.copyListProperties(accountNumList, LiveAccountNumResponse.class);
        return BaseResponse.success(resultList);
    }

    @Override
    public BaseResponse getInfo( @RequestBody   Long liveRoomId) {
        LiveRoomInfoResponse liveRoomInfoResponse = new LiveRoomInfoResponse();
        LiveRoom liveRoom = liveRoomService.getInfo(liveRoomId);
        if (null != liveRoom) {

            BeanUtils.copyProperties(liveRoom, liveRoomInfoResponse);

            
            LiveRoomRelaPageRequest request = new LiveRoomRelaPageRequest();
            request.setLiveRoomId(liveRoomId);
            request.setPageSize(null);
            
            Page<LiveRoomRelaVO> page = liveRoomRelaService.getPage(request);
            Map<String, Map<String, String>> relaTypeMap = getRelaTypeMap(page);
            liveRoomInfoResponse.setAccountMap(getMap(relaTypeMap, liveRoomId, LiveRoomRela.RELATYPE_ACCOUNT));
            liveRoomInfoResponse.setBrandMap(getMap(relaTypeMap, liveRoomId, LiveRoomRela.RELATYPE_BRAND));
            liveRoomInfoResponse.setOperationMap(getMap(relaTypeMap, liveRoomId, LiveRoomRela.RELATYPE_OPERATE));
        }

        return BaseResponse.success(liveRoomInfoResponse);
    }
    
    private Map<String, String> getMap(Map<String, Map<String, String>> relaTypeMap, String key) {
    	return relaTypeMap.get(key);
    }
    
    
    private Map<String, String> getMap(Map<String, Map<String, String>> relaTypeMap, Long liveRoomId, String relaType) {
    	return getMap(relaTypeMap, getRelaTypeMapKey(liveRoomId, relaType));
    }
    
    private Map<String, Map<String, String>> getRelaTypeMap(Page<LiveRoomRelaVO> page) {
    	Map<String, Map<String, String>> relaTypeMap = new HashMap<String, Map<String,String>>();
    	
    	
        page.getContent().stream().forEach(k -> {
        	String key = getRelaTypeMapKey(k);
        	Map<String, String> map = relaTypeMap.get(key);
        	if (null == map) {
        		map = new HashMap<>();
        		relaTypeMap.put(key, map);
        	}
            map.put(k.getRelaId(), k.getRelaContent());
        });
        
        return relaTypeMap;
    }
    
    private String getRelaTypeMapKey(LiveRoomRelaVO mLiveRoomRelaVO) {
    	return getRelaTypeMapKey(mLiveRoomRelaVO.getLiveRoomId(), mLiveRoomRelaVO.getRelaType());
    }
    
    private String getRelaTypeMapKey(Long liveRoomId,  String relaType) {
    	return liveRoomId + "," + relaType;
    }

    private Map<String, String> getMap(Page<LiveRoomRelaVO> page, String relaType) {

        Map<String, String> map = new HashMap<>();
        page.getContent().stream().forEach(k -> {
            map.put(k.getRelaId(), k.getRelaContent());
        });
        return map;
    }

    @Override
    public BaseResponse<List<LiveDetailExportVo>> getExportData(LiveRoomPageRequest pageRequest) {
        if (StringUtils.isEmpty(pageRequest.getStartTime())) {
            pageRequest.setStartTime(null);
        }
        if (StringUtils.isEmpty(pageRequest.getEndTime())) {
            pageRequest.setEndTime(null);
        }
        pageRequest.setPageSize(10000);
        pageRequest.setPageNum((pageRequest.getPageNum())*pageRequest.getPageSize());
        Page<LiveRoomVO> page = liveRoomService.getPage(pageRequest);
        List<LiveRoomVO> roomList = page.getContent();
        if (ObjectUtils.isEmpty(roomList)) {
            return BaseResponse.success(new ArrayList<>());
        }

        pageRequest.setLiveRoomIdList(roomList.stream().map(LiveRoomVO::getLiveRoomId).collect(Collectors.toList()));
        List<LiveStream> streamList = liveStreamService.getByLiveTime(pageRequest);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
        List<LiveDetailExportVo> resultList = new ArrayList<>();

        LiveRoomRelaPageRequest request = new LiveRoomRelaPageRequest();
        request.setLiveRoomIds(roomList.stream().map(LiveRoomVO::getLiveRoomId).collect(Collectors.toList()));
        request.setPageSize(null);
        Page<LiveRoomRelaVO> LiveRoomRelaPage = liveRoomRelaService.getPage(request);
        Map<String, Map<String, String>> relaTypeMap = getRelaTypeMap(LiveRoomRelaPage);

        streamList.forEach(stream -> {
            try {
                LiveDetailExportVo liveDetailExportVo = KsBeanUtil.convert(stream, LiveDetailExportVo.class);
                resultList.add(liveDetailExportVo);
                liveDetailExportVo.setMediaUrl(stream.getMediaUrl());
                try {
                    liveDetailExportVo.setStoreId(Long.parseLong(stream.getStoreId()));
                } catch (Exception e) {
                }

                if (stream.getStartTime() != null && stream.getEndTime() != null) {
                    String startTime = simpleDateFormat.format(stream.getStartTime());
                    String endTime = simpleDateFormat.format(stream.getEndTime());
                    liveDetailExportVo.setLiveTime(startTime + " ~ " + endTime);

                    String startDate = dateFormat.format(stream.getStartTime());
                    String endDate = dateFormat.format(stream.getEndTime());
                    String startHour = hourFormat.format(stream.getStartTime());
                    String endHour = hourFormat.format(stream.getEndTime());
                    if (startDate.equals(endDate)) {
                        liveDetailExportVo.setLiveDate(startDate);
                        liveDetailExportVo.setLiveHour(startHour + " ~ " + endHour);
                    }
                    else {
                        liveDetailExportVo.setLiveDate(liveDetailExportVo.getLiveTime());
                        liveDetailExportVo.setLiveHour(liveDetailExportVo.getLiveTime());
                    }

                    liveDetailExportVo.setLiveDuration((int) ((stream.getEndTime().getTime() - stream.getStartTime().getTime()) / 1000 / 60));
                }

                for (LiveRoomVO room : roomList) {
                    if (stream.getLiveRoomId() != null && room.getLiveRoomId() != null && stream.getLiveRoomId().longValue() == room.getLiveRoomId().longValue()) {
                        liveDetailExportVo.setCompanyId(room.getCompanyId());

                        Map<String, String> brandMap = (getMap(relaTypeMap, room.getLiveRoomId(), LiveRoomRela.RELATYPE_BRAND));
                        if (ObjectUtils.isEmpty(brandMap)) {
                            liveDetailExportVo.setBrandName("-");
                        }
                        else {
                            StringBuffer stringBuffer = new StringBuffer();
                            brandMap.values().forEach(brand -> {
                                stringBuffer.append(brand).append(",");
                            });
                            liveDetailExportVo.setBrandName(stringBuffer.substring(0, stringBuffer.length() - 1));
                        }

                        break;
                    }
                }
            }
            catch (Exception e) {
                log.error("直播记录导出异常", e);
            }
        });
        return BaseResponse.success(resultList);
    }

    @Override
    public BaseResponse getPage(@Valid @RequestBody  LiveRoomPageRequest pageRequest) {
        pageRequest.setPageNum((pageRequest.getPageNum())*pageRequest.getPageSize());
        Page<LiveRoomVO> page = liveRoomService.getPage(pageRequest);
        LiveRoomPageResponse liveRoomPageResponse = new LiveRoomPageResponse();
        BeanUtils.copyProperties(page, liveRoomPageResponse);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (CollectionUtils.isNotEmpty(page.getContent())) {
            List<LiveRoomInfoResponse> list = new ArrayList<>();

            List<Long> liveRoomIds = new ArrayList<Long>();
            page.getContent().stream().forEach(k -> {
            	liveRoomIds.add(k.getLiveRoomId());
            });
            
            LiveRoomRelaPageRequest request = new LiveRoomRelaPageRequest();
            request.setLiveRoomIds(liveRoomIds);
            request.setPageSize(null);
            Page<LiveRoomRelaVO> LiveRoomRelaPage = liveRoomRelaService.getPage(request);
            Map<String, Map<String, String>> relaTypeMap = getRelaTypeMap(LiveRoomRelaPage);
            page.getContent().stream().forEach(liveRoomVO -> {
                if (null != liveRoomVO && null != liveRoomVO.getLiveRoomId()) {
                    LiveRoomInfoResponse liveRoomInfoResponse = new LiveRoomInfoResponse();
                    BeanUtils.copyProperties(liveRoomVO, liveRoomInfoResponse);
                    LiveStreamVO liveStreamVO = liveRoomVO.getLiveStreamVO();
                    liveRoomVO.setLiveStreamVO(null);
                    if (liveStreamVO != null && liveStreamVO.getStartTime() != null && liveStreamVO.getEndTime() != null) {
                        String startTime = simpleDateFormat.format(liveStreamVO.getStartTime());
                        String endTime = simpleDateFormat.format(liveStreamVO.getEndTime());
                        liveRoomInfoResponse.setLastLiveTime(startTime + " ~ " + endTime);
                    }
                    
                    liveRoomInfoResponse.setAccountMap(getMap(relaTypeMap, liveRoomVO.getLiveRoomId(), LiveRoomRela.RELATYPE_ACCOUNT));
                    liveRoomInfoResponse.setBrandMap(getMap(relaTypeMap, liveRoomVO.getLiveRoomId(), LiveRoomRela.RELATYPE_BRAND));
                    liveRoomInfoResponse.setOperationMap(getMap(relaTypeMap, liveRoomVO.getLiveRoomId(), LiveRoomRela.RELATYPE_OPERATE));

                    list.add(liveRoomInfoResponse);
                }
            });

            liveRoomPageResponse.setContent(list);

        }

        return BaseResponse.success(liveRoomPageResponse);
    }

    @Override
    public BaseResponse<LiveStreamRoomListResponse> getLiveRoomListByCustomerId(LiveStreamRoomListRequest roomListRequest) {
        LiveStreamRoomListResponse liveStreamRoomListResponse=new LiveStreamRoomListResponse();
        List<LiveRoom> liveRoomList=liveRoomService.getLiveRoomListByCustomerId(roomListRequest.getCustomerId());
        List<LiveRoomVO> liveRoomVOS= KsBeanUtil.copyListProperties(liveRoomList, LiveRoomVO.class);
        if(!ObjectUtils.isEmpty(liveRoomVOS)) {
            liveRoomVOS.forEach(liveRoom -> {
                if (liveRoom == null || liveRoom.getLiveRoomId() == null) {
                    return;
                }
                LiveStreamVO info = liveStreamService.getLiveStreamEditInfoByRoomId(liveRoom.getLiveRoomId().intValue());
                liveRoom.setLiveStreamVO(info);
            });
        }
        liveStreamRoomListResponse.setLiveRoomVOList(liveRoomVOS);
        return BaseResponse.success(liveStreamRoomListResponse);
    }

    @Override
    public BaseResponse add(@Valid @RequestBody  LiveRoomAddRequest liveRoomAddRequest) {

        liveRoomService.add(liveRoomAddRequest);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 根据商家查询直播间列表
     * @param storeId
     * @return
     */
    @Override
    public BaseResponse<List<LiveRoomVO>> getLiveRoomByStore(Long storeId) {
        List<LiveRoomVO> roomList = liveRoomService.getLiveRoomByStore(storeId);
        return BaseResponse.success(roomList);
    }

}

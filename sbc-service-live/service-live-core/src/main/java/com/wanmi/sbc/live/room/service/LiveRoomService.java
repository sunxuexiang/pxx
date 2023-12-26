package com.wanmi.sbc.live.room.service;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StoreByIdRequest;
import com.wanmi.sbc.customer.api.response.store.StoreByIdResponse;
import com.wanmi.sbc.live.api.request.room.LiveRoomAddRequest;
import com.wanmi.sbc.live.api.request.room.LiveRoomModifyRequest;
import com.wanmi.sbc.live.api.request.room.LiveRoomPageRequest;
import com.wanmi.sbc.live.api.request.roomrela.LiveRoomRelaPageRequest;
import com.wanmi.sbc.live.api.utils.LiveDateUtils;
import com.wanmi.sbc.live.bean.vo.LiveRoomVO;
import com.wanmi.sbc.live.bean.vo.LiveStreamVO;
import com.wanmi.sbc.live.room.dao.LiveRoomMapper;
import com.wanmi.sbc.live.room.model.root.LiveRoom;
import com.wanmi.sbc.live.roomrela.dao.LiveRoomRelaMapper;
import com.wanmi.sbc.live.roomrela.model.root.LiveAccountNum;
import com.wanmi.sbc.live.roomrela.model.root.LiveRoomRela;
import com.wanmi.sbc.live.stream.service.LiveStreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("LiveRoomService")
@Transactional(readOnly = true)
public class LiveRoomService {

    @Autowired
    private LiveRoomMapper liveRoomMapper;

    @Autowired
    private LiveRoomRelaMapper liveRoomRelaMapper;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private LiveStreamService liveStreamService;


    /**
     * 获取主播分页列表
     * @param request
     * @return
     */
    public Page<LiveRoomVO> getPage(LiveRoomPageRequest request){
        if (!StringUtils.isEmpty(request.getStartTime()) || !StringUtils.isEmpty(request.getEndTime())) {
            List<Long> liveRoomIds = liveStreamService.getLiveRoomIdByLiveTime(request.getStartTime(), request.getEndTime());
            if (ObjectUtils.isEmpty(liveRoomIds)) {
                return new PageImpl<>(new ArrayList<>(), request.getPageable(), 0);
            }
            request.setLiveRoomIdList(liveRoomIds);
        }
        LiveRoomPageRequest liveRoom = new LiveRoomPageRequest();
        BeanUtils.copyProperties(request, liveRoom);
        List<LiveRoom> pageList = liveRoomMapper.getPage(liveRoom);
        int total = liveRoomMapper.getPageCount(liveRoom);
        List<LiveRoomVO> liveRoomVOList = new ArrayList<>();
        List<LiveStreamVO> streamList = liveStreamService.getLastLiveByRoomIds(pageList.stream().map(LiveRoom::getLiveRoomId).collect(Collectors.toList()));
        pageList.forEach(k -> {
            LiveRoomVO vo = new LiveRoomVO();
            BeanUtils.copyProperties(k, vo);
            liveRoomVOList.add(vo);
            if (ObjectUtils.isEmpty(streamList)) {
                return;
            }
            for (LiveStreamVO stream : streamList) {
                if (stream.getLiveRoomId() != null && stream.getLiveRoomId() != null && k.getLiveRoomId().longValue() == stream.getLiveRoomId().longValue()) {
                    vo.setLiveStreamVO(stream);
                    break;
                }
            }
        });
        request.setPageNum(request.getPageNum() / request.getPageSize());
        return new PageImpl<LiveRoomVO>(liveRoomVOList, request.getPageable(), total);
    }

    /**
     * 获取信息
     * @param roomId
     * @return
     */
    public LiveRoom getInfo(Long roomId){
        if (roomId == null) {
            return null;
        }
        return liveRoomMapper.getInfo(roomId);
    }

    @Transactional(readOnly = false)
    public void add(LiveRoomAddRequest liveRoomAddRequest) {
        //校验
        LiveRoom liveRoom = new LiveRoom();
        liveRoom.setCreateTime(LocalDateTime.now());
        liveRoom.setUpdateTime(liveRoom.getCreateTime());
        BeanUtils.copyProperties(liveRoomAddRequest, liveRoom);

        /**
         * zhouzhenguo 200230713
         * 注释代码：系统中只允许存在一个平台直播间，正式环境 该平台直播间已经创建
         *   所以新建的直播间全部为非平台直播间
         */
        liveRoom.setSysFlag(LiveRoom.SYSFLAG_NO);
//        if(Objects.isNull(liveRoomAddRequest.getCompanyId()) && Objects.isNull(liveRoomAddRequest.getBrandMap())){
//            LiveRoom liveRoomSys =liveRoomMapper.getSysInfo(0);
//            if(Objects.nonNull(liveRoomSys)){
//                liveRoom.setSysFlag(LiveRoom.SYSFLAG_YES);
//            }
//        }else {
//            liveRoom.setSysFlag(LiveRoom.SYSFLAG_NO);
//        }

        int result = liveRoomMapper.add(liveRoom);
        if (result > 0 && null != liveRoom.getLiveRoomId()) {
            convert(liveRoom.getLiveRoomId(), liveRoomAddRequest.getAccountMap(), LiveRoomRela.RELATYPE_ACCOUNT);
            convert(liveRoom.getLiveRoomId(), liveRoomAddRequest.getBrandMap(), LiveRoomRela.RELATYPE_BRAND);
            convert(liveRoom.getLiveRoomId(), liveRoomAddRequest.getOperationMap(), LiveRoomRela.RELATYPE_OPERATE);
        }
    }



    private void convert(Long liveRoomId, Map<String, String> map, String relaType) {
        LocalDateTime date = LiveDateUtils.toLdt(new Date());
        if (map!=null) {
            map.forEach((k, v) -> {
                LiveRoomRela liveRoomRela = new LiveRoomRela();
                liveRoomRela.setLiveRoomId(liveRoomId);
                liveRoomRela.setRelaType(relaType);
                liveRoomRela.setRelaId(k);
                liveRoomRela.setRelaContent(v);
                liveRoomRela.setCreateTime(date);
                liveRoomRela.setUpdateTime(date);
                liveRoomRelaMapper.add(liveRoomRela);
            });
        }

    }

    @Transactional(readOnly = false)
    public List<LiveAccountNum> modify(LiveRoomModifyRequest modifyRequest) {
        LiveRoom liveRoom = new LiveRoom();
        BeanUtils.copyProperties(modifyRequest, liveRoom);
        liveRoom.setUpdateTime(LocalDateTime.now());
        //liveRoom.setSysFlag(null);
        liveRoomMapper.modify(liveRoom);

        LiveRoomRelaPageRequest queryRequest = new LiveRoomRelaPageRequest();
        queryRequest.setLiveRoomId(modifyRequest.getLiveRoomId());
        queryRequest.setRelaType(LiveRoomRela.RELATYPE_ACCOUNT);
        queryRequest.setPageSize(1000);
        List<LiveAccountNum> accountNumList = new ArrayList<>();
        List<LiveRoomRela> relaList = liveRoomRelaMapper.getPage(queryRequest);
        if (!ObjectUtils.isEmpty(relaList)) {
            List<String> customerIds = relaList.stream().map(LiveRoomRela::getRelaId).distinct().collect(Collectors.toList());
            accountNumList = liveRoomRelaMapper.countByLiveAccount(customerIds, LiveRoomRela.RELATYPE_ACCOUNT);
            log.info("直播间修改 绑定直播账号统计结果 {}", JSON.toJSONString(accountNumList));
            for (LiveAccountNum liveAccountNum : accountNumList) {
                if (liveAccountNum.getQuantity() == null || liveAccountNum.getQuantity() <= 1) {

                }
            }
        }
        modify(liveRoom.getLiveRoomId(), modifyRequest.getAccountMap(), LiveRoomRela.RELATYPE_ACCOUNT);
        modify(liveRoom.getLiveRoomId(), modifyRequest.getBrandMap(), LiveRoomRela.RELATYPE_BRAND);
        modify(liveRoom.getLiveRoomId(), modifyRequest.getOperationMap(), LiveRoomRela.RELATYPE_OPERATE);
        return accountNumList;
    }

    private void modify(Long liveRoomId, Map<String, String> map, String relaType) {
        LiveRoomRela deleteLiveRoomRela = new LiveRoomRela();
        deleteLiveRoomRela.setRelaType(relaType);
        deleteLiveRoomRela.setLiveRoomId(liveRoomId);
        liveRoomRelaMapper.deleteByRoom(deleteLiveRoomRela);
        convert(liveRoomId, map, relaType);
    }

    /**
     * 根据主播用户id获取直播间列表
     * @param customerId
     * @return
     */
    public List<LiveRoom> getLiveRoomListByCustomerId(String customerId){
        return liveRoomMapper.getLiveRoomListByCustomerId(customerId);
    }

    /**
     * 通过直播间id获取品牌
     * @param liveRoomId
     * @return
     */
    public List<Long> getBrandIdsByLiveRoomId(Long liveRoomId){
        return liveRoomMapper.getBrandIdsByLiveRoomId(liveRoomId);
    }

    /**
     * 根据商家查询直播间列表
     * @param storeId
     * @return
     */
    public List<LiveRoomVO> getLiveRoomByStore(Long storeId) {
        List<LiveRoom> list = liveRoomMapper.getListByStoreId(storeId);
        List<LiveRoomVO> resultList = KsBeanUtil.convertList(list, LiveRoomVO.class);
        return resultList;
    }
}
package com.wanmi.sbc.live.roomrela.service;

import com.wanmi.sbc.live.api.request.roomrela.LiveRoomRelaPageRequest;
import com.wanmi.sbc.live.bean.vo.LiveRoomRelaVO;
import com.wanmi.sbc.live.roomrela.dao.LiveRoomRelaMapper;
import com.wanmi.sbc.live.roomrela.model.root.LiveRoomRela;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service("LiveRoomRelaService")
public class LiveRoomRelaService {

    @Autowired
    private LiveRoomRelaMapper liveRoomRelaMapper;


    /**
     * 获取主播分页列表
     * @param request
     * @return
     */
    public Page<LiveRoomRelaVO> getPage(LiveRoomRelaPageRequest request){
    	boolean isNull = null == request; 
    	if (isNull) {
    		request = new LiveRoomRelaPageRequest();
    		request.setPageNum(0);
    		request.setPageSize(10);
    	}
    	if (null == request.getPageSize()) {
    		request.setPageSize(Integer.MAX_VALUE);
    	}
        LiveRoomRela liveRoomRela = new LiveRoomRela();
        if (ObjectUtils.isEmpty(request.getLiveRoomIds())) {
            request.setLiveRoomIds(null);
        }
        List<LiveRoomRela> pageList = liveRoomRelaMapper.getPage(request);
        int total = liveRoomRelaMapper.getPageCount(request);
        List<LiveRoomRelaVO> liveRoomRelaVOList = new ArrayList<>();
        pageList.forEach(k -> {
            LiveRoomRelaVO vo=new LiveRoomRelaVO();
            BeanUtils.copyProperties(k, vo);
            liveRoomRelaVOList.add(vo);
        });
        if (null !=  request.getPageSize()) {
        	request.setPageNum(request.getPageNum() / request.getPageSize());
        }
        return new PageImpl<LiveRoomRelaVO>(liveRoomRelaVOList, request.getPageable(), total);
    }



    /**
     * 获取信息
     * @param room_relaId
     * @return
     */
    public LiveRoomRela getInfo(Long room_relaId){
        return null;
    }
}
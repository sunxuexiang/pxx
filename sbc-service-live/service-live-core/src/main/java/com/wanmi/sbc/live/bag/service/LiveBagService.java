package com.wanmi.sbc.live.bag.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.live.activity.dao.LiveBagLogMapper;
import com.wanmi.sbc.live.api.request.bag.LiveBagAddRequest;
import com.wanmi.sbc.live.api.request.bag.LiveBagListRequest;
import com.wanmi.sbc.live.api.request.bag.LiveBagModifyRequest;
import com.wanmi.sbc.live.api.request.bag.LiveBagPageRequest;
import com.wanmi.sbc.live.bean.vo.LiveBagVO;
import com.wanmi.sbc.live.bag.dao.LiveBagMapper;
import com.wanmi.sbc.live.bag.model.root.LiveBag;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Service("LiveBagService")
public class LiveBagService {

    @Resource
    private LiveBagMapper liveBagMapper;
    @Resource
    private LiveBagLogMapper liveBagLogMapper;
    /**
     * 获取福袋分页列表
     * @param request
     * @return
     */
    public Page<LiveBagVO> getPage(LiveBagPageRequest request){
        request.setDelFlag(0);
        List<LiveBag> pageList=liveBagMapper.getPage(request);
        int total=liveBagMapper.getPageCount(request);
        List<LiveBagVO> liveBagVOList=new ArrayList<>();
        pageList.forEach(liveBag -> {
            LiveBagVO vo=new LiveBagVO();
            BeanUtils.copyProperties(liveBag, vo);
            liveBagVOList.add(vo);
        });
        request.setPageNum(request.getPageNum() / request.getPageSize());
        return new PageImpl<LiveBagVO>(liveBagVOList, request.getPageable(), total);
    }

    /**
     * 获取福袋信息
     * @param bagId
     * @return
     */
    public LiveBag getInfo(Long bagId){
        return liveBagMapper.getInfo(bagId);
    }

    /**
     * 新增福袋信息
     * @param request
     * @return
     */
    @Transactional
    public int add(LiveBagAddRequest request){
        LocalDateTime currentDate= LocalDateTime.now();
        LiveBag liveBag=new LiveBag( );
        BeanUtils.copyProperties(request, liveBag);
        //liveBag.setDelFlag(0L);//未删除
        liveBag.setCreateTime(currentDate);
        liveBag.setUpdateTime(currentDate);
        int count = liveBagMapper.add(liveBag);
        return count;
    }

    /**
     * 修改福袋信息
     * @param request
     * @return
     */
    @Transactional
    public int modify(LiveBagModifyRequest request){
        LiveBag info = liveBagMapper.getInfo(request.getLiveBagId());
        if(info==null){
            throw new RuntimeException("福袋信息不存在");
        }

        //获取更新数据对象
        LiveBag liveBag=new LiveBag();
        BeanUtils.copyProperties(request, liveBag);
        liveBag.setUpdateTime(LocalDateTime.now());
        int count = liveBagMapper.modify(liveBag);
        return count;
    }

    public List<LiveBagVO> liveBagRecordList(LiveBagListRequest request){
        return liveBagMapper.getListBagByLiveId(request);
    }

    public List<LiveBagVO> liveBagRoomList(LiveBagListRequest request){
        return liveBagMapper.getListBagByLiveRoomId(request.getLiveRoomId());
    }
}
package com.wanmi.sbc.goods.liveroomlivegoodsrel.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.wanmi.sbc.goods.liveroomlivegoodsrel.repository.LiveRoomLiveGoodsRelRepository;
import com.wanmi.sbc.goods.liveroomlivegoodsrel.model.root.LiveRoomLiveGoodsRel;
import com.wanmi.sbc.goods.api.request.liveroomlivegoodsrel.LiveRoomLiveGoodsRelQueryRequest;
import com.wanmi.sbc.goods.bean.vo.LiveRoomLiveGoodsRelVO;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.enums.DeleteFlag;
import java.util.List;

/**
 * <p>直播房间和直播商品关联表业务逻辑</p>
 * @author zwb
 * @date 2020-06-08 09:12:17
 */
@Service("LiveRoomLiveGoodsRelService")
public class LiveRoomLiveGoodsRelService {
	@Autowired
	private LiveRoomLiveGoodsRelRepository liveRoomLiveGoodsRelRepository;

	/**
	 * 新增直播房间和直播商品关联表
	 * @author zwb
	 */
	@Transactional
	public LiveRoomLiveGoodsRel add(LiveRoomLiveGoodsRel entity) {
		liveRoomLiveGoodsRelRepository.save(entity);
		return entity;
	}

	/**
	 * 修改直播房间和直播商品关联表
	 * @author zwb
	 */
	@Transactional
	public LiveRoomLiveGoodsRel modify(LiveRoomLiveGoodsRel entity) {
		liveRoomLiveGoodsRelRepository.save(entity);
		return entity;
	}

	/**
	 * 单个删除直播房间和直播商品关联表
	 * @author zwb
	 */
	@Transactional
	public void deleteById(LiveRoomLiveGoodsRel entity) {
		liveRoomLiveGoodsRelRepository.save(entity);
	}

	/**
	 * 批量删除直播房间和直播商品关联表
	 * @author zwb
	 */
	@Transactional
	public void deleteByIdList(List<LiveRoomLiveGoodsRel> infos) {
		liveRoomLiveGoodsRelRepository.saveAll(infos);
	}

	/**
	 * 单个查询直播房间和直播商品关联表
	 * @author zwb
	 */
	public LiveRoomLiveGoodsRel getOne(Long id){
		return liveRoomLiveGoodsRelRepository.findByIdAndDelFlag(id, DeleteFlag.NO)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "直播房间和直播商品关联表不存在"));
	}

	/**
	 * 分页查询直播房间和直播商品关联表
	 * @author zwb
	 */
	public Page<LiveRoomLiveGoodsRel> page(LiveRoomLiveGoodsRelQueryRequest queryReq){
		return liveRoomLiveGoodsRelRepository.findAll(
				LiveRoomLiveGoodsRelWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询直播房间和直播商品关联表
	 * @author zwb
	 */
	public List<LiveRoomLiveGoodsRel> list(LiveRoomLiveGoodsRelQueryRequest queryReq){
		return liveRoomLiveGoodsRelRepository.findAll(LiveRoomLiveGoodsRelWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author zwb
	 */
	public LiveRoomLiveGoodsRelVO wrapperVo(LiveRoomLiveGoodsRel liveRoomLiveGoodsRel) {
		if (liveRoomLiveGoodsRel != null){
			LiveRoomLiveGoodsRelVO liveRoomLiveGoodsRelVO = KsBeanUtil.convert(liveRoomLiveGoodsRel, LiveRoomLiveGoodsRelVO.class);
			return liveRoomLiveGoodsRelVO;
		}
		return null;
	}

	public List<LiveRoomLiveGoodsRel> getByRoomId(Long roomId) {

         return liveRoomLiveGoodsRelRepository.findByRoomIdAndDelFlag(roomId, DeleteFlag.NO);
	}
}


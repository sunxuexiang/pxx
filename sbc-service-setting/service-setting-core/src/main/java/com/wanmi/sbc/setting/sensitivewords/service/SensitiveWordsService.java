package com.wanmi.sbc.setting.sensitivewords.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.SensitiveWordsDelByIdListRequest;
import com.wanmi.sbc.setting.api.request.SensitiveWordsQueryRequest;
import com.wanmi.sbc.setting.bean.vo.SensitiveWordsVO;
import com.wanmi.sbc.setting.redis.RedisService;
import com.wanmi.sbc.setting.sensitivewords.model.root.SensitiveWords;
import com.wanmi.sbc.setting.sensitivewords.repository.SensitiveWordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>业务逻辑</p>
 * @author wjj
 * @date 2019-02-22 16:09:48
 */
@Service("SensitiveWordsService")
public class SensitiveWordsService {
	@Autowired
	private SensitiveWordsRepository sensitiveWordsRepository;

	@Autowired
	private RedisService redisService;

	@Autowired
	private BadWordService badWordService;
	/** 
	 * 新增
	 * @author wjj
	 */
	@Transactional
	public void add(List<SensitiveWords> entityList) {
		sensitiveWordsRepository.saveAll(entityList);
		//更新redis
		refushBadWordRedis();
	}
	
	/** 
	 * 修改
	 * @author wjj
	 */
	@Transactional
	public SensitiveWords modify(SensitiveWords entity) {
		sensitiveWordsRepository.save(entity);
		//更新redis
		refushBadWordRedis();
		return entity;
	}

	/**
	 * 单个删除
	 * @author wjj
	 */
	@Transactional
	public void deleteById(Long id) {
		sensitiveWordsRepository.deleteById(id);
		//更新redis
		refushBadWordRedis();
	}
	
	/** 
	 * 批量删除
	 * @author wjj
	 */
	@Transactional
	public void deleteByIdList( SensitiveWordsDelByIdListRequest delByIdListRequest) {
		sensitiveWordsRepository.deleteSensitiveWordsByIds(DeleteFlag.YES,delByIdListRequest.getDeleteUser(),
				delByIdListRequest.getDeleteTime(),delByIdListRequest.getSensitiveIdList());
		//更新redis
		refushBadWordRedis();
//		sensitiveWordsRepository.delete(ids.stream().map(id -> {
//			SensitiveWords entity = new SensitiveWords();
//			entity.setSensitiveId(id);
//			return entity;
//		}).collect(Collectors.toList()));
	}
	
	/** 
	 * 单个查询
	 * @author wjj
	 */
	public SensitiveWords getById(Long id){
		return sensitiveWordsRepository.findById(id).get();
	}
	
	/** 
	 * 分页查询
	 * @author wjj
	 */
	public Page<SensitiveWords> page(SensitiveWordsQueryRequest queryReq){
		return sensitiveWordsRepository.findAll(
				SensitiveWordsWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}
	
	/** 
	 * 列表查询
	 * @author wjj
	 */
	public List<SensitiveWords> list(SensitiveWordsQueryRequest queryReq){
		return sensitiveWordsRepository.findAll(SensitiveWordsWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author wjj
	 */
	public SensitiveWordsVO wrapperVo(SensitiveWords sensitiveWords) {
		if (sensitiveWords != null){
			SensitiveWordsVO sensitiveWordsVO=new SensitiveWordsVO();
			KsBeanUtil.copyPropertiesThird(sensitiveWords,sensitiveWordsVO);
			return sensitiveWordsVO;
		}
		return null;
	}

	/**
	 * 刷新敏感词redis
	 */
	private void refushBadWordRedis(){
		String badWords = badWordService.getBadWordsFromDB();
		redisService.delete(CacheKeyConstant.BAD_WORD);
		redisService.setString(CacheKeyConstant.BAD_WORD,badWords);
	}
}

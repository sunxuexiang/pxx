package com.wanmi.sbc.wms.record.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.wanmi.sbc.wms.record.repository.RecordRepository;
import com.wanmi.sbc.wms.record.model.root.Record;
import com.wanmi.sbc.wms.api.request.record.RecordQueryRequest;
import com.wanmi.sbc.wms.bean.vo.RecordVO;
import com.wanmi.sbc.common.util.KsBeanUtil;
import java.util.List;

/**
 * <p>请求记录业务逻辑</p>
 * @author baijz
 * @date 2020-05-06 19:23:45
 */
@Service("RecordService")
public class RecordService {
	@Autowired
	private RecordRepository recordRepository;
	
	/** 
	 * 新增请求记录
	 * @author baijz
	 */
	@Transactional
	public Record add(Record entity) {
		recordRepository.save(entity);
		return entity;
	}
	
	/** 
	 * 修改请求记录
	 * @author baijz
	 */
	@Transactional
	public Record modify(Record entity) {
		recordRepository.save(entity);
		return entity;
	}

	/**
	 * 单个删除请求记录
	 * @author baijz
	 */
	@Transactional
	public void deleteById(Long id) {
		recordRepository.deleteById(id);
	}
	
	/** 
	 * 批量删除请求记录
	 * @author baijz
	 */
	@Transactional
	public void deleteByIdList(List<Long> ids) {
		ids.forEach(id -> recordRepository.deleteById(id));
	}
	
	/** 
	 * 单个查询请求记录
	 * @author baijz
	 */
	public Record getById(Long id){
		return recordRepository.findById(id).orElse(null);
	}
	
	/** 
	 * 分页查询请求记录
	 * @author baijz
	 */
	public Page<Record> page(RecordQueryRequest queryReq){
		return recordRepository.findAll(
				RecordWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}
	
	/** 
	 * 列表查询请求记录
	 * @author baijz
	 */
	public List<Record> list(RecordQueryRequest queryReq){
		return recordRepository.findAll(
				RecordWhereCriteriaBuilder.build(queryReq),
				queryReq.getSort());
	}

	/**
	 * 将实体包装成VO
	 * @author baijz
	 */
	public RecordVO wrapperVo(Record record) {
		if (record != null){
			RecordVO recordVO=new RecordVO();
			KsBeanUtil.copyPropertiesThird(record,recordVO);
			return recordVO;
		}
		return null;
	}
}

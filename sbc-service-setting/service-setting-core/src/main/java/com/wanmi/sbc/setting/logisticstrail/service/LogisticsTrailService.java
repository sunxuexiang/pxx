package com.wanmi.sbc.setting.logisticstrail.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.bean.vo.LogisticsTrailVO;
import com.wanmi.sbc.setting.logisticstrail.repository.LogisticsTrailRepository;
import com.wanmi.sbc.setting.logisticstrail.root.LogisticsTrail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * @desc  物流轨迹
 * @author shiy  2023/6/8 14:44
*/
@Service("logisticsTrailService")
@Slf4j
public class LogisticsTrailService {
	@Autowired
	private LogisticsTrailRepository logisticsTrailRepository;

	/**
	 * 新增物流轨迹
	 * @author fcq
	 */
	@Transactional(rollbackFor = RuntimeException.class)
	public LogisticsTrail add(LogisticsTrail entity) {
		Long aLong = logisticsTrailRepository.selectLogisticsNumNumber(entity.getCom(),entity.getNum());
		if (null!=aLong && aLong>0){
			entity.setId(aLong);
		}
		return logisticsTrailRepository.saveAndFlush(entity);
	}


	/**
	 * @desc  列表
	 * @author shiy  2023/6/8 14:56
	*/
	public List<LogisticsTrail> queryList(String com,String num) {
		return logisticsTrailRepository.queryList(com,num);
	}


	/**
	 * 单个查询物流轨迹
	 * @author fcq
	 */
	public LogisticsTrail getOne(Long id){
		return logisticsTrailRepository.getOne(id);
	}


	/**
	 * 将实体包装成VO
	 * @author fcq
	 */
	public LogisticsTrailVO wrapperVo(LogisticsTrail logisticsTrail) {
		if (logisticsTrail != null){
			LogisticsTrailVO logisticsTrailVO = KsBeanUtil.convert(logisticsTrail, LogisticsTrailVO.class);
			return logisticsTrailVO;
		}
		return null;
	}

	public void saveAll(List<LogisticsTrailVO> collect) {
		logisticsTrailRepository.saveAll(KsBeanUtil.convert(collect,LogisticsTrail.class));
	}

}


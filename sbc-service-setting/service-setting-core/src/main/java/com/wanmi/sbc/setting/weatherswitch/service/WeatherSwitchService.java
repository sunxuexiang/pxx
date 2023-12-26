package com.wanmi.sbc.setting.weatherswitch.service;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.weatherswitch.WeatherSwitchQueryRequest;
import com.wanmi.sbc.setting.bean.vo.WeatherSwitchVO;
import com.wanmi.sbc.setting.weatherswitch.model.root.WeatherSwitch;
import com.wanmi.sbc.setting.weatherswitch.repository.WeatherSwitchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>天气设置业务逻辑</p>
 * @author 费传奇
 * @date 2021-04-16 09:54:37
 */
@Service("WeatherSwitchService")
public class WeatherSwitchService {
	@Autowired
	private WeatherSwitchRepository weatherSwitchRepository;

	/**
	 * 新增天气设置
	 * @author 费传奇
	 */
	@Transactional
	public WeatherSwitch add(WeatherSwitch entity) {
		weatherSwitchRepository.save(entity);
		return entity;
	}

	/**
	 * 修改天气设置
	 * @author 费传奇
	 */
	@Transactional
	public WeatherSwitch modify(WeatherSwitch entity) {
		weatherSwitchRepository.save(entity);
		return entity;
	}


	/**
	 * 单个查询天气设置
	 * @author 费传奇
	 */
	public WeatherSwitch getOne(Long id){
		return weatherSwitchRepository.findById(id)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "天气设置不存在"));
	}




	/**
	 * 列表查询天气设置
	 * @author 费传奇
	 */
	public List<WeatherSwitch> list(WeatherSwitchQueryRequest queryReq){
		return weatherSwitchRepository.findAll(WeatherSwitchWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author 费传奇
	 */
	public WeatherSwitchVO wrapperVo(WeatherSwitch weatherSwitch) {
		if (weatherSwitch != null){
			WeatherSwitchVO weatherSwitchVO = KsBeanUtil.convert(weatherSwitch, WeatherSwitchVO.class);
			return weatherSwitchVO;
		}
		return null;
	}
}


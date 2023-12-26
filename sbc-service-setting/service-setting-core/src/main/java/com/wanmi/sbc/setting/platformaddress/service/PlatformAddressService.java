package com.wanmi.sbc.setting.platformaddress.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.platformaddress.PlatformAddressQueryRequest;
import com.wanmi.sbc.setting.bean.vo.PlatformAddressVO;
import com.wanmi.sbc.setting.platformaddress.model.root.PlatformAddress;
import com.wanmi.sbc.setting.platformaddress.repository.PlatformAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
/**
 * <p>平台地址信息业务逻辑</p>
 * @author dyt
 * @date 2020-03-30 14:39:57
 */
@Service("PlatformAddressService")
public class PlatformAddressService {
	@Autowired
	private PlatformAddressRepository platformAddressRepository;

	/**
	 * 新增平台地址信息
	 * @author dyt
	 */
	@Transactional
	public PlatformAddress add(PlatformAddress entity) {
        entity.setDelFlag(DeleteFlag.NO);
		platformAddressRepository.save(entity);
		return entity;
	}

	/**
	 * 修改平台地址信息
	 * @author dyt
	 */
	@Transactional
	public PlatformAddress modify(PlatformAddress entity) {
		platformAddressRepository.save(entity);
		return entity;
	}

	/**
	 * 单个删除平台地址信息
	 * @author dyt
	 */
	@Transactional
	public void deleteById(String id) {
		platformAddressRepository.deleteById(id);
	}

	/**
	 * 批量删除平台地址信息
	 * @author dyt
	 */
	@Transactional
	public void deleteByIdList(List<String> ids) {
		platformAddressRepository.deleteByIdList(ids);
	}

	/**
	 * 单个查询平台地址信息
	 * @author dyt
	 */
	public PlatformAddress getOne(String id){
		return platformAddressRepository.findByIdAndDelFlag(id, DeleteFlag.NO)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "平台地址信息不存在"));
	}

	/**
	 * 分页查询平台地址信息
	 * @author dyt
	 */
	public Page<PlatformAddress> page(PlatformAddressQueryRequest queryReq){
		return platformAddressRepository.findAll(
				PlatformAddressWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询平台地址信息
	 * @author dyt
	 */
	public List<PlatformAddress> list(PlatformAddressQueryRequest queryReq){
		return platformAddressRepository.findAll(PlatformAddressWhereCriteriaBuilder.build(queryReq), Sort.by(Arrays.asList(Sort.Order.desc("dataType"), Sort.Order.asc("sortNo"), Sort.Order.desc("createTime"))));
	}

	/**
	 * 将实体包装成VO
	 * @author dyt
	 */
	public PlatformAddressVO wrapperVo(PlatformAddress platformAddress) {
		if (platformAddress != null){
			PlatformAddressVO platformAddressVO = KsBeanUtil.convert(platformAddress, PlatformAddressVO.class);
            platformAddressVO.setLeafFlag(Boolean.TRUE);
			return platformAddressVO;
		}
		return null;
	}

	/**
	 * 列表查询平台省市地址信息
	 * @author dyt
	 */
	public List<PlatformAddress> provinceCityList(List<String> addrIds){
		return platformAddressRepository.findAllProvinceAndCity(addrIds);
	}
}


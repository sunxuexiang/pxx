package com.wanmi.sbc.setting.banneradmin.service;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.banneradmin.BannerAdminQueryRequest;
import com.wanmi.sbc.setting.banneradmin.model.root.BannerAdmin;
import com.wanmi.sbc.setting.banneradmin.repository.BannerAdminRepository;
import com.wanmi.sbc.setting.bean.vo.BannerAdminVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>轮播管理业务逻辑</p>
 * @author 费传奇
 * @date 2020-12-08 11:44:38
 */
@Service("BannerAdminService")
public class BannerAdminService {
	@Autowired
	private BannerAdminRepository bannerAdminRepository;
	private static final long maxCount = 15;





	@Transactional(rollbackFor = Exception.class)
	public BannerAdmin add(BannerAdmin entity) {
		Long oneCateId = entity.getOneCateId();
		Long maxOneCateId = bannerAdminRepository.findBannerAdminByOneCateId(oneCateId);
		if (Objects.nonNull(maxOneCateId) && maxOneCateId>= maxCount){
			throw new SbcRuntimeException(CommonErrorCode.MaxCount);
		}
		Integer bannerSort = entity.getBannerSort();
		//查询且从小到大排序
		List<BannerAdmin> oneCateBannerAdmin = bannerAdminRepository.findByOneCateIdAndDelFlagOrderByBannerSortAsc(oneCateId,
				DeleteFlag.NO);
		if (CollectionUtils.isNotEmpty(oneCateBannerAdmin)) {
			List<BannerAdmin> newBannerAdmin = Lists.newArrayList();
			int maxSort = oneCateBannerAdmin.stream().mapToInt(c -> c.getBannerSort()).max().getAsInt();
			if (Objects.nonNull(bannerSort) && bannerSort<=maxSort){
				Integer sortValue = 1;
				for (int i = 0; i < oneCateBannerAdmin.size(); i++) {
					BannerAdmin bannerAdmin = oneCateBannerAdmin.get(i);
					Integer sort = bannerAdmin.getBannerSort();
					if (Objects.equals(sort,bannerSort)){
						entity.setBannerSort(sortValue);
						newBannerAdmin.add(entity);
						sortValue++;
					}
					bannerAdmin.setBannerSort(sortValue);
					newBannerAdmin.add(bannerAdmin);
					sortValue++;
				}
				bannerAdminRepository.saveAll(newBannerAdmin);
			}else {
				entity.setBannerSort(maxSort+1);
				bannerAdminRepository.save(entity);
			}
		}else {
			entity.setBannerSort(1);
			bannerAdminRepository.save(entity);
		}

		return entity;
	}

	/**
	 * 修改轮播管理
	 *
	 * @author 费传奇
	 */
	@Transactional(rollbackFor = Exception.class)
	public BannerAdmin modify(BannerAdmin entity) {
		Long oneCateId = entity.getOneCateId();
		Integer bannerSort = entity.getBannerSort();
		Long entityId = entity.getId();
		Long maxOneCateId = bannerAdminRepository.findBannerAdminByOneCateId(oneCateId);
		Optional<BannerAdmin> byIdAndDelFlag = bannerAdminRepository.findByIdAndDelFlag(entityId, DeleteFlag.NO);
		if (byIdAndDelFlag.isPresent()) {
			BannerAdmin bannerAdmin = byIdAndDelFlag.get();
			Long oldCateId = bannerAdmin.getOneCateId();
			Integer oldSort = bannerAdmin.getBannerSort();
			//查询且从小到大排序
			List<BannerAdmin> oneCateBannerAdmin = bannerAdminRepository.findByOneCateIdAndDelFlagOrderByBannerSortAsc(oneCateId,
					DeleteFlag.NO);
			List<BannerAdmin> newBannerAdmin = Lists.newArrayList();
			Integer sortValue = 1;

			List<BannerAdmin> bannerAdmins = oneCateBannerAdmin.stream().filter(c -> !Objects.equals(c.getId(),
					entityId)).collect(Collectors.toList());

			if (CollectionUtils.isEmpty(oneCateBannerAdmin) || CollectionUtils.isEmpty(bannerAdmins)){
				entity.setBannerSort(sortValue);
				bannerAdminRepository.save(entity);
				if (!Objects.equals(oldCateId,oneCateId)) {
					//改动分类需要对旧分类重新排序
					this.oldSort(oldCateId,entityId);
				}
				return entity;
			}

			int maxSort = bannerAdmins.stream().mapToInt(c -> c.getBannerSort()).max().getAsInt();
			//没有改分类的情况下
			if (Objects.equals(oldCateId,oneCateId)) {
			/*	if (Objects.nonNull(maxOneCateId) && maxOneCateId >maxCount){
					throw new SbcRuntimeException(CommonErrorCode.MaxCount);
				}*/
				if (Objects.isNull(bannerSort)){
					for (BannerAdmin admin : bannerAdmins) {
						admin.setBannerSort(sortValue);
						newBannerAdmin.add(admin);
						sortValue++;
					}
					entity.setBannerSort(sortValue);
					newBannerAdmin.add(entity);
					bannerAdminRepository.saveAll(newBannerAdmin);
					return entity;
				}
				//改动排序的情况下
				if (!Objects.equals(bannerSort,oldSort)) {
					return  getEntity(entity,bannerSort,bannerAdmins,maxSort);
				}else {
					bannerAdminRepository.save(entity);
					return entity;
				}
			}else {
				//改动分类的情况下
				if (Objects.nonNull(maxOneCateId) && maxOneCateId >= maxCount){
					throw new SbcRuntimeException(CommonErrorCode.MaxCount);
				}
				//改动分类需要对老分类进行重新排序
				this.oldSort(oldCateId,entityId);

				if (Objects.isNull(bannerSort)){
					for (BannerAdmin admin : bannerAdmins) {
						admin.setBannerSort(sortValue);
						newBannerAdmin.add(admin);
						sortValue++;
					}
					entity.setBannerSort(sortValue);
					newBannerAdmin.add(entity);
					bannerAdminRepository.saveAll(newBannerAdmin);
					return entity;
				}else {
					return  getEntity(entity,bannerSort,bannerAdmins,maxSort);
				}
			}
		}

		return entity;
	}


	/**
	 * 更新变动之前的分类排序
	 * @param oldCateId
	 * @param entityId
	 */
	public void oldSort(Long oldCateId,Long entityId){
		List<BannerAdmin> byBannerSortAsc =
				bannerAdminRepository.findByOneCateIdAndDelFlagOrderByBannerSortAsc(oldCateId,
						DeleteFlag.NO);
		List<BannerAdmin> oldBannerAdmins = byBannerSortAsc.stream().filter(c -> !Objects.equals(c.getId(),
				entityId)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(oldBannerAdmins)) {
			for (int i = 0; i < oldBannerAdmins.size(); i++) {
				BannerAdmin bannerAdmin1 = oldBannerAdmins.get(i);
				bannerAdmin1.setBannerSort(i+1);
			}
			bannerAdminRepository.saveAll(oldBannerAdmins);
		}
	}

	/**
	 * 排序相关业务抽取
	 * @param entity
	 * @param bannerSort
	 * @param bannerAdmins
	 * @param maxSort
	 * @return
	 */
	public BannerAdmin getEntity(BannerAdmin entity,Integer bannerSort,List<BannerAdmin>  bannerAdmins,
								Integer maxSort ){
		Integer sortValue = 1;
		List<BannerAdmin> newBannerAdmin = Lists.newArrayList();
		if (bannerSort ==1){
			newBannerAdmin.add(entity);
			sortValue++;
			for (BannerAdmin admin : bannerAdmins) {
				admin.setBannerSort(sortValue);
				newBannerAdmin.add(admin);
				sortValue++;
			}
			bannerAdminRepository.saveAll(newBannerAdmin);
			return entity;
		}if (bannerSort>maxSort){
			for (BannerAdmin admin : bannerAdmins) {
				admin.setBannerSort(sortValue);
				newBannerAdmin.add(admin);
				sortValue++;
			}
			entity.setBannerSort(sortValue);
			newBannerAdmin.add(entity);
			bannerAdminRepository.saveAll(newBannerAdmin);
			return entity;
		}
		//都在最大最小值范围内，且都是有序的  1,2,3,4,14,15  14->3   1,2,3,4  4-2  1,2* 3 4
		for (int i = 0; i < bannerAdmins.size(); i++) {
			BannerAdmin bannerAdmin1 = bannerAdmins.get(i);
			Integer bannerSort1 = bannerAdmin1.getBannerSort();
			if (Objects.equals(bannerSort1,bannerSort)){
				entity.setBannerSort(sortValue);
				newBannerAdmin.add(entity);
				sortValue++;
				bannerAdmin1.setBannerSort(sortValue);
				newBannerAdmin.add(bannerAdmin1);
				sortValue++;
			}else {
				bannerAdmin1.setBannerSort(sortValue);
				newBannerAdmin.add(bannerAdmin1);
				sortValue++;
			}
		}
		bannerAdminRepository.saveAll(newBannerAdmin);
		return entity;
	}


	/**
	 * 修改轮播管理
	 * @author 费传奇
	 */
	@Transactional(rollbackFor = Exception.class)
	public BannerAdmin modifyStatus(BannerAdmin entity) {
		Long id = entity.getId();
		Optional<BannerAdmin> byIdAndDelFlag = bannerAdminRepository.findByIdAndDelFlag(id, DeleteFlag.NO);
		if (byIdAndDelFlag.isPresent()) {
			BannerAdmin bannerAdmin = byIdAndDelFlag.get();
			bannerAdmin.setIsShow(entity.getIsShow());
			BannerAdmin save = bannerAdminRepository.save(bannerAdmin);
			return save;
		}
		return entity;
	}

	/**
	 * 单个删除轮播管理
	 * @author 费传奇
	 */
	@Transactional(rollbackFor = Exception.class)
	public void deleteById(BannerAdmin entity) {
		Long id = entity.getId();
		Optional<BannerAdmin> byIdAndDelFlag = bannerAdminRepository.findByIdAndDelFlag(id, DeleteFlag.NO);
		if (byIdAndDelFlag.isPresent()) {
			BannerAdmin bannerAdmin = byIdAndDelFlag.get();
			bannerAdmin.setDelFlag(entity.getDelFlag());
			bannerAdmin.setDeleteTime(LocalDateTime.now());
			Long oneCateId = bannerAdmin.getOneCateId();
			List<BannerAdmin> bannerAdmins =
					bannerAdminRepository.findByOneCateIdAndDelFlagOrderByBannerSortAsc(oneCateId, DeleteFlag.NO);
			//删除时进行批量排序
			Integer sortValue =1;
			for (BannerAdmin admin : bannerAdmins) {
				admin.setBannerSort(sortValue);
				sortValue++;
			}
			bannerAdmins.add(bannerAdmin);
			bannerAdminRepository.saveAll(bannerAdmins);
		}
	}

	/**
	 * 批量删除轮播管理
	 * @author 费传奇
	 */
	@Transactional(rollbackFor = Exception.class)
	public void deleteByIdList(List<BannerAdmin> infos) {
		bannerAdminRepository.saveAll(infos);
	}

	/**
	 * 单个查询轮播管理
	 * @author 费传奇
	 */
	public BannerAdmin getOne(Long id){
		return bannerAdminRepository.findByIdAndDelFlag(id, DeleteFlag.NO)
		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "轮播管理不存在"));
	}

	/**
	 * 分页查询轮播管理
	 * @author 费传奇
	 */
	public Page<BannerAdmin> page(BannerAdminQueryRequest queryReq){
		return bannerAdminRepository.findAll(
				BannerAdminWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询轮播管理
	 * @author 费传奇
	 */
	public List<BannerAdmin> list(BannerAdminQueryRequest queryReq){
		return bannerAdminRepository.findAll(BannerAdminWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 * @author 费传奇
	 */
	public BannerAdminVO wrapperVo(BannerAdmin bannerAdmin) {
		if (bannerAdmin != null){
			BannerAdminVO bannerAdminVO = KsBeanUtil.convert(bannerAdmin, BannerAdminVO.class);
			return bannerAdminVO;
		}
		return null;
	}
}


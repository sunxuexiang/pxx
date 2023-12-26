package com.wanmi.sbc.goods.catebrandsortrel.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.constant.EmployeeErrorCode;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.CateBrandSortRelQueryRequest;
import com.wanmi.sbc.goods.bean.vo.CateBrandSortRelVO;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.brand.request.GoodsBrandQueryRequest;
import com.wanmi.sbc.goods.brand.service.GoodsBrandService;
import com.wanmi.sbc.goods.catebrandsortrel.model.root.CateBrandRelId;
import com.wanmi.sbc.goods.catebrandsortrel.model.root.CateBrandSortRel;
import com.wanmi.sbc.goods.catebrandsortrel.repository.CateBrandSortRelRepository;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>类目品牌排序表业务逻辑</p>
 * @author lvheng
 * @date 2021-04-08 11:24:32
 */
@Service("CateBrandSortRelService")
@Slf4j
public class CateBrandSortRelService {
	@Autowired
	private CateBrandSortRelRepository cateBrandSortRelRepository;

	@Value("classpath:cate_brand_imp.xlsx")
	private Resource templateFile;

	@Autowired
	private GoodsBrandService goodsBrandService;


	/**
	 * 新增类目品牌排序表
	 * @author lvheng
	 */
	@Transactional
	public CateBrandSortRel add(CateBrandSortRel entity) {
		cateBrandSortRelRepository.save(entity);
		return entity;
	}

	/**
	 * 修改类目品牌排序表
	 * @author lvheng
	 */
	@Transactional
	public CateBrandSortRel modify(CateBrandSortRel entity) {
		cateBrandSortRelRepository.save(entity);
		return entity;
	}

	/**
	 * 单个删除类目品牌排序表
	 * @author lvheng
	 */
	@Transactional
	public void deleteById(Long cateId) {
		cateBrandSortRelRepository.deleteByCateId(cateId);
	}

	/**
	 * 批量删除类目品牌排序表
	 * @author lvheng
	 */
	@Transactional
	public void deleteByIdList(List<CateBrandSortRel> infos) {
		cateBrandSortRelRepository.saveAll(infos);
	}

	/**
	 * 单个查询类目品牌排序表
	 * @author lvheng
	 */
	public CateBrandSortRel getOne(CateBrandRelId cateBrandRelId){
//		return cateBrandSortRelRepository.findById(cateBrandRelId.getCateId(),cateBrandRelId.getBrandId(), DeleteFlag.NO)
//		.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "该类目品牌排序不存在"));

		return cateBrandSortRelRepository.findById(cateBrandRelId.getCateId(),cateBrandRelId.getBrandId(), DeleteFlag.NO).orElse(null);
	}

	/**
	 * 分页查询类目品牌排序表
	 * @author lvheng
	 */
	public Page<CateBrandSortRel> page(CateBrandSortRelQueryRequest queryReq){
		//别名查询
		if (StringUtils.isNotBlank(queryReq.getAlias())) {
			List<GoodsBrand> goodsBrands = goodsBrandService.query(GoodsBrandQueryRequest.builder().likeNickName(queryReq.getAlias()).build());
			List<Long> brandIds = goodsBrands.stream().map(GoodsBrand::getBrandId).collect(Collectors.toList());
			queryReq.setBrandIdList(brandIds);
		}

		log.info("-----------查询CateBrandSortRel-----参数：" + queryReq.toString() + "开始时间：" + LocalDateTime.now());
		Page<CateBrandSortRel> response  = cateBrandSortRelRepository.findAll(
				CateBrandSortRelWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
		log.info("-----------查询CateBrandSortRel-----结束时间：" + LocalDateTime.now());

		List<CateBrandSortRel> caterBrand = response.getContent();
		List<Long> brandIds = caterBrand.stream().map(CateBrandSortRel::getBrandId).collect(Collectors.toList());
		log.info("-----------查询GoodsBrand-----参数：" + brandIds.toString() + "开始时间：" + LocalDateTime.now());
		List<GoodsBrand> goodsBrands = goodsBrandService.findByIds(brandIds);
		log.info("-----------查询GoodsBrand-----结束时间：" + LocalDateTime.now());

		Map<Long, String> goodsBrandsMap = goodsBrands.stream()
				.collect(Collectors.toMap(a -> a.getBrandId(), b -> StringUtils.isNotBlank(b.getNickName()) ? b.getNickName() : ""));
		caterBrand.forEach(cateBrand -> cateBrand.setAlias(goodsBrandsMap.get(cateBrand.getBrandId())));
		return response;

	}

	/**
	 * 列表查询类目品牌排序表
	 * @author lvheng
	 */
	public List<CateBrandSortRel> list(CateBrandSortRelQueryRequest queryReq){
		return cateBrandSortRelRepository.findAll(CateBrandSortRelWhereCriteriaBuilder.build(queryReq), queryReq.getSort());
	}

	/**
	 * 将实体包装成VO
	 * @author lvheng
	 */
	public CateBrandSortRelVO wrapperVo(CateBrandSortRel cateBrandSortRel) {
		if (cateBrandSortRel != null){
			CateBrandSortRelVO cateBrandSortRelVO = KsBeanUtil.convert(cateBrandSortRel, CateBrandSortRelVO.class);
			return cateBrandSortRelVO;
		}
		return null;
	}

	public String exportTemplate() {
		if (templateFile == null || !templateFile.exists()) {
			throw new SbcRuntimeException(EmployeeErrorCode.TEMPLATE_NOT_SETTING);
		}
		try (InputStream is = templateFile.getInputStream();
			 ByteArrayOutputStream bos = new ByteArrayOutputStream();
			 Workbook wk = WorkbookFactory.create(is)) {
			wk.write(bos);
			return new BASE64Encoder().encode(bos.toByteArray());
		} catch (Exception e) {
			throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
		}
	}

	public Integer findBySort(Long cateId, Integer serialNo) {
		return cateBrandSortRelRepository.countByCateIdAndSerialNoAndDelFlag(cateId,serialNo,DeleteFlag.NO);
	}
}


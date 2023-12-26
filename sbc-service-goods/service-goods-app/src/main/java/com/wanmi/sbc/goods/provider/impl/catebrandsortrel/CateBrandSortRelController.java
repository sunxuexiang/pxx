package com.wanmi.sbc.goods.provider.impl.catebrandsortrel;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.catebrandsortrel.CateBrandSortRelProvider;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.*;
import com.wanmi.sbc.goods.api.response.catebrandsortrel.CateBrandSortRelAddResponse;
import com.wanmi.sbc.goods.api.response.catebrandsortrel.CateBrandSortRelModifyResponse;
import com.wanmi.sbc.goods.api.response.catebrandsortrel.CateBrandSortRelTemplateResponse;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.brand.service.GoodsBrandService;
import com.wanmi.sbc.goods.catebrandsortrel.model.root.CateBrandRelId;
import com.wanmi.sbc.goods.catebrandsortrel.model.root.CateBrandSortRel;
import com.wanmi.sbc.goods.catebrandsortrel.service.CateBrandSortRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>类目品牌排序表保存服务接口实现</p>
 * @author lvheng
 * @date 2021-04-08 11:24:32
 */
@RestController
@Validated
public class CateBrandSortRelController implements CateBrandSortRelProvider {
	@Autowired
	private CateBrandSortRelService cateBrandSortRelService;

	@Autowired
	private GoodsBrandService goodsBrandService;

	@Override
	public BaseResponse<CateBrandSortRelAddResponse> add(@RequestBody @Valid CateBrandSortRelAddRequest cateBrandSortRelAddRequest) {
		CateBrandSortRel cateBrandSortRel = KsBeanUtil.convert(cateBrandSortRelAddRequest, CateBrandSortRel.class);
		GoodsBrand goodsBrand = goodsBrandService.getByName(cateBrandSortRelAddRequest.getName());
		CateBrandRelId cateBrandRelId = new CateBrandRelId();
		cateBrandRelId.setBrandId(goodsBrand.getBrandId());
		cateBrandRelId.setCateId(cateBrandSortRelAddRequest.getCateId());
		return BaseResponse.success(new CateBrandSortRelAddResponse(
				cateBrandSortRelService.wrapperVo(cateBrandSortRelService.add(cateBrandSortRel))));
	}

	@Override
	public BaseResponse<CateBrandSortRelModifyResponse> modify(@RequestBody @Valid CateBrandSortRelModifyRequest cateBrandSortRelModifyRequest) {
		CateBrandRelId cateBrandRelId = new CateBrandRelId();
		cateBrandRelId.setCateId(cateBrandSortRelModifyRequest.getCateId());
		cateBrandRelId.setBrandId(cateBrandSortRelModifyRequest.getBrandId());

		CateBrandSortRel cateBrandSortRel = cateBrandSortRelService.getOne(cateBrandRelId);

		Integer bySort = cateBrandSortRelService.findBySort(cateBrandSortRelModifyRequest.getCateId(),
				 cateBrandSortRelModifyRequest.getSerialNo());
		if (!cateBrandSortRel.getSerialNo().equals(cateBrandSortRelModifyRequest.getSerialNo()) && bySort > 0) {
			throw new SbcRuntimeException("K-180007");
		}


		cateBrandSortRel.setSerialNo(cateBrandSortRelModifyRequest.getSerialNo());
		cateBrandSortRel.setUpdatePerson(cateBrandSortRelModifyRequest.getUpdatePerson());
		cateBrandSortRel.setUpdateTime(cateBrandSortRelModifyRequest.getUpdateTime());
		return BaseResponse.success(new CateBrandSortRelModifyResponse(
				cateBrandSortRelService.wrapperVo(cateBrandSortRelService.modify(cateBrandSortRel))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid CateBrandSortRelDelByIdRequest cateBrandSortRelDelByIdRequest) {
		cateBrandSortRelService.deleteById(cateBrandSortRelDelByIdRequest.getCateId());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid CateBrandSortRelDelByIdListRequest cateBrandSortRelDelByIdListRequest) {
		List<CateBrandSortRel> cateBrandSortRelList = cateBrandSortRelDelByIdListRequest.getCateIdList().stream()
			.map(CateId -> {
				CateBrandSortRel cateBrandSortRel = KsBeanUtil.convert(CateId, CateBrandSortRel.class);
				cateBrandSortRel.setDelFlag(DeleteFlag.YES);
				return cateBrandSortRel;
			}).collect(Collectors.toList());
		cateBrandSortRelService.deleteByIdList(cateBrandSortRelList);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse batchAdd(@Valid CateBrandSortRelBatchAddRequest relBatchAddRequest) {
		try {
			relBatchAddRequest.getCateBrandSortRelVO().forEach(item->{
				cateBrandSortRelService.add(KsBeanUtil.convert(item, CateBrandSortRel.class));
			});
		} catch (Exception e) {
			throw new SbcRuntimeException("K-180003");
		}
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<CateBrandSortRelTemplateResponse> exportTemplate() {
		String file = cateBrandSortRelService.exportTemplate();
		return BaseResponse.success(CateBrandSortRelTemplateResponse.builder().template(file).build());
	}

}


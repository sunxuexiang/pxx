package com.wanmi.sbc.goods.provider.impl.catebrandsortrel;

import com.wanmi.sbc.goods.catebrandsortrel.model.root.CateBrandRelId;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.catebrandsortrel.CateBrandSortRelQueryProvider;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.CateBrandSortRelPageRequest;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.CateBrandSortRelQueryRequest;
import com.wanmi.sbc.goods.api.response.catebrandsortrel.CateBrandSortRelPageResponse;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.CateBrandSortRelListRequest;
import com.wanmi.sbc.goods.api.response.catebrandsortrel.CateBrandSortRelListResponse;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.CateBrandSortRelByIdRequest;
import com.wanmi.sbc.goods.api.response.catebrandsortrel.CateBrandSortRelByIdResponse;
import com.wanmi.sbc.goods.bean.vo.CateBrandSortRelVO;
import com.wanmi.sbc.goods.catebrandsortrel.service.CateBrandSortRelService;
import com.wanmi.sbc.goods.catebrandsortrel.model.root.CateBrandSortRel;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>类目品牌排序表查询服务接口实现</p>
 * @author lvheng
 * @date 2021-04-08 11:24:32
 */
@RestController
@Validated
public class CateBrandSortRelQueryController implements CateBrandSortRelQueryProvider {
	@Autowired
	private CateBrandSortRelService cateBrandSortRelService;

	@Override
	public BaseResponse<CateBrandSortRelPageResponse> page(@RequestBody @Valid CateBrandSortRelPageRequest cateBrandSortRelPageReq) {
		CateBrandSortRelQueryRequest queryReq = KsBeanUtil.convert(cateBrandSortRelPageReq, CateBrandSortRelQueryRequest.class);
		Page<CateBrandSortRel> cateBrandSortRelPage = cateBrandSortRelService.page(queryReq);
		Page<CateBrandSortRelVO> newPage = cateBrandSortRelPage.map(entity -> cateBrandSortRelService.wrapperVo(entity));
		MicroServicePage<CateBrandSortRelVO> microPage = new MicroServicePage<>(newPage, cateBrandSortRelPageReq.getPageable());
		CateBrandSortRelPageResponse finalRes = new CateBrandSortRelPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<CateBrandSortRelListResponse> list(@RequestBody @Valid CateBrandSortRelListRequest cateBrandSortRelListReq) {
		CateBrandSortRelQueryRequest queryReq = KsBeanUtil.convert(cateBrandSortRelListReq, CateBrandSortRelQueryRequest.class);
		List<CateBrandSortRel> cateBrandSortRelList = cateBrandSortRelService.list(queryReq);
		List<CateBrandSortRelVO> newList = cateBrandSortRelList.stream().map(entity -> cateBrandSortRelService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new CateBrandSortRelListResponse(newList));
	}

	@Override
	public BaseResponse<CateBrandSortRelByIdResponse> getById(@RequestBody @Valid CateBrandSortRelByIdRequest cateBrandSortRelByIdRequest) {
		CateBrandRelId cateBrandRelId = new CateBrandRelId();
		cateBrandRelId.setBrandId(cateBrandSortRelByIdRequest.getBrandId());
		cateBrandRelId.setCateId(cateBrandSortRelByIdRequest.getCateId());

		CateBrandSortRel cateBrandSortRel =
		cateBrandSortRelService.getOne(cateBrandRelId);

		if(cateBrandSortRel == null){
			return BaseResponse.success(new CateBrandSortRelByIdResponse());
		}

		return BaseResponse.success(new CateBrandSortRelByIdResponse(cateBrandSortRelService.wrapperVo(cateBrandSortRel)));
	}

}


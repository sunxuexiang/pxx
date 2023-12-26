package com.wanmi.sbc.goods.provider.impl.goodsevaluate;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodsevaluate.GoodsEvaluateQueryProvider;
import com.wanmi.sbc.goods.api.request.goodsevaluate.*;
import com.wanmi.sbc.goods.api.response.goodsevaluate.GoodsEvaluateByIdResponse;
import com.wanmi.sbc.goods.api.response.goodsevaluate.GoodsEvaluateCountResponse;
import com.wanmi.sbc.goods.api.response.goodsevaluate.GoodsEvaluateListResponse;
import com.wanmi.sbc.goods.api.response.goodsevaluate.GoodsEvaluatePageResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsEvaluateVO;
import com.wanmi.sbc.goods.goodsevaluate.model.root.GoodsEvaluate;
import com.wanmi.sbc.goods.goodsevaluate.service.GoodsEvaluateService;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.repository.GoodsInfoRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>商品评价查询服务接口实现</p>
 * @author liutao
 * @date 2019-02-25 15:14:16
 */
@RestController
@Validated
public class GoodsEvaluateQueryController implements GoodsEvaluateQueryProvider {
	@Autowired
	private GoodsEvaluateService goodsEvaluateService;

	@Autowired
	private GoodsInfoRepository goodsInfoRepository;
	/**
	 * 分页查询商品评价
	 *
	 * @param goodsEvaluatePageReq 分页请求参数和筛选对象 {@link GoodsEvaluatePageRequest}
	 * @return
	 */
	@Override
	public BaseResponse<GoodsEvaluatePageResponse> page(@RequestBody @Valid GoodsEvaluatePageRequest goodsEvaluatePageReq) {
		GoodsEvaluateQueryRequest queryReq = new GoodsEvaluateQueryRequest();
		KsBeanUtil.copyPropertiesThird(goodsEvaluatePageReq, queryReq);
		Page<GoodsEvaluate> goodsEvaluatePage = goodsEvaluateService.page(queryReq);
		Page<GoodsEvaluateVO> newPage = goodsEvaluatePage.map(entity -> goodsEvaluateService.wrapperVo(entity));
		MicroServicePage<GoodsEvaluateVO> microPage = new MicroServicePage<>(newPage, goodsEvaluatePageReq.getPageable());
		GoodsEvaluatePageResponse finalRes = new GoodsEvaluatePageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<GoodsEvaluateListResponse> list(@RequestBody @Valid GoodsEvaluateListRequest goodsEvaluateListReq) {
		GoodsEvaluateQueryRequest queryReq = new GoodsEvaluateQueryRequest();
		KsBeanUtil.copyPropertiesThird(goodsEvaluateListReq, queryReq);
		List<GoodsEvaluate> goodsEvaluateList = goodsEvaluateService.list(queryReq);
		List<GoodsEvaluateVO> newList = goodsEvaluateList.stream().map(entity -> goodsEvaluateService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new GoodsEvaluateListResponse(newList));
	}

	@Override
	public BaseResponse<GoodsEvaluateByIdResponse> getById(@RequestBody @Valid GoodsEvaluateByIdRequest goodsEvaluateByIdRequest) {
		GoodsEvaluate goodsEvaluate = goodsEvaluateService.getById(goodsEvaluateByIdRequest.getEvaluateId());
		return BaseResponse.success(new GoodsEvaluateByIdResponse(goodsEvaluateService.wrapperVo(goodsEvaluate)));
	}

	@Override
	public BaseResponse<Long> getGoodsEvaluateNum(@RequestBody  GoodsEvaluateQueryRequest queryReq){
		return BaseResponse.success(goodsEvaluateService.getGoodsEvaluateNum(queryReq));
	}

	/**
	 * @param request {@link GoodsEvaluateCountRequset}
	 * @Description: 该spu商品评价总数、晒单、好评率
	 * @Author: Bob
	 * @Date: 2019-04-04 16:17
	 */
	@Override
	public BaseResponse<GoodsEvaluateCountResponse> getGoodsEvaluateSum(@RequestBody @Valid GoodsEvaluateCountRequset request) {
		//商品详情中的评价总数不加是否展示条件，列表数据加了是否展示条件
		GoodsEvaluateQueryRequest queryRequest =
				GoodsEvaluateQueryRequest.builder().goodsId(request.getGoodsId()).delFlag(0).build();
		Long count = goodsEvaluateService.getGoodsEvaluateNum(queryRequest);
		queryRequest.setIsUpload(1);
		Long uploadCount = goodsEvaluateService.getGoodsEvaluateNum(queryRequest);
		String praise = goodsEvaluateService.getGoodsPraise(request);
		return BaseResponse.success(GoodsEvaluateCountResponse.builder().evaluateConut(count).postOrderCount(uploadCount)
				.praise(praise).build());
	}

	/**
	 * @param request {@link GoodsEvaluatePageRequest}
	 * @Description: 查询最新评价<排除系统评价>
	 * @Author: Bob
	 * @Date: 2019-05-29 17:49
	 */
	@Override
	public BaseResponse<GoodsEvaluateListResponse> getGoodsEvaluateTopData(@RequestBody @Valid GoodsEvaluatePageRequest request) {
		List<GoodsEvaluate> goodsEvaluateList = goodsEvaluateService.getTop(request);
		List<GoodsEvaluateVO> newList = goodsEvaluateList.stream().map(entity -> goodsEvaluateService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new GoodsEvaluateListResponse(newList));
	}

	/**
	 * @param
	 * @return
	 * @discription 根据skuid查该spu商品评价总数、晒单、好评率
	 * @author yangzhen
	 * @date 2020/9/2 14:00
	 */
	@Override
	public BaseResponse<GoodsEvaluateCountResponse> getGoodsEvaluateSumByskuId(@Valid GoodsEvaluateCountBySkuIdRequset request) {

		List<GoodsInfo> goodsInfos = goodsInfoRepository.findByGoodsInfoIds(Arrays.asList(request.getSkuId()));
		if (CollectionUtils.isNotEmpty(goodsInfos)) {
			//商品详情中的评价总数不加是否展示条件，列表数据加了是否展示条件
			GoodsEvaluateQueryRequest queryRequest =
					GoodsEvaluateQueryRequest.builder().goodsId(goodsInfos.get(0).getGoodsId()).delFlag(0).build();
			Long count = goodsEvaluateService.getGoodsEvaluateNum(queryRequest);
			queryRequest.setIsUpload(1);
			Long uploadCount = goodsEvaluateService.getGoodsEvaluateNum(queryRequest);
			String praise = goodsEvaluateService.getGoodsPraise(GoodsEvaluateCountRequset
					.builder()
					.goodsId(goodsInfos.get(0).getGoodsId())
					.build());
			return BaseResponse.success(GoodsEvaluateCountResponse.builder().evaluateConut(count).postOrderCount(uploadCount)
					.praise(praise).goodsId(goodsInfos.get(0).getGoodsId()).build());
		}
		return BaseResponse.FAILED();
	}
}


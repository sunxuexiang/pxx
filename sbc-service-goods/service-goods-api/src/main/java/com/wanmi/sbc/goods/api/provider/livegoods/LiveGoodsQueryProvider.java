package com.wanmi.sbc.goods.api.provider.livegoods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.livegoods.LiveGoodsBySkuIdRequest;
import com.wanmi.sbc.goods.api.request.livegoods.LiveGoodsPageRequest;
import com.wanmi.sbc.goods.api.response.livegoods.LiveGoodsBySkuIdResponse;
import com.wanmi.sbc.goods.api.response.livegoods.LiveGoodsPageResponse;
import com.wanmi.sbc.goods.api.request.livegoods.LiveGoodsListRequest;
import com.wanmi.sbc.goods.api.response.livegoods.LiveGoodsListResponse;
import com.wanmi.sbc.goods.api.request.livegoods.LiveGoodsByIdRequest;
import com.wanmi.sbc.goods.api.response.livegoods.LiveGoodsByIdResponse;
import com.wanmi.sbc.goods.bean.vo.LiveGoodsVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>直播商品查询服务Provider</p>
 * @author zwb
 * @date 2020-06-10 11:05:45
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "LiveGoodsQueryProvider")
public interface LiveGoodsQueryProvider {

	/**
	 * 分页查询直播商品API
	 *
	 * @author zwb
	 * @param liveGoodsPageReq 分页请求参数和筛选对象 {@link LiveGoodsPageRequest}
	 * @return 直播商品分页列表信息 {@link LiveGoodsPageResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/livegoods/page")
	BaseResponse<LiveGoodsPageResponse> page(@RequestBody @Valid LiveGoodsPageRequest liveGoodsPageReq);

	/**
	 * 列表查询直播商品API
	 *
	 * @author zwb
	 * @param liveGoodsListReq 列表请求参数和筛选对象 {@link LiveGoodsListRequest}
	 * @return 直播商品的列表信息 {@link LiveGoodsListResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/livegoods/list")
	BaseResponse<LiveGoodsListResponse> list(@RequestBody @Valid LiveGoodsListRequest liveGoodsListReq);

	/**
	 * 单个查询直播商品API
	 *
	 * @author zwb
	 * @param liveGoodsByIdRequest 单个查询直播商品请求参数 {@link LiveGoodsByIdRequest}
	 * @return 直播商品详情 {@link LiveGoodsByIdResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/livegoods/get-by-id")
	BaseResponse<LiveGoodsByIdResponse> getById(@RequestBody @Valid LiveGoodsByIdRequest liveGoodsByIdRequest);


	/**
	 * 通过SkuId获取商品的直播信息
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/livegoods/get-room-by-sku-id")
	BaseResponse<LiveGoodsBySkuIdResponse> getRoomInfoBySkuId(@RequestBody @Valid LiveGoodsBySkuIdRequest request);

	/**
	 * 通过商品skuid集合获取商品的直播信息
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/livegoods/get-room-by-goods-info-id")
	BaseResponse<List<LiveGoodsVO>> getRoomInfoByGoodsInfoId(@RequestBody List<String> request);

}


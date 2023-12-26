package com.wanmi.sbc.goods.api.provider.goodswarestock;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.goodswarestock.*;
import com.wanmi.sbc.goods.api.response.goodswarestock.*;
import com.wanmi.sbc.goods.api.response.shortages.ShortagesGoodsInfoResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>sku分仓库存表查询服务Provider</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:22:56
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsWareStockQueryProvider")
public interface GoodsWareStockQueryProvider {

	/**
	 * 分页查询sku分仓库存表API
	 *
	 * @author zhangwenchang
	 * @param goodsWareStockPageReq 分页请求参数和筛选对象 {@link GoodsWareStockPageRequest}
	 * @return sku分仓库存表分页列表信息 {@link GoodsWareStockPageResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestock/page")
    BaseResponse<GoodsWareStockPageResponse> page(@RequestBody @Valid GoodsWareStockPageRequest goodsWareStockPageReq);

	/**
	 * 列表查询sku分仓库存表API
	 *
	 * @author zhangwenchang
	 * @param goodsWareStockListReq 列表请求参数和筛选对象 {@link GoodsWareStockListRequest}
	 * @return sku分仓库存表的列表信息 {@link GoodsWareStockListResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestock/list")
    BaseResponse<GoodsWareStockListResponse> list(@RequestBody @Valid GoodsWareStockListRequest goodsWareStockListReq);

	/**
	 * 单个查询sku分仓库存表API
	 *
	 * @author zhangwenchang
	 * @param goodsWareStockByIdRequest 单个查询sku分仓库存表请求参数 {@link GoodsWareStockByIdRequest}
	 * @return sku分仓库存表详情 {@link GoodsWareStockByIdResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestock/get-by-id")
    BaseResponse<GoodsWareStockByIdResponse> getById(@RequestBody @Valid GoodsWareStockByIdRequest goodsWareStockByIdRequest);

	/**
	 * 根据区域ids，查询商品库存
	 * @param request
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestock/get-goods-stock-by-area-ids")
    BaseResponse<GoodsWareStockByAreaIdsResponse> getGoodsStockByAreaIds(@RequestBody @Valid GoodsWareStockByAreaIdsRequest request);

	/**
	 * 根据区域ids，查询商品库存
	 * @param request
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestock/get-goods-stock-by-area-ids-and-goods-info-ids")
    BaseResponse<GoodsWareStockListResponse> getGoodsStockByAreaIdsAndGoodsInfoIds(@RequestBody @Valid StockQueryByAresAndInfoIdListRequest request);

	/**
	 * 初始化列表查询sku分仓库存表API
	 *
	 * @author zhangwenchang
	 * @param goodsWareStockListReq 列表请求参数和筛选对象 {@link GoodsWareStockListRequest}
	 * @return sku分仓库存表的列表信息 {@link GoodsWareStockListResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestock/list-init")
    BaseResponse<GoodsWareStockInitListResponse> initList(@RequestBody @Valid GoodsWareStockListRequest goodsWareStockListReq);

	/**
	 * 根据商品goodsInfoIdList查询对应的商品库存数量
	 * @param request
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestock/get-goods-stock-by--goods-info-ids")
    BaseResponse<GoodsWareStockListResponse> getGoodsWareStockByGoodsInfoIds(@RequestBody @Valid GoodsWareStockByGoodsForIdsRequest request);

	/**
	 * 根据商品goodsInfoIdList查询对应的商品库存数量(乡镇件压舱)
	 * @param request
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestock/get-goods-stock-by--goods-info-ids-jiya")
	BaseResponse<Map<String, BigDecimal>> getGoodsWareStockByGoodsInfoIdsjiya(@RequestBody @Valid GoodsWareStockByGoodsForIdsRequest request);

	/**
	 * 根据商品goodsInfoNoList查询对应的商品库存数量
	 * @param request
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestock/get-goods-stock-by--goods-info-nos")
	BaseResponse<GoodsWareStockListResponse> getGoodsWareStockByGoodsInfoNos(@RequestBody @Valid GoodsWareStockByGoodsForNoRequest request);

	/**
	 * 根据商品goodsInfoIdList查询对应的商品库存数量
	 * @param request
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestock/get-goods-stock-by-sku-ids")
	BaseResponse<GoodsWareStockListResponse> findByGoodsInfoIdIn(@RequestBody @Valid GoodsWareStockByGoodsForIdsRequest request);


	/**
	 * 根据商品goodsInfoIdList查询对应的商品库存数量
	 * @param request
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/goodswarestock/get-goods-stock-by-sku-ids-and-ware-id")
	BaseResponse<GoodsWareStockListResponse> getGoodsStockByWareIdAndStoreId(@RequestBody @Valid GoodsWareStockByWareIdAndStoreIdRequest request);


	@PostMapping("/goods/${application.goods.version}/goodswarestock/find-ware-stock-by-goods-id")
	BaseResponse<GoodsWareStockByGoodsIdResponse>  findByGoodsIdIn(@RequestBody @Valid GoodsWareStockByGoodsIdRequest request);

	@PostMapping("/goods/${application.goods.version}/goodswarestock/get-inventory")
	BaseResponse<List<Object[]>>  getInventory();

	@PostMapping("/goods/${application.goods.version}/goodswarestock/get-shortages-goods-infos")
	BaseResponse<ShortagesGoodsInfoResponse> getShortagesGoodsInfos();


	@PostMapping("/goods/${application.goods.version}/goodswarestock/get-stock-goodsinfoid-and-wareid")
	BaseResponse<GoodsWareStockListResponse> queryWareStockByWareIdAndGoodsInfoId(@RequestBody @Valid  GoodsWareStockByGoodsInfoIdAndWareIdRequest request);

	@PostMapping("/goods/${application.goods.version}/goodswarestock/get-stocklist-goodsinfoidlist-and-wareid")
	BaseResponse<GoodsWareStockListResponse> queryWareStockByWareIdAndGoodsInfoIdList(@RequestBody @Valid  GoodsWareStockListRequest goodsWareStockListReq);

	@GetMapping("/goods/${application.goods.version}/goodswarestock/get-onestock-goodsinfoid-and-wareid")
	BaseResponse<GoodsWareStockVO> getWareStockByWareIdAndInfoId(@RequestParam(value="goodsInfoId") String goodsInfoId,@RequestParam(value="wareId") Long wareId);

}


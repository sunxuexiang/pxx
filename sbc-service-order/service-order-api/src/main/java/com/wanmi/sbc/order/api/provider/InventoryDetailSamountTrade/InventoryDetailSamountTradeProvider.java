package com.wanmi.sbc.order.api.provider.InventoryDetailSamountTrade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.request.InventoryDetailSamount.InventoryDetailSamountRequest;
import com.wanmi.sbc.order.api.request.InventoryDetailSamountTrade.InventoryDetailSamountTradeRequest;
import com.wanmi.sbc.order.api.response.inventorydetailsamounttrade.InventoryDetailSamountTradeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;


@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "InventoryDetailSamountTradeProvider")
public interface InventoryDetailSamountTradeProvider {


	/**
	 * 插入
	 * @param inventoryDetailSamountTradeRequest
	 * @return
	 */
	@PostMapping("/order/${application.order.version}/inventorydetailsamounttrade/save-goods-share-money")
	BaseResponse  saveGoodsShareMoney(@RequestBody @Valid InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest);

	@PostMapping("/order/${application.order.version}/inventorydetailsamounttrade/update-goods-share-money")
	BaseResponse  updateGoodsShareMoney(@RequestBody @Valid InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest);

	/**
	 * 修改
	 * @param inventoryDetailSamountTradeRequest
	 * @return
	 */
	@PostMapping("/order/${application.order.version}/inventorydetailsamounttrade/update-inventory-detail-samount-return-flag")
	BaseResponse  updateInventoryDetailSamountReturnFlag(@RequestBody @Valid InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest);


	/**
	 * 修改
	 * @param inventoryDetailSamountTradeRequest
	 * @return
	 */
	@PostMapping("/order/${application.order.version}/inventorydetailsamounttrade/update-inventory-detail-samount-flag")
	BaseResponse  updateInventoryDetailSamountFlag(@RequestBody @Valid InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest);

	/**
	 * 查询方法, 尽量请使用此方法
     * -- 此方法已处理老数据
     *
	 * @param inventoryDetailSamountTradeRequest
	 * @return
	 */
	@PostMapping("/order/${application.order.version}/inventorydetailsamounttrade/get-tiinventory-adaptive")
	BaseResponse<InventoryDetailSamountTradeResponse>  getInventoryAdaptive(@RequestBody @Valid InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest);

	/**
	 * 通过订单id获取所有商品价格
     * -- 此方法已处理老数据
     *
	 * @param inventoryDetailSamountTradeRequest
	 * @return
	 */
	@PostMapping("/order/${application.order.version}/inventorydetailsamounttrade/get-tiinventory-all-take-id")
	BaseResponse<InventoryDetailSamountTradeResponse>  getInventoryAllByOId(@RequestBody @Valid InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest);

	/**
	 * 查询可退的
	 * @param inventoryDetailSamountTradeRequest
	 * @return
	 */
	@PostMapping("/order/${application.order.version}/inventorydetailsamounttrade/get-no-tiinventory-take-id")
	BaseResponse<InventoryDetailSamountTradeResponse>  getInventoryByReturnId(@RequestBody @Valid InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest);

	/**
	 * 查询可退的
	 * @param inventoryDetailSamountTradeRequest
	 * @return
	 */
	@PostMapping("/order/${application.order.version}/inventorydetailsamounttrade/get-tiinventory-oid")
	BaseResponse<InventoryDetailSamountTradeResponse>  getInventoryByOId(@RequestBody @Valid InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest);

	/**
	 * 查询可退的
	 * @param inventoryDetailSamountTradeRequest
	 * @return
	 */
	@PostMapping("/order/${application.order.version}/inventorydetailsamounttrade/update-tiinventory-take-id-back")
	BaseResponse<InventoryDetailSamountTradeResponse>  updateInventoryByReturnIdBack(@RequestBody @Valid InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest);


	/**
	 * 查询可退的
	 * @param inventoryDetailSamountTradeRequest
	 * @return
	 */
	@PostMapping("/order/${application.order.version}/inventorydetailsamounttrade/get-no-tiinventory-detail-samount")
	BaseResponse<InventoryDetailSamountTradeResponse>  getNoTiinventoryDetailSamount(@RequestBody @Valid InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest);

}


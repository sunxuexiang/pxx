package com.wanmi.sbc.order.api.provider.InventoryDetailSamount;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.request.InventoryDetailSamount.InventoryDetailSamountRequest;
import com.wanmi.sbc.order.api.request.InventoryDetailSamount.SubInventoryDetailSamountRequest;
import com.wanmi.sbc.order.api.request.historytownshiporder.HistoryTownShipOrderStockRequest;
import com.wanmi.sbc.order.api.response.histoytownshiporder.HistoryTownShipOrderStockResponse;
import com.wanmi.sbc.order.api.response.inventorydetailsamount.InventoryDetailSamountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;


@FeignClient(value = "${application.order.name}", url="${feign.url.order:#{null}}", contextId = "InventoryDetailSamountProvider")
public interface InventoryDetailSamountProvider {


	/**
	 * 插入
	 * @param inventoryDetailSamountRequest
	 * @return
	 */
	@PostMapping("/order/${application.order.version}/inventorydetailsamount/save-goods-share-money")
	BaseResponse  saveGoodsShareMoney(@RequestBody @Valid InventoryDetailSamountRequest inventoryDetailSamountRequest);


	/**
	 * 修改(作废)
	 * @param inventoryDetailSamountRequest
	 * @return
	 *//*
	@PostMapping("/order/${application.order.version}/inventorydetailsamount/update-inventory-detail-samount-flag")
	BaseResponse  updateInventoryDetailSamountFlag(@RequestBody @Valid InventoryDetailSamountRequest inventoryDetailSamountRequest);*/

	/**
	 * 获取本次使用提货商品价格
	 * @param inventoryDetailSamountRequest
	 * @return
	 */
	@PostMapping("/order/${application.order.version}/inventorydetailsamount/get-inventory-detail-samount-flag")
	BaseResponse<InventoryDetailSamountResponse>  getInventoryDetailSamountFlag(@RequestBody @Valid InventoryDetailSamountRequest inventoryDetailSamountRequest);

	/**
	 *
	 * @param inventoryDetailSamountRequest
	 * @return
	 */
	@PostMapping("/order/${application.order.version}/inventorydetailsamount/get-no-tiinventory-take-id")
	BaseResponse<InventoryDetailSamountResponse>  getInventoryByTakeId(@RequestBody @Valid InventoryDetailSamountRequest inventoryDetailSamountRequest);

	/**
	 *
	 * @param inventoryDetailSamountRequest
	 * @return
	 */
	@PostMapping("/order/${application.order.version}/inventorydetailsamount/get-no-tiinventory-take-ids")
	BaseResponse<InventoryDetailSamountResponse>  getInventoryByTakeIds(@RequestBody @Valid InventoryDetailSamountRequest inventoryDetailSamountRequest);

	/**
	 * 获取本次使用提货商品价格
	 * @param subInventoryDetailSamountRequest
	 * @return
	 */
	@PostMapping("/order/${application.order.version}/inventorydetailsamount/sub-tiinventory-take-id")
	BaseResponse subInventoryDetailSamountFlag(@RequestBody @Valid SubInventoryDetailSamountRequest subInventoryDetailSamountRequest);

	/**
	 * 查询可退的
	 * @param inventoryDetailSamountRequest
	 * @return
	 */
	@PostMapping("/order/${application.order.version}/inventorydetailsamount/get-tiinventory-oid")
	BaseResponse<InventoryDetailSamountResponse>  getInventoryByOId(@RequestBody @Valid InventoryDetailSamountRequest inventoryDetailSamountRequest);

	/**
	 * 查询可退的
	 * @param inventoryDetailSamountRequest
	 * @return
	 */
	@PostMapping("/order/${application.order.version}/inventorydetailsamount/update-tiinventory-take-id-back")
	BaseResponse<InventoryDetailSamountResponse>  updateInventoryByTakeIdBack(@RequestBody @Valid InventoryDetailSamountRequest inventoryDetailSamountRequest);


	/**
	 * 查询可退的
	 * @param inventoryDetailSamountRequest
	 * @return
	 */
	@PostMapping("/order/${application.order.version}/inventorydetailsamount/get-no-tiinventory-detail-samount")
	BaseResponse<InventoryDetailSamountResponse>  getNoTiinventoryDetailSamount(@RequestBody @Valid InventoryDetailSamountRequest inventoryDetailSamountRequest);

	@PostMapping("/order/${application.order.version}/inventorydetailsamount/unlockAmountByRid/{rid}")
	BaseResponse<Void> unlockAmountByRid(@PathVariable(value = "rid") String rid);

	@PostMapping("/order/${application.order.version}/inventorydetailsamount/returnAmountByRid/{rid}")
	BaseResponse<Void> returnAmountByRid(@PathVariable(value = "rid") String rid);
}


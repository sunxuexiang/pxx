package com.wanmi.sbc.goods.api.provider.freight;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.freight.FreightStoreSyncRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaListRequest;
import com.wanmi.sbc.goods.api.response.freight.FreightTemplateDeliveryAreaByStoreIdResponse;
import com.wanmi.sbc.goods.bean.vo.FreightTemplateDeliveryAreaVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>配送到家范围查询服务Provider</p>
 * @author zhaowei
 * @date 2021-03-25 16:57:57
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "FreightTemplateDeliveryAreaQueryProvider")
public interface FreightTemplateDeliveryAreaQueryProvider {


	/**
	 * 列表查询配送到家范围API
	 *
	 * @author zhaowei
	 * @param freightTemplateDeliveryAreaListReq 列表请求参数和筛选对象 {@link FreightTemplateDeliveryAreaListRequest}
	 * @return 配送到家范围的列表信息 {@link }
	 */
	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/query")
	BaseResponse<List<FreightTemplateDeliveryAreaByStoreIdResponse>> query(@RequestBody @Valid FreightTemplateDeliveryAreaListRequest freightTemplateDeliveryAreaListReq);

	/**
	 * @desc  订单查询配置
	 * @author shiy  2023/7/4 9:17
	 */
	@GetMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/query-by-storeId-wareId-desttype")
	BaseResponse<FreightTemplateDeliveryAreaVO> queryByStoreIdAndWareIdAndDestinationType(@RequestParam(value="storeId")Long storeId, @RequestParam(value="wareId")Long wareId, @RequestParam(value="destinationType") Integer destinationType);

	/**
	 * @desc  配送方式
	 * @author shiy  2023/7/4 9:17
	 */
	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/query-by-storeId-wareId")
	BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryByStoreIdAndWareIdAndOpened(@RequestBody @Valid FreightTemplateDeliveryAreaListRequest freightTemplateDeliveryAreaListReq);

	/**
	 * @desc  免费店配
	 * @author shiy  2023/7/4 9:17
	 */
	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/query-delivery-home-confifg")
	BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryDeliveryHomeConfifg(@RequestBody @Valid FreightTemplateDeliveryAreaListRequest freightTemplateDeliveryAreaListReq);
	
	/**
	 * @desc  配送到店区域
	 * @author shiy  2023/11/6 17:25
	*/
	@GetMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/queryToStoreAreaVO")
	BaseResponse<FreightTemplateDeliveryAreaVO> queryToStoreAreaVO();

	/**
	 * @desc  配送到店区域
	 * @author shiy  2023/11/6 17:25
	 */
	@GetMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/queryToStoreAreaVOByProvinceId")
	BaseResponse<FreightTemplateDeliveryAreaVO> queryToStoreAreaVOByProvinceId(@RequestParam(value="provinceId")Long provinceId);

	/**
	 * @desc  配送到店乡镇件
	 * @author shiy  2023/11/6 17:26
	*/
	@GetMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/queryToStoreAreaVillageVO")
	BaseResponse<FreightTemplateDeliveryAreaVO> queryToStoreAreaVillageVO();
	@GetMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/queryToStoreAreaVillageVOByProvinceId")
	BaseResponse<FreightTemplateDeliveryAreaVO> queryToStoreAreaVillageVOByProvinceId(@RequestParam(value="provinceId")Long provinceId);
	

	/**
	 * @desc  免费店配
	 * @author shiy  2023/7/4 9:17
	*/
	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/query-free-store-dilivery")
	BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryFreeStoreDilivery(@RequestBody @Valid FreightTemplateDeliveryAreaListRequest freightTemplateDeliveryAreaListReq);

	/**
	 * @desc  乡镇件
	 * @author shiy  2023/7/4 9:17
	 */
	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/query-villages-dilivery")
	BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryVillagesDilivery(@RequestBody @Valid FreightTemplateDeliveryAreaListRequest freightTemplateDeliveryAreaListReq);

	/**
	 * @desc  第三方物流
	 * @author shiy  2023/7/4 9:17
	 */
	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/query-third-logistics-dilivery")
	BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryThirdLogisticsDilivery(@RequestBody @Valid FreightTemplateDeliveryAreaListRequest freightTemplateDeliveryAreaListReq);

	/**
	 * 列表查询配送到家范围API
	 *
	 * @author zhaowei
	 * @param freightTemplateDeliveryAreaListReq 列表请求参数和筛选对象 {@link FreightTemplateDeliveryAreaListRequest}
	 * @return 配送到家范围的列表信息 {@link }
	 */
	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/queryByStoreId")
	BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryByStoreId(@RequestBody @Valid FreightTemplateDeliveryAreaListRequest freightTemplateDeliveryAreaListReq);

	/**
	 * @desc  上门自提
	 * @author shiy  2023/7/4 9:17
	 */
	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/query-to-door-pick")
    BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryToDoorPick(@RequestBody @Valid FreightTemplateDeliveryAreaListRequest queryRequest);

	/**
	 * @desc  收费快递
	 * @author shiy  2023/8/11 9:17
	 */
	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/query-paid-express")
	BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryPaidExpress(@RequestBody @Valid FreightTemplateDeliveryAreaListRequest queryRequest);

	/**
	 * @desc  配送到店
	 * @author shiy  2023/8/11 9:17
	 */
	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/delivery-to-store")
	BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryDeliveryToStore(@RequestBody @Valid FreightTemplateDeliveryAreaListRequest queryRequest);

	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/delivery-to-store-boss")
	BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryDeliveryToStoreBoss(@RequestBody @Valid FreightTemplateDeliveryAreaListRequest queryRequest);

	/**
	 * @desc  获取配送到店
	 * @author shiy  2023/8/16 16:34
	*/
	@GetMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/querySupplierUseDeliveryToStore")
	BaseResponse<List<Long>> querySupplierUseDeliveryToStore(@RequestParam(value="destinationType") Integer destinationType);

	@GetMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/queryIsToStoreVillageFlag")
	BaseResponse<Boolean> queryIsToStoreVillageFlag(@RequestParam(value="cityId") Long cityId,@RequestParam(value="townId") Long townId);
	@GetMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/queryIsToStoreVillageFlagWithProvinceId")
	BaseResponse<Boolean> queryIsToStoreVillageFlag(@RequestParam(value="provinceId") Long provinceId,@RequestParam(value="cityId") Long cityId,@RequestParam(value="townId") Long townId);

	/***
	 * @desc 复制信息
	 * @author shiy  2023/8/17 17:10
	*/
	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/sync-store-delivery-area")
	BaseResponse<List<FreightTemplateDeliveryAreaVO>> syncStoreDeliveryArea(@RequestBody @Valid FreightStoreSyncRequest freightStoreSyncRequest);

	/**
	 * @desc  指定物流
	 * @author shiy  2023/9/13 15:57
	*/
	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/specify-logistics")
	BaseResponse<List<FreightTemplateDeliveryAreaVO>> querySpecifyLogistics(@RequestBody @Valid FreightTemplateDeliveryAreaListRequest queryRequest);

	/**
	 * @desc  同城配送
	 * @author shiy  2023/9/13 15:57
	*/
	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/intra-city-logistics")
	BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryIntraCityLogistics(@RequestBody @Valid FreightTemplateDeliveryAreaListRequest queryRequest);

	/**
	 * @desc  快递到家(到付)
	 * @author shiy  2023/10/17 16:00
	*/
	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/express_arrived")
    BaseResponse<List<FreightTemplateDeliveryAreaVO>> queryExpressArrived(@RequestBody @Valid FreightTemplateDeliveryAreaListRequest queryRequest);
}


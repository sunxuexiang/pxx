package com.wanmi.sbc.goods.api.provider.freight;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaSaveListRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaSaveRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * <p>配送到家范围保存服务Provider</p>
 * @author zhaowei
 * @date 2021-03-25 16:57:57
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "FreightTemplateDeliveryAreaSaveProvider")
public interface FreightTemplateDeliveryAreaSaveProvider {

	/**
	 * 保存配送到家范围API
	 *
	 * @author zhaowei
	 * @param freightTemplateDeliveryAreaSaveRequest 配送到家范围新增参数结构 {@link }
	 * @return 保存的配送到家范围信息
	 */
	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/save")
	BaseResponse save(@RequestBody @Valid FreightTemplateDeliveryAreaSaveRequest freightTemplateDeliveryAreaSaveRequest);

	@GetMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/deleteRedisByStoreId")
	BaseResponse deleteRedisByStoreId(@RequestParam(value = "storeId")Long storeId, @RequestParam(value="destinationType") Integer destinationType);

	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/saveOpenFlag")
	BaseResponse saveOpenFlag(@RequestBody FreightTemplateDeliveryAreaSaveListRequest listRequest);

	/**
	 * 开仓初始化 免费店配、乡镇件、第三方物流
	 * storeid,companyInfoId,wareId
	 * @param freightTemplateDeliveryAreaSaveRequest
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/init-by-storeid-wareid")
	BaseResponse initByStoreIdAndWareId(@RequestBody @Valid FreightTemplateDeliveryAreaSaveRequest freightTemplateDeliveryAreaSaveRequest);

	@PostMapping("/goods/${application.goods.version}/freighttemplatedeliveryarea/init-by-boss-storeid-wareid")
	BaseResponse initByBossStoreIdAndWareId();
}


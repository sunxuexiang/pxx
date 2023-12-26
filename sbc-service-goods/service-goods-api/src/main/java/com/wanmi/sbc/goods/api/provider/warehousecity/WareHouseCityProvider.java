package com.wanmi.sbc.goods.api.provider.warehousecity;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.warehousecity.WareHouseCityAddRequest;
import com.wanmi.sbc.goods.api.request.warehousecity.WareHouseCityDelByIdListRequest;
import com.wanmi.sbc.goods.api.request.warehousecity.WareHouseCityDelByIdRequest;
import com.wanmi.sbc.goods.api.request.warehousecity.WareHouseCityModifyRequest;
import com.wanmi.sbc.goods.api.response.warehousecity.WareHouseCityAddResponse;
import com.wanmi.sbc.goods.api.response.warehousecity.WareHouseCityModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p> 仓库地区表保存服务Provider</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:28:33
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "WareHouseCityProvider")
public interface WareHouseCityProvider {

	/**
	 * 新增 仓库地区表API
	 *
	 * @author zhangwenchang
	 * @param wareHouseCityAddRequest  仓库地区表新增参数结构 {@link WareHouseCityAddRequest}
	 * @return 新增的 仓库地区表信息 {@link WareHouseCityAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/warehousecity/add")
    BaseResponse<WareHouseCityAddResponse> add(@RequestBody @Valid WareHouseCityAddRequest wareHouseCityAddRequest);

	/**
	 * 修改 仓库地区表API
	 *
	 * @author zhangwenchang
	 * @param wareHouseCityModifyRequest  仓库地区表修改参数结构 {@link WareHouseCityModifyRequest}
	 * @return 修改的 仓库地区表信息 {@link WareHouseCityModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/warehousecity/modify")
    BaseResponse<WareHouseCityModifyResponse> modify(@RequestBody @Valid WareHouseCityModifyRequest wareHouseCityModifyRequest);

	/**
	 * 单个删除 仓库地区表API
	 *
	 * @author zhangwenchang
	 * @param wareHouseCityDelByIdRequest 单个删除参数结构 {@link WareHouseCityDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/warehousecity/delete-by-id")
    BaseResponse deleteById(@RequestBody @Valid WareHouseCityDelByIdRequest wareHouseCityDelByIdRequest);

	/**
	 * 批量删除 仓库地区表API
	 *
	 * @author zhangwenchang
	 * @param wareHouseCityDelByIdListRequest 批量删除参数结构 {@link WareHouseCityDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/warehousecity/delete-by-id-list")
    BaseResponse deleteByIdList(@RequestBody @Valid WareHouseCityDelByIdListRequest wareHouseCityDelByIdListRequest);

}


package com.wanmi.sbc.goods.api.provider.warehouse;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.warehouse.*;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseAddResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>仓库表保存服务Provider</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "WareHouseProvider")
public interface WareHouseProvider {

	/**
	 * 新增默认仓 新增仓库表API
	 *
	 * @author huapeiliang
	 * @param wareHouseAddRequest 仓库表新增参数结构 {@link WareHouseAddRequest}
	 * @return 新增的仓库表信息 {@link WareHouseAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/warehouse/init")
    BaseResponse<WareHouseAddResponse> init(@RequestBody @Valid WareHouseAddRequest wareHouseAddRequest);

	/**
	 * 新增仓库表API
	 *
	 * @author zhangwenchang
	 * @param wareHouseAddRequest 仓库表新增参数结构 {@link WareHouseAddRequest}
	 * @return 新增的仓库表信息 {@link WareHouseAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/warehouse/add")
    BaseResponse<WareHouseAddResponse> add(@RequestBody @Valid WareHouseAddRequest wareHouseAddRequest);

	/**
	 * 修改仓库表API
	 *
	 * @author zhangwenchang
	 * @param wareHouseModifyRequest 仓库表修改参数结构 {@link WareHouseModifyRequest}
	 * @return 修改的仓库表信息 {@link WareHouseModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/warehouse/modify")
    BaseResponse<WareHouseModifyResponse> modify(@RequestBody @Valid WareHouseModifyRequest wareHouseModifyRequest);

	/**
	 * 修改默认仓
	 *
	 * @author huapeiliang
	 * @param wareHouseModifyDefaultFlagRequest 仓库表修改默认仓参数结构 {@link WareHouseModifyDefaultFlagRequest}
	 * @return 修改的默认仓库表信息 {@link WareHouseModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/warehouse/modify-defaultFlag")
    BaseResponse<WareHouseModifyResponse> modifyDefaultFlag(@RequestBody @Valid WareHouseModifyDefaultFlagRequest wareHouseModifyDefaultFlagRequest);

	/**
	 * 单个删除仓库表API
	 *
	 * @author zhangwenchang
	 * @param wareHouseDelByIdRequest 单个删除参数结构 {@link WareHouseDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/warehouse/delete-by-id")
    BaseResponse deleteById(@RequestBody @Valid WareHouseDelByIdRequest wareHouseDelByIdRequest);

	/**
	 * 批量删除仓库表API
	 *
	 * @author zhangwenchang
	 * @param wareHouseDelByIdListRequest 批量删除参数结构 {@link WareHouseDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/warehouse/delete-by-id-list")
    BaseResponse deleteByIdList(@RequestBody @Valid WareHouseDelByIdListRequest wareHouseDelByIdListRequest);

	/**
	 * 设为默认API
	 *
	 * @author zhangwenchang
	 * @param wareHouseModifyRequest 仓库表修改参数结构 {@link WareHouseModifyRequest}
	 * @return 修改的仓库表信息 {@link WareHouseModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/warehouse/set-default")
    BaseResponse<WareHouseModifyResponse> setDefault(@RequestBody @Valid WareHouseModifyRequest wareHouseModifyRequest);

	/**
	 * 批量删除仓库表API
	 *
	 * @author zhangwenchang
	 * @param wareHouseDelByIdListRequest 批量删除参数结构 {@link WareHouseDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/warehouse/delete-by-store-id")
	BaseResponse deleteByIdStoreId(@RequestBody @Valid WareHouseDelByIdListRequest wareHouseDelByIdListRequest);
}


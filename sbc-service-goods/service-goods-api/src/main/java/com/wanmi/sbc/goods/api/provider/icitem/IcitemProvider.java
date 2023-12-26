package com.wanmi.sbc.goods.api.provider.icitem;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.icitem.IcitemAddRequest;
import com.wanmi.sbc.goods.api.response.icitem.IcitemAddResponse;
import com.wanmi.sbc.goods.api.request.icitem.IcitemModifyRequest;
import com.wanmi.sbc.goods.api.response.icitem.IcitemModifyResponse;
import com.wanmi.sbc.goods.api.request.icitem.IcitemDelByIdRequest;
import com.wanmi.sbc.goods.api.request.icitem.IcitemDelByIdListRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>配送到家保存服务Provider</p>
 * @author lh
 * @date 2020-12-05 18:16:34
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "IcitemProvider")
public interface IcitemProvider {

	/**
	 * 新增配送到家API
	 *
	 * @author lh
	 * @param icitemAddRequest 配送到家新增参数结构 {@link IcitemAddRequest}
	 * @return 新增的配送到家信息 {@link IcitemAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/icitem/add")
	BaseResponse<IcitemAddResponse> add(@RequestBody @Valid IcitemAddRequest icitemAddRequest);

	/**
	 * 修改配送到家API
	 *
	 * @author lh
	 * @param icitemModifyRequest 配送到家修改参数结构 {@link IcitemModifyRequest}
	 * @return 修改的配送到家信息 {@link IcitemModifyResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/icitem/modify")
	BaseResponse<IcitemModifyResponse> modify(@RequestBody @Valid IcitemModifyRequest icitemModifyRequest);

	/**
	 * 单个删除配送到家API
	 *
	 * @author lh
	 * @param icitemDelByIdRequest 单个删除参数结构 {@link IcitemDelByIdRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/icitem/delete-by-id")
	BaseResponse deleteById(@RequestBody @Valid IcitemDelByIdRequest icitemDelByIdRequest);

	/**
	 * 批量删除配送到家API
	 *
	 * @author lh
	 * @param icitemDelByIdListRequest 批量删除参数结构 {@link IcitemDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/icitem/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid IcitemDelByIdListRequest icitemDelByIdListRequest);

}


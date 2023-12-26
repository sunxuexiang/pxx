package com.wanmi.sbc.goods.api.provider.soldoutgoods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.soldoutgoods.SoldOutGoodsPageRequest;
import com.wanmi.sbc.goods.api.response.soldoutgoods.SoldOutGoodsPageResponse;
import com.wanmi.sbc.goods.api.request.soldoutgoods.SoldOutGoodsListRequest;
import com.wanmi.sbc.goods.api.response.soldoutgoods.SoldOutGoodsListResponse;
import com.wanmi.sbc.goods.api.request.soldoutgoods.SoldOutGoodsByIdRequest;
import com.wanmi.sbc.goods.api.response.soldoutgoods.SoldOutGoodsByIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>类目品牌排序表查询服务Provider</p>
 * @author lvheng
 * @date 2021-04-10 15:09:50
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "SoldOutGoodsQueryProvider")
public interface SoldOutGoodsQueryProvider {


	/**
	 * 列表查询类目品牌排序表API
	 *
	 * @author lvheng
	 * @param soldOutGoodsListReq 列表请求参数和筛选对象 {@link SoldOutGoodsListRequest}
	 * @return 类目品牌排序表的列表信息 {@link SoldOutGoodsListResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/soldoutgoods/list")
	BaseResponse<SoldOutGoodsListResponse> list(@RequestBody @Valid SoldOutGoodsListRequest soldOutGoodsListReq);


}


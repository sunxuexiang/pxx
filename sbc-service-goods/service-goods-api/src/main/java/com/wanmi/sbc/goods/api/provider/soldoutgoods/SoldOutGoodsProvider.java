package com.wanmi.sbc.goods.api.provider.soldoutgoods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.soldoutgoods.SoldOutGoodsAddRequest;
import com.wanmi.sbc.goods.api.response.soldoutgoods.SoldOutGoodsAddResponse;
import com.wanmi.sbc.goods.api.request.soldoutgoods.SoldOutGoodsModifyRequest;
import com.wanmi.sbc.goods.api.response.soldoutgoods.SoldOutGoodsModifyResponse;
import com.wanmi.sbc.goods.api.request.soldoutgoods.SoldOutGoodsDelByIdRequest;
import com.wanmi.sbc.goods.api.request.soldoutgoods.SoldOutGoodsDelByIdListRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * <p>类目品牌排序表保存服务Provider</p>
 * @author lvheng
 * @date 2021-04-10 15:09:50
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "SoldOutGoodsProvider")
public interface SoldOutGoodsProvider {

	/**
	 * 新增类目品牌排序表API
	 *
	 * @author lvheng
	 * @param soldOutGoodsAddRequest 类目品牌排序表新增参数结构 {@link SoldOutGoodsAddRequest}
	 * @return 新增的类目品牌排序表信息 {@link SoldOutGoodsAddResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/soldoutgoods/add")
	BaseResponse batchAdd(@RequestBody @Valid SoldOutGoodsAddRequest soldOutGoodsAddRequest);


	/**
	 * 批量删除类目品牌排序表API
	 *
	 * @author lvheng
	 * @param soldOutGoodsDelByIdListRequest 批量删除参数结构 {@link SoldOutGoodsDelByIdListRequest}
	 * @return 删除结果 {@link BaseResponse}
	 */
	@PostMapping("/goods/${application.goods.version}/soldoutgoods/delete-by-id-list")
	BaseResponse deleteByIdList(@RequestBody @Valid SoldOutGoodsDelByIdListRequest soldOutGoodsDelByIdListRequest);

}


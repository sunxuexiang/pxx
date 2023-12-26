package com.wanmi.sbc.goods.api.provider.warehouse;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.warehouse.*;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseAreaIdByIdAndStoreIdResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseByIdResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHousePageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>仓库表查询服务Provider</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "WareHouseQueryProvider")
public interface WareHouseQueryProvider {

    /**
     * 分页查询仓库表API
     *
     * @param wareHousePageReq 分页请求参数和筛选对象 {@link WareHousePageRequest}
     * @return 仓库表分页列表信息 {@link WareHousePageResponse}
     * @author zhangwenchang
     */
    @PostMapping("/goods/${application.goods.version}/warehouse/page")
    BaseResponse<WareHousePageResponse> page(@RequestBody @Valid WareHousePageRequest wareHousePageReq);

    /**
     * 列表查询仓库表API
     *
     * @param wareHouseListReq 列表请求参数和筛选对象 {@link WareHouseListRequest}
     * @return 仓库表的列表信息 {@link WareHouseListResponse}
     * @author zhangwenchang
     */
    @PostMapping("/goods/${application.goods.version}/warehouse/list")
    BaseResponse<WareHouseListResponse> list(@RequestBody @Valid WareHouseListRequest wareHouseListReq);

    /**
     * 列表查询仓库表API
     *
     * @param wareHouseListReq 列表请求参数和筛选对象 {@link WareHouseListRequest}
     * @return 仓库表的列表信息 {@link WareHouseListResponse}
     * @author zhangwenchang
     */
    @PostMapping("/goods/${application.goods.version}/warehouse/list-by-storeId")
    BaseResponse<WareHouseListResponse> listByStoreId(@RequestBody @Valid WareHouseListByStoreIdRequest wareHouseListReq);

    /**
     * 单个查询仓库表API
     *
     * @param wareHouseByIdRequest 单个查询仓库表请求参数 {@link WareHouseByIdRequest}
     * @return 仓库表详情 {@link WareHouseByIdResponse}
     * @author zhangwenchang
     */
    @PostMapping("/goods/${application.goods.version}/warehouse/get-by-id")
    BaseResponse<WareHouseByIdResponse> getById(@RequestBody @Valid WareHouseByIdRequest wareHouseByIdRequest);


    /**
     * 单个查询仓库表API
     *
     * @param wareHouseByIdRequest 单个查询仓库表请求参数 {@link WareHouseByIdRequest}
     * @return 仓库表详情 {@link WareHouseByIdResponse}
     * @author zhangwenchang
     */
    @PostMapping("/goods/${application.goods.version}/warehouse/get-by-wareId")
    BaseResponse<WareHouseByIdResponse> getByWareId(@RequestBody @Valid WareHouseByIdRequest wareHouseByIdRequest);

    /**
     * 仓库覆盖区域
     *
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/warehouse/query-area-id-by-id-and-store-id")
    BaseResponse<WareHouseAreaIdByIdAndStoreIdResponse> queryAreaIdsByIdAndStoreId(@RequestBody @Valid WareHouseAreaIdByStoreIdRequest request);

    /**
     * 仓库覆盖区域
     *
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/warehouse/query-ware-house-by-store-id-and-province-id-and-city-id")
    BaseResponse<WareHouseByIdResponse> queryWareHouseByStoreIdAndProvinceIdAndCityId(@RequestBody @Valid
                                                                                              WareHouseQueryRequest
                                                                                              request);
}


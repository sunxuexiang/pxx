package com.wanmi.sbc.goods.provider.impl.warehouse;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.warehouse.*;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseAreaIdByIdAndStoreIdResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseByIdResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHousePageResponse;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import com.wanmi.sbc.goods.warehouse.service.WareHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>仓库表查询服务接口实现</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@RestController
@Validated
public class WareHouseQueryController implements WareHouseQueryProvider {
    @Autowired
    private WareHouseService wareHouseService;
    @Override
    public BaseResponse<WareHousePageResponse> page(@RequestBody @Valid WareHousePageRequest wareHousePageReq) {
        WareHouseQueryRequest queryReq = KsBeanUtil.convert(wareHousePageReq, WareHouseQueryRequest.class);
        Page<WareHouse> wareHousePage = wareHouseService.page(queryReq);
        Page<WareHouseVO> newPage = wareHousePage.map(entity -> wareHouseService.wrapperVo(entity));
        MicroServicePage<WareHouseVO> microPage = new MicroServicePage<>(newPage, wareHousePageReq.getPageable());
        WareHousePageResponse finalRes = new WareHousePageResponse(microPage);
        return BaseResponse.success(finalRes);
    }

    @Override
    public BaseResponse<WareHouseListResponse> list(@RequestBody @Valid WareHouseListRequest wareHouseListReq) {
        WareHouseQueryRequest queryReq = KsBeanUtil.convert(wareHouseListReq, WareHouseQueryRequest.class);
        List<WareHouse> wareHouseList = wareHouseService.list(queryReq);
        List<WareHouseVO> newList =
                wareHouseList.stream().map(entity -> wareHouseService.wrapperVo(entity)).collect(Collectors.toList());
        return BaseResponse.success( WareHouseListResponse.builder().wareHouseVOList(newList).build());
    }

    @Override
    public BaseResponse<WareHouseListResponse> listByStoreId(@Valid WareHouseListByStoreIdRequest wareHouseListReq) {
        List<WareHouse> byWareIdIn = wareHouseService.findByStoreIdIn(wareHouseListReq.getStoreIds());
        List<WareHouseVO> newList =
                byWareIdIn.stream().map(entity -> wareHouseService.wrapperVo(entity)).collect(Collectors.toList());
        return BaseResponse.success( WareHouseListResponse.builder().wareHouseVOList(newList).build());
    }

    @Override
    public BaseResponse<WareHouseByIdResponse> getById(@RequestBody @Valid WareHouseByIdRequest wareHouseByIdRequest) {
        WareHouse wareHouse =
                wareHouseService.getOne(wareHouseByIdRequest.getWareId(), wareHouseByIdRequest.getStoreId());
        WareHouseVO wareHouseVO = wareHouseService.wrapperVo(wareHouse);
        wareHouseVO.setSelectedAreas(wareHouseService.getSelectAreas(wareHouse.getStoreId(), wareHouse.getWareId(),
                wareHouse.getWareHouseType()));
        return BaseResponse.success(new WareHouseByIdResponse(wareHouseVO));
    }

    @Override
    public BaseResponse<WareHouseByIdResponse> getByWareId(@Valid WareHouseByIdRequest wareHouseByIdRequest) {
        WareHouse wareHouse =
                wareHouseService.getOneById(wareHouseByIdRequest.getWareId());
        WareHouseVO wareHouseVO = wareHouseService.wrapperVo(wareHouse);
        wareHouseVO.setSelectedAreas(wareHouseService.getSelectAreas(wareHouse.getStoreId(), wareHouse.getWareId(),
                wareHouse.getWareHouseType()));
        return BaseResponse.success(new WareHouseByIdResponse(wareHouseVO));
    }

    /**
     * 仓库覆盖区域
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse<WareHouseAreaIdByIdAndStoreIdResponse> queryAreaIdsByIdAndStoreId(@Valid WareHouseAreaIdByStoreIdRequest request) {
        return BaseResponse.success(
                WareHouseAreaIdByIdAndStoreIdResponse.builder()
                        .areaIds(wareHouseService.getSelectAreas(request.getStoreId(), request.getWareId()
                                ,request.getWareHouseType()))
                        .build()
        );
    }

    @Override
    public BaseResponse<WareHouseByIdResponse> queryWareHouseByStoreIdAndProvinceIdAndCityId(@Valid WareHouseQueryRequest request) {
        WareHouse wareHouse = wareHouseService.queryWareHouseByStoreIdAndProvinceIdAndCityId(request);
        WareHouseVO wareHouseVO = wareHouseService.wrapperVo(wareHouse);
        return BaseResponse.success(new WareHouseByIdResponse(wareHouseVO));
    }
}


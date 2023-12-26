package com.wanmi.sbc.goods.provider.impl.warehouse;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.PickUpFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.constant.WareHouseErrorCode;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseProvider;
import com.wanmi.sbc.goods.api.request.warehouse.*;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseAddResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseModifyResponse;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockService;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import com.wanmi.sbc.goods.warehouse.service.WareHouseService;
import com.wanmi.sbc.goods.warehousecity.service.WareHouseCityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>仓库表保存服务接口实现</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@RestController
@Validated
public class WareHouseController implements WareHouseProvider {
    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private WareHouseCityService wareHouseCityService;

    @Autowired
    private GoodsWareStockService goodsWareStockService;

    @Override
    public BaseResponse<WareHouseAddResponse> init(@RequestBody @Valid WareHouseAddRequest wareHouseAddRequest) {
        // 查询是否存在默认仓，存在抛出异常
        WareHouseQueryRequest wareHouseQueryRequest = new WareHouseQueryRequest();
        wareHouseQueryRequest.setStoreId(wareHouseAddRequest.getStoreId());
        wareHouseQueryRequest.setDelFlag(DeleteFlag.NO);
        wareHouseQueryRequest.setDefaultFlag(DefaultFlag.YES);
        List<WareHouse> list = wareHouseService.list(wareHouseQueryRequest);
        if (!ObjectUtils.isEmpty(list)){
            throw new SbcRuntimeException(WareHouseErrorCode.NOT_INIT_BY_DEFAULT_FLAG);
        }

        WareHouse wareHouse = KsBeanUtil.convert(wareHouseAddRequest, WareHouse.class);
        wareHouse.setCreateTime(LocalDateTime.now());
        wareHouse.setDefaultFlag(DefaultFlag.YES);
        wareHouse.setDestinationArea(StringUtils.join(wareHouseAddRequest.getDestinationArea(), ","));
        wareHouse.setDestinationAreaName(StringUtils.join(wareHouseAddRequest.getDestinationAreaName(), ","));
        WareHouse wareHouse1 = wareHouseService.add(wareHouse);
        //保存仓库关联省市
        wareHouseCityService.saveBatch(wareHouseAddRequest.getDestinationArea(), wareHouse1.getWareId());
        return BaseResponse.success(new WareHouseAddResponse(
                wareHouseService.wrapperVo(wareHouse1)));
    }

    @Override
    public BaseResponse<WareHouseAddResponse> add(@RequestBody @Valid WareHouseAddRequest wareHouseAddRequest) {
        WareHouseModifyRequest modifyRequest = KsBeanUtil.convert(wareHouseAddRequest, WareHouseModifyRequest.class);
        //校验仓库编号和仓库名称是否重复
        WareHouse wareHouse = wareHouseService.checkParam(modifyRequest);
        wareHouse.setCreateTime(LocalDateTime.now());
        wareHouse.setDefaultFlag(DefaultFlag.NO);
        wareHouse.setDelFlag(DeleteFlag.NO);
        if (null==wareHouseAddRequest.getLng()  ||  null== wareHouseAddRequest.getLat()){
            //暂时设置成默认，此精度维度
            wareHouse.setLat(Constants.MARKETING_FULLAMOUNT_MIN);
            wareHouse.setLng(Constants.MARKETING_FULLAMOUNT_MIN);
        }
        wareHouse.setType(DefaultFlag.YES);
        wareHouse.setCreatePerson(wareHouseAddRequest.getCreatePerson());
        wareHouse.setWareHouseType(WareHouseType.ONLINE);
        wareHouse.setPickUpFlag(PickUpFlag.NO);
        wareHouse.setDistance(wareHouseAddRequest.getDistance());
        wareHouse.setDestinationArea(StringUtils.join(wareHouseAddRequest.getDestinationArea(), ","));
        wareHouse.setDestinationAreaName(StringUtils.join(wareHouseAddRequest.getDestinationAreaName(), ","));
        WareHouse wareHouse1 = wareHouseService.add(wareHouse);
        //保存仓库关联省市
        wareHouseCityService.saveBatch(wareHouseAddRequest.getDestinationArea(), wareHouse1.getWareId());
        return BaseResponse.success(new WareHouseAddResponse(
                wareHouseService.wrapperVo(wareHouse1)));
    }

    @Override
    public BaseResponse<WareHouseModifyResponse> modify(@RequestBody @Valid WareHouseModifyRequest wareHouseModifyRequest) {
        //校验仓库编号和仓库名称是否重复
        WareHouse wareHouse = wareHouseService.checkParam(wareHouseModifyRequest);
        wareHouse.setDestinationArea(StringUtils.join(wareHouseModifyRequest.getDestinationArea(), ","));
        wareHouse.setDestinationAreaName(StringUtils.join(wareHouseModifyRequest.getDestinationAreaName(), ","));
        wareHouse = wareHouseService.modify(wareHouse);
        //保存仓库关联省市
//        wareHouseCityService.saveBatch(wareHouseModifyRequest.getDestinationArea(), wareHouse.getWareId());
        return BaseResponse.success(new WareHouseModifyResponse(
                wareHouseService.wrapperVo(wareHouse)));
    }

    @Override
    public BaseResponse<WareHouseModifyResponse> modifyDefaultFlag(@RequestBody @Valid WareHouseModifyDefaultFlagRequest wareHouseModifyDefaultFlagRequest) {
        return BaseResponse.success(new WareHouseModifyResponse(
                wareHouseService.wrapperVo(wareHouseService.modifyDefaultFlag(wareHouseModifyDefaultFlagRequest))));
    }


    @Override
    @LcnTransaction
    public BaseResponse deleteById(@RequestBody @Valid WareHouseDelByIdRequest wareHouseDelByIdRequest) {

        // 判断是否为默认仓，默认仓不给删除
        WareHouse wareHouse = wareHouseService.getOne(wareHouseDelByIdRequest.getWareId(),
                wareHouseDelByIdRequest.getStoreId());
        if (wareHouse.getDefaultFlag().equals(DefaultFlag.YES)) {
            throw new SbcRuntimeException(WareHouseErrorCode.NOT_DELETE_BY_DEFAULT_FLAG);
        }

        // 判断该仓库关联的商品是否存在库存，如存在库存不给删除
        Long countStockGoods = goodsWareStockService.countStockGoodsByWareId(wareHouseDelByIdRequest.getWareId(), BigDecimal.ZERO);
        if (Objects.nonNull(countStockGoods) && countStockGoods > 0) {
            throw new SbcRuntimeException(WareHouseErrorCode.NOT_DELETE_BY_HAVE_STOCK);
        }

        // 删除仓库表、仓库覆盖区域表、商品关联的仓库表
        wareHouse.setDelFlag(DeleteFlag.YES);
        wareHouse.setUpdateTime(LocalDateTime.now());
        wareHouseService.deleteById(wareHouse);
        wareHouseCityService.deleteByWareHouseId(wareHouse.getWareId());

        // 删除商品关联仓库表、商品关联仓库明细表
        goodsWareStockService.deleteByWareId(wareHouseDelByIdRequest.getWareId());

        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse deleteByIdList(@RequestBody @Valid WareHouseDelByIdListRequest wareHouseDelByIdListRequest) {
        List<WareHouse> wareHouseList = wareHouseDelByIdListRequest.getWareIdList().stream()
                .map(WareId -> {
                    WareHouse wareHouse = new WareHouse();
                    wareHouse.setWareId(WareId);
                    wareHouse.setDelFlag(DeleteFlag.YES);
                    return wareHouse;
                }).collect(Collectors.toList());
        wareHouseService.deleteByIdList(wareHouseList);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 设为默认API
     *
     * @param wareHouseModifyRequest 仓库表修改参数结构 {@link WareHouseModifyRequest}
     * @return 修改的仓库表信息 {@link WareHouseModifyResponse}
     * @author zhangwenchang
     */
    @Override
    public BaseResponse<WareHouseModifyResponse> setDefault(@Valid WareHouseModifyRequest wareHouseModifyRequest) {
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse deleteByIdStoreId(@RequestBody @Valid WareHouseDelByIdListRequest listRequest) {
        wareHouseService.deleteByIds(listRequest.getWareIdList());
        return BaseResponse.SUCCESSFUL();
    }
}


package com.wanmi.sbc.goods.provider.impl.goodsunit;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.EnableStatus;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodsunit.GoodsUnitSaveProvider;
import com.wanmi.sbc.goods.api.request.goodsunit.StoreGoodsUnitAddRequest;
import com.wanmi.sbc.goods.api.response.goodsunit.GoodsUnitAddResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsUnitVo;
import com.wanmi.sbc.goods.goodsunit.request.GoodsUnitQueryRequest;
import com.wanmi.sbc.goods.goodsunit.root.StoreGoodsUnit;
import com.wanmi.sbc.goods.goodsunit.service.GoodsUnitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <p> 新增商品单位</p>
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:22
 * @Description: TODO
 * @Version 1.0
 */
@RestController
@Validated
@Slf4j
public class GoodsUnitSaveController implements GoodsUnitSaveProvider {

    @Autowired
    private GoodsUnitService goodsUnitService ;

    @Override
    public BaseResponse<GoodsUnitAddResponse> add(@RequestBody @Valid StoreGoodsUnitAddRequest request) {
        request.setStatus(EnableStatus.ENABLE.toValue());
        request.setDelFlag(DeleteFlag.NO.toValue());
        List<StoreGoodsUnit> query = goodsUnitService.query(GoodsUnitQueryRequest.builder().unitCount(request.getUnit()).companyInfoId(request.getCompanyInfoId()).delFlag(0).build());
        if (CollectionUtils.isNotEmpty(query)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"单位已存在");
        }
        StoreGoodsUnit goodsUnit = new StoreGoodsUnit();
        KsBeanUtil.copyPropertiesThird(request, goodsUnit);
        GoodsUnitVo goodsUnitV = goodsUnitService.wrapperVo(goodsUnitService.add(goodsUnit));
        return BaseResponse.success(new GoodsUnitAddResponse(goodsUnitV));
    }




    @Override
    public BaseResponse<GoodsUnitAddResponse> updateUnit( @RequestBody @Valid StoreGoodsUnitAddRequest request) {
        StoreGoodsUnit goodsUnit =goodsUnitService.getById(request.getStoreGoodsUnitId());
        if (BooleanUtils.isTrue(request.getSupplierUpdate()) && Objects.equals(goodsUnit.getCompanyInfoId(),-1L)){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"商家不支持修改基本商品单位");
        }
        goodsUnit.setStoreGoodsUnitId(Long.valueOf(request.getStoreGoodsUnitId()));
        goodsUnit.setUpdateTime(LocalDateTime.now());
        goodsUnit.setUnit(request.getUnit());
        goodsUnit.setUpdatePerson(request.getUpdatePerson());
        List<StoreGoodsUnit> query = goodsUnitService.query(GoodsUnitQueryRequest.builder().unitCount(request.getUnit()).companyInfoId(request.getCompanyInfoId()).delFlag(0).build());
        if (CollectionUtils.isNotEmpty(query)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"单位已存在");
        }

        GoodsUnitVo goodsUnitV = goodsUnitService.wrapperVo(goodsUnitService.updateUnit(goodsUnit));
        return BaseResponse.success(new GoodsUnitAddResponse(goodsUnitV));
    }
    @Override
    public BaseResponse<GoodsUnitAddResponse> updateStatus( @RequestBody @Valid StoreGoodsUnitAddRequest request) {
        StoreGoodsUnit goodsUnit = goodsUnitService.getById(request.getStoreGoodsUnitId());
        goodsUnit.setStatus(request.getStatus());
        goodsUnit.setUpdateTime(LocalDateTime.now());
        GoodsUnitVo goodsUnitV = goodsUnitService.wrapperVo(goodsUnitService.updateUnit(goodsUnit));
        return BaseResponse.success(new GoodsUnitAddResponse(goodsUnitV));
    }

    @Override
    public BaseResponse<GoodsUnitAddResponse> deleteById(@RequestBody @Valid StoreGoodsUnitAddRequest request) {
        StoreGoodsUnit goodsUnit =goodsUnitService.getById(request.getStoreGoodsUnitId());
        if (BooleanUtils.isTrue(request.getSupplierUpdate()) && Objects.equals(goodsUnit.getCompanyInfoId(),-1L)){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"商家不支持删除基本商品单位");
        }
        goodsUnitService.deleteById(Long.parseLong(request.getStoreGoodsUnitId()));
        return BaseResponse.SUCCESSFUL();
    }

}

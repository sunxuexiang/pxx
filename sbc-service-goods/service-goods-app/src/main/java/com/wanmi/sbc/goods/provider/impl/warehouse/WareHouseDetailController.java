package com.wanmi.sbc.goods.provider.impl.warehouse;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseDetailProvider;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseDetailAddRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseDetailRequest;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseDetailResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseDetailPageResponse;
import com.wanmi.sbc.goods.bean.vo.WareHouseDetailVO;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouseDetail;
import com.wanmi.sbc.goods.warehouse.service.WareHouseDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>仓库表保存服务接口实现</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@RestController
@Validated
public class WareHouseDetailController implements WareHouseDetailProvider {

    @Autowired
    private WareHouseDetailService wareHouseDetailService;

    @Override
    public BaseResponse<WareHouseDetailPageResponse> list(WareHouseDetailRequest wreHouseDetailRequest) {
        List<WareHouseDetail> wareHouseList = wareHouseDetailService.list(wreHouseDetailRequest);
        List<WareHouseDetailVO> newList =
                wareHouseList.stream().map(entity -> wareHouseDetailService.wrapperVo(entity)).collect(Collectors.toList());
        return BaseResponse.success( WareHouseDetailPageResponse.builder().wareHouseDetailVOList(newList).build());
    }

    @Override
    public BaseResponse<WareHouseDetailResponse> add(WareHouseDetailAddRequest wareHouseDetailAddRequest) {
        //校验仓库编号和仓库名称是否重复
        WareHouseDetail wareHouseDetail = wareHouseDetailService.getByWareId(wareHouseDetailAddRequest.getWareId());
        if(null != wareHouseDetail){
            new SbcRuntimeException(CommonErrorCode.SPECIFIED, "该仓库已经存在，不允许在添加");
        }
        wareHouseDetail = new WareHouseDetail();
        wareHouseDetail.setCreateTime(LocalDateTime.now());
        wareHouseDetail.setDelFlag(DeleteFlag.NO);
        wareHouseDetail.setWareId(wareHouseDetailAddRequest.getWareId());
        wareHouseDetail.setWareName(wareHouseDetailAddRequest.getWareName());
        wareHouseDetail.setWarePlayerImg(wareHouseDetailAddRequest.getWarePlayerImg());
        wareHouseDetail.setCreatePerson(wareHouseDetailAddRequest.getCreatePerson());
        WareHouseDetail wareHouse1 = wareHouseDetailService.add(wareHouseDetail);
        return BaseResponse.success(new WareHouseDetailResponse(
                wareHouseDetailService.wrapperVo(wareHouse1)));
    }

    @Override
    public BaseResponse<WareHouseDetailResponse> modify(WareHouseDetailAddRequest wareHouseDetailAddRequest) {
        //校验仓库编号和仓库名称是否重复
        WareHouseDetail wareHouseDetail = wareHouseDetailService.getByWareId(wareHouseDetailAddRequest.getWareId());
        if(null == wareHouseDetail){
            new SbcRuntimeException(CommonErrorCode.SPECIFIED, "该仓库不存在，不允许在添加");
        }
        wareHouseDetail.setWarePlayerImg(wareHouseDetailAddRequest.getWarePlayerImg());
        wareHouseDetail.setUpdatePerson(wareHouseDetailAddRequest.getUpdatePerson());
        wareHouseDetail.setUpdateTime(LocalDateTime.now());
        return BaseResponse.success(new WareHouseDetailResponse(
                wareHouseDetailService.wrapperVo( wareHouseDetailService.modify(wareHouseDetail))));
    }

    @Override
    public BaseResponse deleteById(WareHouseDetailAddRequest wareHouseDetailAddRequest) {
        //校验仓库编号和仓库名称是否重复
        WareHouseDetail wareHouseDetail = wareHouseDetailService.getByWareId(wareHouseDetailAddRequest.getWareId());
        if(null == wareHouseDetail){
            new SbcRuntimeException(CommonErrorCode.SPECIFIED, "该仓库不存在，不允许删除");
        }
        wareHouseDetail.setDelFlag(wareHouseDetailAddRequest.getDelFlag());
        wareHouseDetail.setUpdatePerson(wareHouseDetailAddRequest.getUpdatePerson());
        wareHouseDetail.setUpdateTime(LocalDateTime.now());
        wareHouseDetailService.deleteById(wareHouseDetail);
        return BaseResponse.SUCCESSFUL();
    }
}


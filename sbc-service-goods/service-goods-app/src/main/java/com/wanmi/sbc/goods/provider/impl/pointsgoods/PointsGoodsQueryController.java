package com.wanmi.sbc.goods.provider.impl.pointsgoods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.pointsgoods.PointsGoodsQueryProvider;
import com.wanmi.sbc.goods.api.request.pointsgoods.*;
import com.wanmi.sbc.goods.api.response.pointsgoods.PointsGoodsByIdResponse;
import com.wanmi.sbc.goods.api.response.pointsgoods.PointsGoodsListResponse;
import com.wanmi.sbc.goods.api.response.pointsgoods.PointsGoodsPageResponse;
import com.wanmi.sbc.goods.bean.vo.PointsGoodsVO;
import com.wanmi.sbc.goods.pointsgoods.model.root.PointsGoods;
import com.wanmi.sbc.goods.pointsgoods.service.PointsGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>积分商品表查询服务接口实现</p>
 *
 * @author yang
 * @date 2019-05-07 15:01:41
 */
@RestController
@Validated
public class PointsGoodsQueryController implements PointsGoodsQueryProvider {
    @Autowired
    private PointsGoodsService pointsGoodsService;

    @Override
    public BaseResponse<PointsGoodsPageResponse> page(@RequestBody @Valid PointsGoodsPageRequest pointsGoodsPageReq) {
        PointsGoodsQueryRequest queryReq = new PointsGoodsQueryRequest();
        KsBeanUtil.copyPropertiesThird(pointsGoodsPageReq, queryReq);
        queryReq.setDelFlag(DeleteFlag.NO);
        Page<PointsGoods> pointsGoodsPage = pointsGoodsService.page(queryReq);
        Page<PointsGoodsVO> newPage = pointsGoodsPage.map(entity -> pointsGoodsService.wrapperVo(entity));
        MicroServicePage<PointsGoodsVO> microPage = new MicroServicePage<>(newPage, pointsGoodsPageReq.getPageable());
        PointsGoodsPageResponse finalRes = new PointsGoodsPageResponse(microPage);
        return BaseResponse.success(finalRes);
    }

    @Override
    public BaseResponse<PointsGoodsListResponse> list(@RequestBody @Valid PointsGoodsListRequest pointsGoodsListReq) {
        PointsGoodsQueryRequest queryReq = new PointsGoodsQueryRequest();
        KsBeanUtil.copyPropertiesThird(pointsGoodsListReq, queryReq);
        List<PointsGoods> pointsGoodsList = pointsGoodsService.list(queryReq);
        List<PointsGoodsVO> newList = pointsGoodsList.stream().map(entity -> pointsGoodsService.wrapperVo(entity)).collect(Collectors.toList());
        return BaseResponse.success(new PointsGoodsListResponse(newList));
    }

    @Override
    public BaseResponse<PointsGoodsByIdResponse> getById(@RequestBody @Valid PointsGoodsByIdRequest pointsGoodsByIdRequest) {
        PointsGoods pointsGoods = pointsGoodsService.getById(pointsGoodsByIdRequest.getPointsGoodsId());
        return BaseResponse.success(new PointsGoodsByIdResponse(pointsGoodsService.wrapperVo(pointsGoods)));
    }

    @Override
    public BaseResponse<PointsGoodsListResponse> queryOverdueList() {
        List<PointsGoods> pointsGoodsList = pointsGoodsService.queryOverdueList();
        List<PointsGoodsVO> pointsGoodsVOS = pointsGoodsList.stream().map(entity -> pointsGoodsService.wrapperVo(entity)).collect(Collectors.toList());
        return BaseResponse.success(new PointsGoodsListResponse(pointsGoodsVOS));
    }

    @Override
    public BaseResponse<PointsGoodsListResponse> getByStoreId(@RequestBody @Valid PointsGoodsByStoreIdRequest request) {
        List<PointsGoods> pointsGoodsList = pointsGoodsService.getByStoreId(request.getStoreId());
        List<PointsGoodsVO> pointsGoodsVOS = pointsGoodsList.stream().map(entity -> pointsGoodsService.wrapperVo(entity)).collect(Collectors.toList());
        return BaseResponse.success(new PointsGoodsListResponse(pointsGoodsVOS));
    }
}


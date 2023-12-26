package com.wanmi.sbc.returnorder.provider.impl.pointstrade;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.level.CustomerLevelQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreCustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.storelevel.StoreLevelQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.level.CustomerLevelByIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreCustomerRelaListByConditionRequest;
import com.wanmi.sbc.customer.api.request.storelevel.StoreLevelByIdRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.api.response.level.CustomerLevelByIdResponse;
import com.wanmi.sbc.customer.bean.vo.StoreCustomerRelaVO;
import com.wanmi.sbc.customer.bean.vo.StoreLevelVO;
import com.wanmi.sbc.returnorder.api.provider.pointstrade.PointsTradeQueryProvider;
import com.wanmi.sbc.returnorder.api.request.pointstrade.PointsTradeGetByIdRequest;
import com.wanmi.sbc.returnorder.api.request.pointstrade.PointsTradeListExportRequest;
import com.wanmi.sbc.returnorder.api.request.pointstrade.PointsTradePageCriteriaRequest;
import com.wanmi.sbc.returnorder.api.response.pointstrade.PointsTradeGetByIdResponse;
import com.wanmi.sbc.returnorder.api.response.pointstrade.PointsTradeListExportResponse;
import com.wanmi.sbc.returnorder.api.response.pointstrade.PointsTradePageCriteriaResponse;
import com.wanmi.sbc.returnorder.bean.vo.PointsTradeVO;
import com.wanmi.sbc.returnorder.pointstrade.request.PointsTradeQueryRequest;
import com.wanmi.sbc.returnorder.pointstrade.service.PointsTradeService;
import com.wanmi.sbc.returnorder.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName PointsTradeQueryController
 * @Description 积分订单接口实现类
 * @Author lvzhenwei
 * @Date 2019/5/10 13:47
 **/
@Validated
@RestController
public class PointsTradeQueryController implements PointsTradeQueryProvider {

    @Autowired
    private PointsTradeService pointsTradeService;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CustomerLevelQueryProvider customerLevelQueryProvider;

    @Autowired
    private StoreCustomerQueryProvider storeCustomerQueryProvider;

    @Autowired
    private StoreLevelQueryProvider storeLevelQueryProvider;

    /**
     * @return com.wanmi.sbc.common.base.BaseResponse<com.wanmi.sbc.returnorder.api.response.pointstrade.PointsTradeGetByIdResponse>
     * @Author lvzhenwei
     * @Description 通过id获取订单信息
     * @Date 14:47 2019/5/10
     * @Param [pointsTradeGetByIdRequest]
     **/
    @Override
    public BaseResponse<PointsTradeGetByIdResponse> getById(@RequestBody @Valid PointsTradeGetByIdRequest pointsTradeGetByIdRequest) {
        Trade pointsTrade = pointsTradeService.getById(pointsTradeGetByIdRequest.getTid());
        if (Objects.nonNull(pointsTrade) && Objects.nonNull(pointsTrade.getBuyer()) && StringUtils.isNotEmpty(pointsTrade.getBuyer()
                .getAccount())) {
            pointsTrade.getBuyer().setAccount(ReturnOrderService.getDexAccount(pointsTrade.getBuyer().getAccount()));
            // 若是自营，获取平台等级，否则获取店铺等级
            if (pointsTrade.getSupplier().getIsSelf()) {
                CustomerGetByIdResponse getByIdResponse = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(pointsTrade.getBuyer().getId())).getContext();
                CustomerLevelByIdResponse levelByIdResponse = customerLevelQueryProvider.getCustomerLevelById(CustomerLevelByIdRequest.builder()
                        .customerLevelId(getByIdResponse.getCustomerLevelId())
                        .build()).getContext();
                pointsTrade.getBuyer().setLevelId(getByIdResponse.getCustomerLevelId());
                pointsTrade.getBuyer().setLevelName(levelByIdResponse.getCustomerLevelName());
            } else {
                Long storeId = pointsTrade.getSupplier().getStoreId();
                StoreCustomerRelaListByConditionRequest listByConditionRequest = new StoreCustomerRelaListByConditionRequest();
                listByConditionRequest.setCustomerId(pointsTrade.getBuyer().getId());
                listByConditionRequest.setStoreId(storeId);
                List<StoreCustomerRelaVO> relaVOList = storeCustomerQueryProvider.listByCondition(listByConditionRequest).getContext().getRelaVOList();
                if (Objects.nonNull(relaVOList) && relaVOList.size() > 0) {
                    StoreCustomerRelaVO storeCustomerRelaVO = relaVOList.get(0);
                    StoreLevelVO storeLevelVO = storeLevelQueryProvider.getById(StoreLevelByIdRequest.builder()
                            .storeLevelId(storeCustomerRelaVO.getStoreLevelId())
                            .build()).getContext().getStoreLevelVO();
                    pointsTrade.getBuyer().setLevelId(storeCustomerRelaVO.getStoreLevelId());
                    pointsTrade.getBuyer().setLevelName(storeLevelVO.getLevelName());
                }
            }
            pointsTrade.setBuyer(pointsTrade.getBuyer());
        }
        BaseResponse<PointsTradeGetByIdResponse> baseResponse = BaseResponse.success(PointsTradeGetByIdResponse.builder().
                pointsTradeVo(KsBeanUtil.convert(pointsTrade, PointsTradeVO.class)).build());
        return baseResponse;
    }

    /**
     * @return com.wanmi.sbc.common.base.BaseResponse<com.wanmi.sbc.returnorder.api.response.pointstrade.PointsTradePageCriteriaResponse>
     * @Author lvzhenwei
     * @Description 根据查询条件分页查询订单信息
     * @Date 14:48 2019/5/10
     * @Param [pointsTradePageCriteriaRequest]
     **/
    @Override
    public BaseResponse<PointsTradePageCriteriaResponse> pageCriteria(@RequestBody @Valid PointsTradePageCriteriaRequest pointsTradePageCriteriaRequest) {
        PointsTradeQueryRequest pointsTradeQueryRequest = KsBeanUtil.convert(
                pointsTradePageCriteriaRequest.getPointsTradePageDTO(), PointsTradeQueryRequest.class);
        Criteria criteria = pointsTradeQueryRequest.getWhereCriteria();
        Page<Trade> page = pointsTradeService.page(criteria, pointsTradeQueryRequest);
        MicroServicePage<PointsTradeVO> pointsTradeVOS = KsBeanUtil.convertPage(page, PointsTradeVO.class);
        return BaseResponse.success(PointsTradePageCriteriaResponse.builder().pointsTradePage(pointsTradeVOS).build());
    }

    /**
     * @return com.wanmi.sbc.common.base.BaseResponse<com.wanmi.sbc.returnorder.api.response.pointstrade.PointsTradeListExportResponse>
     * @Author lvzhenwei
     * @Description 查询积分订单导出数据
     * @Date 15:29 2019/5/10
     * @Param [pointsTradeListExportRequest]
     **/
    @Override
    public BaseResponse<PointsTradeListExportResponse> listPointsTradeExport(@RequestBody PointsTradeListExportRequest pointsTradeListExportRequest) {
        PointsTradeQueryRequest pointsTradeQueryRequest = KsBeanUtil.convert(pointsTradeListExportRequest.getPointsTradeQueryDTO(),
                PointsTradeQueryRequest.class);
        List<Trade> pointsTradeList = pointsTradeService.listPointsTradeExport(pointsTradeQueryRequest);
        return BaseResponse.success(PointsTradeListExportResponse.builder().
                pointsTradeVOList(KsBeanUtil.convert(pointsTradeList, PointsTradeVO.class)).build());
    }

    /**
     * @return com.wanmi.sbc.common.base.BaseResponse
     * @Author lvzhenwei
     * @Description 积分订单自动确认收货
     * @Date 11:39 2019/5/28
     * @Param []
     **/
    @Override
    public BaseResponse pointsOrderAutoReceive() {
        pointsTradeService.pointsOrderAutoReceive();
        return BaseResponse.SUCCESSFUL();
    }

}

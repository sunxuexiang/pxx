package com.wanmi.sbc.customer.provider.impl.company;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.enums.MallIntoCompanyEnums;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformProvider;
import com.wanmi.sbc.customer.api.request.company.*;
import com.wanmi.sbc.customer.api.response.company.*;
import com.wanmi.sbc.customer.bean.enums.MallContractRelationType;
import com.wanmi.sbc.customer.bean.vo.CompanyMallContractRelationVO;
import com.wanmi.sbc.customer.company.model.root.CompanyMallBulkMarket;
import com.wanmi.sbc.customer.company.model.root.CompanyMallContractRelation;
import com.wanmi.sbc.customer.company.model.root.CompanyMallSupplierRecommend;
import com.wanmi.sbc.customer.company.request.CompanyMallBulkMarketRequest;
import com.wanmi.sbc.customer.company.request.CompanyMallContractRelationRequest;
import com.wanmi.sbc.customer.company.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>公司信息查询接口实现</p>
 * Created by of628-wenzhi on 2018-08-18-下午4:41.
 */
@RestController
@Validated
@Slf4j
public class CompanyMallIntoPlatformController implements CompanyIntoPlatformProvider {

    @Autowired
    private CompanyMallBulkMarketService companyMallBulkMarketService;


    @Autowired
    private CompanyMallSupplierTabService companyMallSupplierTabService;

    @Autowired
    private CompanyMallSupplierRecommendService companyMallSupplierRecommendService;

    @Autowired
    private CompanyMallReturnGoodsAddressService companyMallReturnGoodsAddressService;

    @Autowired
    private CompanyMallContractRelationService companyMallContractRelationService;

    @Override
    public BaseResponse<CompanyMallBulkMarketSaveResponse> addMarket(CompanyMallBulkMarketAddRequest request) {
        return BaseResponse.success(KsBeanUtil.copyPropertiesThird(companyMallBulkMarketService.addMarket(request), CompanyMallBulkMarketSaveResponse.class));
    }

    @Override
    public BaseResponse<CompanyMallBulkMarketSaveResponse> editMarket(CompanyMallBulkMarketAddRequest request) {
        return BaseResponse.success(KsBeanUtil.copyPropertiesThird(companyMallBulkMarketService.editMarket(request), CompanyMallBulkMarketSaveResponse.class));
    }

    @Override
    public BaseResponse<CompanyMallSupplierRecommendResponse> addSupplierRecommend(CompanyMallSupplierRecommendAddRequest request) {
        return BaseResponse.success(KsBeanUtil.copyPropertiesThird(companyMallSupplierRecommendService.add(request), CompanyMallSupplierRecommendResponse.class));
    }

    @Override
    public BaseResponse<CompanyMallSupplierRecommendResponse> editSupplierRecommend(CompanyMallSupplierRecommendAddRequest request) {
        if (null != request.getMarketId() && null != request.getAssignSort() && request.getId() != null) {
            checkAssignSort(request.getId(), request.getMarketId(), request.getAssignSort());
        }
        final CompanyMallSupplierRecommendResponse context = KsBeanUtil.copyPropertiesThird(companyMallSupplierRecommendService.edit(request), CompanyMallSupplierRecommendResponse.class);
        return BaseResponse.success(context);
    }

    private void checkAssignSort(Long id, Long marketId, Integer assignSort) {
        final CompanyMallSupplierRecommend companyMallSupplierRecommend = companyMallSupplierRecommendService.getById(id);
        final CompanyMallContractRelationRequest request = new CompanyMallContractRelationRequest();
        request.setRelationValue(marketId.toString());
        request.setRelationType(MallContractRelationType.MARKET.getValue());
        request.setPageNum(0);
        request.setPageSize(Integer.MAX_VALUE);
        final Page<CompanyMallContractRelation> page = companyMallContractRelationService.page(request);
        List<Long> storeIds = page.getContent().stream().map(CompanyMallContractRelation::getStoreId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(companyMallSupplierRecommendService.findByStoreIdNotAndStoreIdInAndDelFlagAndAssignSort(companyMallSupplierRecommend.getStoreId(), storeIds, assignSort))) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "当前排序已经存在");
        }
    }

    @Override
    public BaseResponse<CompanyMallSupplierTabResponse> addSupplierTab(CompanyMallSupplierTabAddRequest request) {
        return BaseResponse.success(KsBeanUtil.copyPropertiesThird(companyMallSupplierTabService.add(request), CompanyMallSupplierTabResponse.class));

    }

    @Override
    public BaseResponse<CompanyMallSupplierTabResponse> editSupplierTab(CompanyMallSupplierTabAddRequest request) {
        return BaseResponse.success(KsBeanUtil.copyPropertiesThird(companyMallSupplierTabService.edit(request), CompanyMallSupplierTabResponse.class));
    }

    @Override
    public BaseResponse<CompanyMallReturnGoodsAddressResponse> saveReturnGoodsAddress(CompanyMallReturnGoodsAddressAddRequest request) {
        return BaseResponse.success(KsBeanUtil.copyPropertiesThird(companyMallReturnGoodsAddressService.add(request), CompanyMallReturnGoodsAddressResponse.class));
    }

    @Override
    public BaseResponse<CompanyMallContactRelationBatchSaveResponse> batchContactRelation(CompanyMallContactRelationBatchSaveRequest request) {
        List<CompanyMallContractRelation> companyMallContractRelations = companyMallContractRelationService.batchAdd(request);
        CompanyMallContactRelationBatchSaveResponse response = new CompanyMallContactRelationBatchSaveResponse();
        response.setContactRelationList(KsBeanUtil.convertList(companyMallContractRelations, CompanyMallContractRelationVO.class));
        return BaseResponse.success(response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Integer> batchEditSort(CompanyMallBatchSortEditRequest request) {
        Integer r = request.getSorts().size();
        final Integer type = request.getType();
        if (MallIntoCompanyEnums.SortType.MARKET.getValue().equals(type)) {
            companyMallBulkMarketService.batchSort(request.getSorts());
        } else if (MallIntoCompanyEnums.SortType.TAB.getValue().equals(type)) {
            companyMallSupplierTabService.batchSort(request.getSorts());

        } else if (MallIntoCompanyEnums.SortType.RECOMMEND.getValue().equals(type)) {
            companyMallSupplierRecommendService.batchSort(request.getSorts());
        } else {
            r = 0;
        }
        return BaseResponse.success(r);
    }

    @Override
    public BaseResponse<Integer> eidtSort(CompanyMallSortEditRequest request) {
        if (MallIntoCompanyEnums.SortType.MARKET.getValue().equals(request.getType())) {
            companyMallBulkMarketService.sort(request.getId(), request.getSort());
        } else {
            return BaseResponse.success(0);
        }
        return BaseResponse.success(1);
    }

    @Override
    public BaseResponse<Integer> refreshSearchName(CompanyMallMarketRefreshNameRequest request) {
        final CompanyMallBulkMarketRequest marketRequest = new CompanyMallBulkMarketRequest();
        marketRequest.setMarketIds(request.getMarketIds());
        marketRequest.setDeleteFlag(DeleteFlag.NO);
        final List<CompanyMallBulkMarket> companyMallBulkMarkets = companyMallBulkMarketService.lisAll(marketRequest);
        if (CollectionUtils.isEmpty(companyMallBulkMarkets)) return BaseResponse.success(0);
        companyMallBulkMarkets.forEach(o -> companyMallBulkMarketService.refreshSearchName(o));
        return BaseResponse.success(companyMallBulkMarkets.size());
    }

    @Override
    public BaseResponse<Boolean> batchSortCompanyMallRelation(CompanyMallContactRelationBatchSortRequest request) {
        return BaseResponse.success(companyMallContractRelationService.batchSort(request.getContactRelationList()));
    }

    @Override
    public BaseResponse<List<CompanyMallSupplierRecommendResponse>> batchAddSupplierRecommend(CompanyMallSupplierRecommendBatchAddRequest request) {
        return BaseResponse.success(KsBeanUtil.convertList(companyMallSupplierRecommendService.batchAdd(request), CompanyMallSupplierRecommendResponse.class));
    }

    @Override
    public BaseResponse<Boolean> editSort(CompanyMallSupplierRecommendSortRequest request) {
        return BaseResponse.success(companyMallSupplierRecommendService.sort(request.getId(), request.getSort()));
    }
}

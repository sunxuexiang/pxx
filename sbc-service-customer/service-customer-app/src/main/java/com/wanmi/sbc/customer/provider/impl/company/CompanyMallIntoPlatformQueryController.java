package com.wanmi.sbc.customer.provider.impl.company;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.request.company.*;
import com.wanmi.sbc.customer.api.response.company.*;
import com.wanmi.sbc.customer.bean.enums.MallContractRelationType;
import com.wanmi.sbc.customer.bean.vo.*;
import com.wanmi.sbc.customer.company.model.root.CompanyMallBulkMarket;
import com.wanmi.sbc.customer.company.model.root.CompanyMallContractRelation;
import com.wanmi.sbc.customer.company.model.root.CompanyMallSupplierRecommend;
import com.wanmi.sbc.customer.company.model.root.CompanyMallSupplierTab;
import com.wanmi.sbc.customer.company.request.CompanyMallBulkMarketRequest;
import com.wanmi.sbc.customer.company.request.CompanyMallContractRelationRequest;
import com.wanmi.sbc.customer.company.request.CompanyMallSupplierRecommendRequest;
import com.wanmi.sbc.customer.company.request.CompanyMallSupplierTabRequest;
import com.wanmi.sbc.customer.company.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>公司信息查询接口实现</p>
 * Created by of628-wenzhi on 2018-08-18-下午4:41.
 */
@RestController
@Validated
public class CompanyMallIntoPlatformQueryController implements CompanyIntoPlatformQueryProvider {

    @Autowired
    private CompanyMallBulkMarketService companyMallBulkMarketService;

    @Autowired
    private CompanyMallSupplierTabService companyMallSupplierTabService;

    @Autowired
    private CompanyMallSupplierRecommendService companyMallSupplierRecommendService;

    @Autowired
    private CompanyMallContractRelationService companyMallContractRelationService;

    @Autowired
    private CompanyMallReturnGoodsAddressService companyMallReturnGoodsAddressService;


    @Override
    public BaseResponse<CompanyMallBulkMarketPageResponse> pageMarket(CompanyMallBulkMarketPageRequest request) {
        CompanyMallBulkMarketRequest targetRequest = new CompanyMallBulkMarketRequest();
        KsBeanUtil.copyPropertiesThird(request, targetRequest);
        Page<CompanyMallBulkMarket> companyInfoPage = companyMallBulkMarketService.page(targetRequest);
        List<CompanyMallBulkMarketVO> voList = KsBeanUtil.convertList(companyInfoPage.getContent(), CompanyMallBulkMarketVO.class);
        CompanyMallBulkMarketPageResponse response = CompanyMallBulkMarketPageResponse.builder().page(new MicroServicePage<>(voList, request.getPageable(), companyInfoPage.getTotalElements())).build();
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<List<CompanyMallReturnGoodsAddressVO>> listReturnGoodsAddress(CompanyMallReturnGoodsAddressRequest request) {
        request.setDeleteFlag(DeleteFlag.NO);
        return BaseResponse.success(companyMallReturnGoodsAddressService.list(KsBeanUtil.convert(request, com.wanmi.sbc.customer.company.request.CompanyMallReturnGoodsAddressRequest.class)));
    }

    @Override
    public BaseResponse<CompanyMallBulkMarketResponse> getByIdForMarket(CompanyMallBulkMarketQueryRequest request) {
        CompanyMallBulkMarketResponse response = new CompanyMallBulkMarketResponse();
        KsBeanUtil.copyPropertiesThird(companyMallBulkMarketService.getById(request.getMarketId()), response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<CompanyMallSupplierTabPageResponse> pageSupplierTab(CompanyMallSupplierTabPageRequest request) {
        CompanyMallSupplierTabRequest targetRequest = new CompanyMallSupplierTabRequest();
        KsBeanUtil.copyPropertiesThird(request, targetRequest);
        Page<CompanyMallSupplierTab> companyInfoPage = companyMallSupplierTabService.page(targetRequest);
        List<CompanyMallSupplierTabVO> voList = KsBeanUtil.convertList(companyInfoPage.getContent(), CompanyMallSupplierTabVO.class);
        voList.forEach(CompanyMallSupplierTabVO::wrap);
        CompanyMallSupplierTabPageResponse response = CompanyMallSupplierTabPageResponse.builder().page(new MicroServicePage<>(voList, request.getPageable(), companyInfoPage.getTotalElements())).build();
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<List<CompanyMallSupplierTabVO>> listSupplierTab(CompanyMallSupplierTabListQueryRequest request) {
        CompanyMallSupplierTabRequest targetRequest = new CompanyMallSupplierTabRequest();
        KsBeanUtil.copyPropertiesThird(request, targetRequest);
        List<CompanyMallSupplierTab> companyMallSupplierTabs = companyMallSupplierTabService.list(targetRequest);
        List<CompanyMallSupplierTabVO> voList = KsBeanUtil.convertList(companyMallSupplierTabs, CompanyMallSupplierTabVO.class);
        voList.forEach(CompanyMallSupplierTabVO::wrap);
        return BaseResponse.success(voList);
    }

    @Override
    public BaseResponse<CompanyMallSupplierTabResponse> getByIdSupplierTab(CompanyMallSupplierTabQueryRequest request) {
        CompanyMallSupplierTabResponse response = getCompanyMallSupplierTabResponseById(request.getId());
        return BaseResponse.success(response);
    }

    private CompanyMallSupplierTabResponse getCompanyMallSupplierTabResponseById(Long id) {
        CompanyMallSupplierTabResponse response = new CompanyMallSupplierTabResponse();
        KsBeanUtil.copyPropertiesThird(companyMallSupplierTabService.getById(id), response);
        response.wrap();
        return response;
    }

    @Override
    public BaseResponse<CompanyMallSupplierRecommendPageResponse> pageSupplierRecommend(CompanyMallSupplierRecommendPageRequest request) {
        CompanyMallSupplierRecommendRequest targetRequest = new CompanyMallSupplierRecommendRequest();
        KsBeanUtil.copyPropertiesThird(request, targetRequest);
        Page<CompanyMallSupplierRecommend> companyInfoPage = companyMallSupplierRecommendService.page(targetRequest);
        List<CompanyMallSupplierRecommendVO> voList = KsBeanUtil.convertList(companyInfoPage.getContent(), CompanyMallSupplierRecommendVO.class);
        CompanyMallSupplierRecommendPageResponse response = CompanyMallSupplierRecommendPageResponse.builder().page(new MicroServicePage<>(voList, request.getPageable(), companyInfoPage.getTotalElements())).build();
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<CompanyMallSupplierRecommendResponse> getByIdSupplierRecommend(CompanyMallSupplierRecommendQueryRequest request) {
        CompanyMallSupplierRecommendResponse response = new CompanyMallSupplierRecommendResponse();
        KsBeanUtil.copyPropertiesThird(companyMallSupplierTabService.getById(request.getId()), response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<CompanyMallContractRelationPageResponse> pageContractRelation(CompanyMallContractRelationPageRequest request) {
        CompanyMallContractRelationRequest targetRequest = new CompanyMallContractRelationRequest();
        KsBeanUtil.copyPropertiesThird(request, targetRequest);
        Page<CompanyMallContractRelation> companyInfoPage = companyMallContractRelationService.page(targetRequest);
        List<CompanyMallContractRelationVO> voList = KsBeanUtil.convertList(companyInfoPage.getContent(), CompanyMallContractRelationVO.class);
        CompanyMallContractRelationPageResponse response = CompanyMallContractRelationPageResponse.builder().page(new MicroServicePage<>(voList, request.getPageable(), companyInfoPage.getTotalElements())).build();
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<List<CompanyMallBulkMarketVO>> listMarketByStoreId(Long storeId) {
        List<CompanyMallBulkMarketVO> empList = new ArrayList<>();
        if (null == storeId)
            return BaseResponse.success(empList);
        // 查找签约关系
        final List<CompanyMallContractRelation> relationList = getCompanyMallContractRelations(storeId,MallContractRelationType.MARKET.getValue());
        if (CollectionUtils.isEmpty(relationList))
            return BaseResponse.success(empList);
        List<Long> marketIds = relationList.stream().map(o -> Long.valueOf(o.getRelationValue())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(marketIds))
            return BaseResponse.success(empList);
        // 查找市场信息
        final CompanyMallBulkMarketRequest manageBulkMarketRequest = new CompanyMallBulkMarketRequest();
        manageBulkMarketRequest.setMarketIds(marketIds);
        manageBulkMarketRequest.setDeleteFlag(DeleteFlag.NO);
        final List<CompanyMallBulkMarket> companyMallBulkMarkets = companyMallBulkMarketService.lisAll(manageBulkMarketRequest);
        return BaseResponse.success(KsBeanUtil.convertList(companyMallBulkMarkets, CompanyMallBulkMarketVO.class));
    }

    private List<CompanyMallContractRelation> getCompanyMallContractRelations(Long storeId,Integer relationType) {
        final CompanyMallContractRelationRequest relationRequest = new CompanyMallContractRelationRequest();
        relationRequest.setStoreId(storeId);
        relationRequest.setDeleteFlag(DeleteFlag.NO);
        relationRequest.setRelationType(relationType);
        final List<CompanyMallContractRelation> relationList = companyMallContractRelationService.list(relationRequest);
        return relationList;
    }

    @Override
    public BaseResponse<CompanyMallBulkMarketVO> getMarketByStoreId(Long storeId) {
        BaseResponse<List<CompanyMallBulkMarketVO>> r = listMarketByStoreId(storeId);
        if (CollectionUtils.isNotEmpty(r.getContext()))
            return BaseResponse.success(r.getContext().get(0));
        else
            return BaseResponse.success(new CompanyMallBulkMarketVO());
    }

    @Override
    public BaseResponse<CompanyMallSupplierTabResponse> getSupplierByStoreId(Long storeId) {
        final List<CompanyMallContractRelation> relationList = getCompanyMallContractRelations(storeId,MallContractRelationType.TAB.getValue());
        if(CollectionUtils.isEmpty(relationList)){
            return BaseResponse.success(new CompanyMallSupplierTabResponse());
        }
        CompanyMallSupplierTabResponse response = getCompanyMallSupplierTabResponseById(Long.valueOf(relationList.get(0).getRelationValue()));
        return BaseResponse.success(response);
    }
}

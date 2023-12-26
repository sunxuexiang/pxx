package com.wanmi.sbc.imMessage;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.companymall.CompanyMallBulkMarketController;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoForDistributionRecordRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyListRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallBulkMarketQueryRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallContractRelationPageRequest;
import com.wanmi.sbc.customer.api.request.store.ListStoreByIdsRequest;
import com.wanmi.sbc.customer.api.request.store.ListStoreRequest;
import com.wanmi.sbc.customer.api.request.store.StorePageRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoListResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallContractRelationPageResponse;
import com.wanmi.sbc.customer.api.response.store.ListStoreByIdsResponse;
import com.wanmi.sbc.customer.api.response.store.ListStoreResponse;
import com.wanmi.sbc.customer.api.response.store.StoreListForDistributionResponse;
import com.wanmi.sbc.customer.api.response.store.StorePageResponse;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.CompanyMallContractRelationVO;
import com.wanmi.sbc.customer.bean.vo.StoreSimpleInfo;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceStoreRelationProvider;
import com.wanmi.sbc.setting.api.provider.onlineservice.OnlineServiceQueryProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceStoreRelationGetRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceStoreRelationRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.RelationStoreListRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceStoreRelationChildResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceStoreRelationDetailResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceStoreRelationResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author zzg
 *
 * @createDate 2023-10-05 10:49
 * @Description: 在线客服店铺关系
 * @Version 1.0
 */
@Api(tags = "ImOnLineServiceController", description = "在线客服店铺关系api")
@RestController
@RequestMapping("/customerServiceRelation")
@Slf4j
@Validated
@Data
public class CustomerServiceStoreRelationController {

    @Autowired
    private CustomerServiceStoreRelationProvider customerServiceStoreRelationProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private OnlineServiceQueryProvider onlineServiceQueryProvider;

    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;

    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;


    /**
     * <p>添加在线客服店铺关系</p>
     */
    @ApiOperation(value = "添加在线客服店铺关系")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public BaseResponse addRelation (@RequestBody CustomerServiceStoreRelationRequest request) {
        try {
            return customerServiceStoreRelationProvider.addRelation(request);
        }
        catch (SbcRuntimeException e) {
            return BaseResponse.error(e.getErrorCode());
        }
    }

    @ApiOperation(value = "修改在线客服店铺关系")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public BaseResponse updateRelation (@RequestBody CustomerServiceStoreRelationRequest request) {
        return customerServiceStoreRelationProvider.editRelation(request);
    }

    @ApiOperation(value = "删除在线客服店铺关系")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public BaseResponse deleteRelation (@RequestBody CustomerServiceStoreRelationRequest request) {
        return customerServiceStoreRelationProvider.deleteRelation(request);
    }

    @ApiOperation(value = "查询在线客户店铺关系列表")
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    public BaseResponse getList (@RequestBody CustomerServiceStoreRelationGetRequest request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        BaseResponse<List<CustomerServiceStoreRelationResponse>> allRelationStoreResponse = customerServiceStoreRelationProvider.getAllRelationStores();
        if (ObjectUtils.isEmpty(allRelationStoreResponse.getContext())) {
            return emptyRelationListResponse();
        }
        stopWatch.stop();
        log.info("客服关系列表：查询所有创建的店铺关系列表耗时 {}", stopWatch.getTotalTimeSeconds());

        stopWatch.start();
        List<Long> queryCompanyIds = null;
        CompanyListRequest companyListRequest = CompanyListRequest.builder().build();
        boolean isQueryCompany = false;
        if (!StringUtils.isEmpty(request.getSupplierName())) {
            isQueryCompany = true;
            companyListRequest.setCompanyInfoIds(allRelationStoreResponse.getContext().stream().map(CustomerServiceStoreRelationResponse::getCompanyInfoId).collect(Collectors.toList()));
            companyListRequest.setSupplierName(request.getSupplierName());
        }
        if (!StringUtils.isEmpty(request.getCompanyCode())) {
            isQueryCompany = true;
            companyListRequest.setCompanyInfoIds(allRelationStoreResponse.getContext().stream().map(CustomerServiceStoreRelationResponse::getCompanyInfoId).collect(Collectors.toList()));
            companyListRequest.setCompanyCode(request.getCompanyCode());
        }
        if (isQueryCompany) {
            BaseResponse<CompanyInfoListResponse> companyInfoListResponseBaseResponse = companyInfoQueryProvider.listCompanyInfo(companyListRequest);
            if (ObjectUtils.isEmpty(companyInfoListResponseBaseResponse.getContext()) || ObjectUtils.isEmpty(companyInfoListResponseBaseResponse.getContext().getCompanyInfoVOList())) {
                return emptyRelationListResponse();
            }
            queryCompanyIds = companyInfoListResponseBaseResponse.getContext().getCompanyInfoVOList().stream().map(CompanyInfoVO::getCompanyInfoId).collect(Collectors.toList());
        }
        request.setCompanyInfoIds(queryCompanyIds);
        stopWatch.stop();
        log.info("客服关系列表：查询公司条件列表耗时 {}", stopWatch.getTotalTimeSeconds());

        stopWatch.start();
        ListStoreRequest listStoreRequest = ListStoreRequest.builder().build();
        boolean isStoreQuery = false;
        if (!ObjectUtils.isEmpty(request.getStoreName())) {
            isStoreQuery = true;
            listStoreRequest.setStoreIds(allRelationStoreResponse.getContext().stream().map(CustomerServiceStoreRelationResponse::getStoreId).collect(Collectors.toList()));
            listStoreRequest.setStoreName(request.getStoreName());
        }
        if (!ObjectUtils.isEmpty(request.getCompanyType())) {
            isStoreQuery = true;
            listStoreRequest.setStoreIds(allRelationStoreResponse.getContext().stream().map(CustomerServiceStoreRelationResponse::getStoreId).collect(Collectors.toList()));
            listStoreRequest.setCompanyType(CompanyType.fromValue(request.getCompanyType()));
        }
        List<Long> storeQueryIds = null;
        if (isStoreQuery) {
            BaseResponse<ListStoreResponse> listStoreResponseBaseResponse = storeQueryProvider.listStore(listStoreRequest);
            if (ObjectUtils.isEmpty(listStoreResponseBaseResponse.getContext()) || ObjectUtils.isEmpty(listStoreResponseBaseResponse.getContext().getStoreVOList())) {
                return emptyRelationListResponse();
            }
            storeQueryIds = listStoreResponseBaseResponse.getContext().getStoreVOList().stream().map(StoreVO::getStoreId).collect(Collectors.toList());
        }
        stopWatch.stop();
        log.info("客服关系列表：查询店铺条件列表耗时 {}", stopWatch.getTotalTimeSeconds());

        stopWatch.start();
        request.setStoreIds(storeQueryIds);
        BaseResponse<MicroServicePage<CustomerServiceStoreRelationResponse>> baseResponse = customerServiceStoreRelationProvider.getList(request);
        if (ObjectUtils.isEmpty(baseResponse.getContext()) || ObjectUtils.isEmpty(baseResponse.getContext().getContent())) {
            return baseResponse;
        }
        List<CustomerServiceStoreRelationResponse> list = baseResponse.getContext().getContent();
        Set<Long> storeIds = new HashSet<>();
        list.forEach(store -> {
            storeIds.add(store.getStoreId());
            if (!ObjectUtils.isEmpty(store.getChildList())) {
                storeIds.addAll(store.getChildList().stream().map(CustomerServiceStoreRelationChildResponse::getStoreId).collect(Collectors.toList()));
            }
        });


        BaseResponse<ListStoreByIdsResponse> storeResponse = storeQueryProvider.listByIds(ListStoreByIdsRequest.builder().storeIds(new ArrayList<>(storeIds)).build());

        List<CompanyMallContractRelationVO> mallList;
        List<Long> companyInfoIds = new ArrayList<>();
        storeResponse.getContext().getStoreVOList().forEach(storeVO -> {
            if (storeVO.getCompanyInfo() != null) {
                companyInfoIds.add(storeVO.getCompanyInfo().getCompanyInfoId());
            }
        });
        if (!ObjectUtils.isEmpty(companyInfoIds)) {
            CompanyMallContractRelationPageRequest mallRequest = new CompanyMallContractRelationPageRequest();
            mallRequest.setCompanyInfoIds(companyInfoIds);
            mallRequest.setPageSize(companyInfoIds.size());
            BaseResponse<CompanyMallContractRelationPageResponse> mallResponse = companyIntoPlatformQueryProvider.pageContractRelation(mallRequest);
            mallList = mallResponse.getContext() != null ? mallResponse.getContext().getPage().getContent() : null;
        }
        else {
            mallList = null;
        }

        list.forEach(relation -> {
            for (StoreVO store : storeResponse.getContext().getStoreVOList()) {
                if (relation.getStoreId().equals(store.getStoreId())) {
                    relation.setSupplierName(store.getSupplierName());
                    relation.setStoreName(store.getStoreName());
                    relation.setCompanyType(store.getCompanyType());
                    relation.setContactMobile(store.getContactMobile());
                    if (store.getCompanyInfo() != null) {
                        relation.setCompanyCode(store.getCompanyInfo().getCompanyCodeNew());
                    }
                    if (ObjectUtils.isEmpty(mallList)) {
                        break;
                    }
                    for (CompanyMallContractRelationVO relationVO : mallList) {
                        if (store.getStoreId().equals(relationVO.getStoreId())) {
                            relation.setMarketName(relationVO.getRelationName());
                            break;
                        }
                    }
                    break;
                }
            }

            if (ObjectUtils.isEmpty(relation.getChildList())) {
                return;
            }
            relation.getChildList().forEach(child -> {
                for (StoreVO store : storeResponse.getContext().getStoreVOList()) {
                    if (child.getStoreId().equals(store.getStoreId())) {
                        if (store.getCompanyInfo() != null) {
                            child.setCompanyCode(store.getCompanyInfo().getCompanyCodeNew());
                        }
                        child.setStoreName(store.getStoreName());
                        child.setSupplierName(store.getSupplierName());
                        child.setCompanyType(store.getCompanyType());
                        child.setContactMobile(store.getContactMobile());
                        if (ObjectUtils.isEmpty(mallList)) {
                            break;
                        }
                        for (CompanyMallContractRelationVO relationVO : mallList) {
                            if (store.getStoreId().equals(relationVO.getStoreId())) {
                                child.setMarketName(relationVO.getRelationName());
                                break;
                            }
                        }
                        break;
                    }
                }
            });
        });
        stopWatch.stop();
        log.info("客服关系列表：列表总耗时 {}", stopWatch.getTotalTimeSeconds());
        return baseResponse;
    }

    private static BaseResponse<MicroServicePage<CustomerServiceStoreRelationResponse>> emptyRelationListResponse() {
        BaseResponse<MicroServicePage<CustomerServiceStoreRelationResponse>> baseResponse = new BaseResponse<>();
        MicroServicePage<CustomerServiceStoreRelationResponse> microServicePage = new MicroServicePage<>();
        microServicePage.setContent(new ArrayList<>());
        baseResponse.setContext(microServicePage);
        return baseResponse;
    }

    @ApiOperation(value = "查询父级店铺列表")
    @RequestMapping(value = "/getParentStoreList", method = RequestMethod.POST)
    public BaseResponse getParentStoreList (@RequestBody RelationStoreListRequest request) {
        BaseResponse<List<Long>> relationResponse = onlineServiceQueryProvider.getHaveCustomerServiceStoreIds();
        return getStoreInfoList(request, relationResponse);
    }

    @ApiOperation(value = "查询子级店铺列表")
    @RequestMapping(value = "/getChildStoreList", method = RequestMethod.POST)
    public BaseResponse getChildStoreList (@RequestBody RelationStoreListRequest request) {
        BaseResponse<List<Long>> relationResponse = customerServiceStoreRelationProvider.getChildStoreIds(request.getStoreId());
        return getStoreInfoList(request, relationResponse);
    }

    @ApiOperation(value = "查询全部店铺列表")
    @RequestMapping(value = "/getAllStoreList", method = RequestMethod.POST)
    public BaseResponse getAllStoreList (@RequestBody RelationStoreListRequest request) {
        List<Long> excludeStoreIdList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(request.getStoreId())) {
            excludeStoreIdList.add(request.getStoreId());
        }
        return storeListResponse(request, null, excludeStoreIdList);
    }

    private BaseResponse getStoreInfoList(RelationStoreListRequest request, BaseResponse<List<Long>> relationResponse) {
        if (ObjectUtils.isEmpty(relationResponse.getContext())) {
            return emptyStoreListResponse();
        }
        List<Long> storeIdList = relationResponse.getContext();
        if (!StringUtils.isEmpty(request.getCompanyCode())) {
            CompanyInfoForDistributionRecordRequest subRequest = new CompanyInfoForDistributionRecordRequest();
            subRequest.setCompanyCode(request.getCompanyCode());
            BaseResponse<StoreListForDistributionResponse> storeListResponse = companyInfoQueryProvider.queryByCompanyCode(subRequest);
            if (ObjectUtils.isEmpty(storeListResponse.getContext()) || ObjectUtils.isEmpty(storeListResponse.getContext().getStoreSimpleInfos())) {
                return emptyStoreListResponse();
            }
            List<Long> companyCodeList = storeListResponse.getContext().getStoreSimpleInfos().stream().map(StoreSimpleInfo::getStoreId).collect(Collectors.toList());
            storeIdList = (List<Long>) CollectionUtils.intersection(storeIdList, companyCodeList);
        }


        return storeListResponse(request, storeIdList, null);
    }

    private BaseResponse<MicroServicePage<CustomerServiceStoreRelationResponse>> storeListResponse(RelationStoreListRequest request, List<Long> storeIdList, List<Long> excludeStoreIdList) {
        StorePageRequest storePageRequest = KsBeanUtil.convert(request, StorePageRequest.class);
        storePageRequest.setStoreIds(storeIdList);
        BaseResponse<StorePageResponse> storeResponse = storeQueryProvider.page(storePageRequest);
        MicroServicePage<CustomerServiceStoreRelationResponse> responsePage = new MicroServicePage<>();
        KsBeanUtil.copyProperties(storeResponse.getContext().getStoreVOPage(), responsePage);


        List<CustomerServiceStoreRelationResponse> storeList = new ArrayList<>();
        responsePage.setContent(storeList);
        if (!ObjectUtils.isEmpty(storeResponse.getContext()) && !ObjectUtils.isEmpty(storeResponse.getContext().getStoreVOPage())
                && !ObjectUtils.isEmpty(storeResponse.getContext().getStoreVOPage().getContent())) {

            List<CompanyMallContractRelationVO> mallList;
            List<Long> companyInfoIds = new ArrayList<>();
            storeResponse.getContext().getStoreVOPage().getContent().forEach(storeVO -> {
                if (storeVO.getCompanyInfo() != null) {
                    companyInfoIds.add(storeVO.getCompanyInfo().getCompanyInfoId());
                }
            });
            if (!ObjectUtils.isEmpty(companyInfoIds)) {
                CompanyMallContractRelationPageRequest mallRequest = new CompanyMallContractRelationPageRequest();
                mallRequest.setCompanyInfoIds(companyInfoIds);
                mallRequest.setPageSize(companyInfoIds.size());
                BaseResponse<CompanyMallContractRelationPageResponse> mallResponse = companyIntoPlatformQueryProvider.pageContractRelation(mallRequest);
                mallList = mallResponse.getContext() != null ? mallResponse.getContext().getPage().getContent() : null;
            }
            else {
                mallList = null;
            }

            storeResponse.getContext().getStoreVOPage().getContent().forEach(storeVO -> {
                if (!ObjectUtils.isEmpty(excludeStoreIdList) && excludeStoreIdList.contains(storeVO.getStoreId())) {
                    return;
                }
                CustomerServiceStoreRelationResponse item = KsBeanUtil.convert(storeVO, CustomerServiceStoreRelationResponse.class);
                storeList.add(item);
                item.setCompanyInfoId(storeVO.getCompanyInfo().getCompanyInfoId());
                if (storeVO.getCompanyInfo() != null) {
                    item.setCompanyCode(storeVO.getCompanyInfo().getCompanyCodeNew());
                }
                item.setContactMobile(storeVO.getContactMobile());

                if (ObjectUtils.isEmpty(mallList)) {
                    return;
                }
                for (CompanyMallContractRelationVO relationVO : mallList) {
                    if (storeVO.getStoreId().equals(relationVO.getStoreId())) {
                        item.setMarketName(relationVO.getRelationName());
                        break;
                    }
                }
            });
        }
        return BaseResponse.success(responsePage);
    }

    private static BaseResponse<StorePageResponse> emptyStoreListResponse() {
        StorePageResponse storePageResponse = new StorePageResponse();
        MicroServicePage<StoreVO> storeVOPage = new MicroServicePage<>();
        storeVOPage.setTotal(0);
        storeVOPage.setContent(new ArrayList<>());
        return BaseResponse.success(storePageResponse);
    }
}

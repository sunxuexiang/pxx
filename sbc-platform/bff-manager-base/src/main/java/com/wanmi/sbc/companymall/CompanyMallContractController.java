package com.wanmi.sbc.companymall;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.*;
import com.wanmi.sbc.customer.api.request.store.StoreQueryRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyMallBulkMarketPageResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallContactRelationBatchSaveResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallContractRelationTabStoreVO;
import com.wanmi.sbc.customer.api.response.company.CompanyMallSupplierTabPageResponse;
import com.wanmi.sbc.customer.api.response.store.StoreSimpleResponse;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.enums.MallContractRelationType;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.bean.vo.CompanyMallBulkMarketVO;
import com.wanmi.sbc.customer.bean.vo.CompanyMallContractRelationVO;
import com.wanmi.sbc.customer.bean.vo.CompanyMallSupplierTabVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 签约信息(品牌，)
 * Created by sunkun on 2017/11/2.
 */
@Api(tags = "CompanyMallContractController", description = "签约信息 API")
@RestController
@RequestMapping("/contract")
@Validated
public class CompanyMallContractController {

    @Autowired
    private CompanyIntoPlatformProvider companyIntoPlatformProvider;

    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;

    @Autowired
    CommonUtil commonUtil;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "保存商家签约的tab和商城")
    @RequestMapping(value = "/company-mall/into-platform-relation/save", method = RequestMethod.POST)
    public BaseResponse<CompanyMallContactRelationBatchSaveResponse> saveCompanyMallRelation(@RequestBody CompanyMallContactRelationBatchSaveRequest request) {
        List<CompanyMallContractRelationVO> contactRelationList = companyIntoPlatformProvider.batchContactRelation(request).getContext().getContactRelationList();
        CompanyMallContactRelationBatchSaveResponse response = new CompanyMallContactRelationBatchSaveResponse();
        response.setContactRelationList(contactRelationList);
        operateLogMQUtil.convertAndSend("签约信息", "保存商家签约的tab和商城", "操作成功");
        return BaseResponse.success(response);
    }

    @ApiOperation(value = "分页查找商家签约的tab和商城")
    @RequestMapping(value = "/company-mall/into-platform-relation/page", method = RequestMethod.POST)
    public BaseResponse<Page<CompanyMallContractRelationVO>> pageCompanyMallRelation(@RequestBody CompanyMallContractRelationPageRequest request) {
        request.putSort("sort", SortType.ASC.toValue());
        MicroServicePage<CompanyMallContractRelationVO> page = companyIntoPlatformQueryProvider.pageContractRelation(request).getContext().getPage();
        List<CompanyMallContractRelationVO> list = KsBeanUtil.convertList(page.getContent(), CompanyMallContractRelationVO.class);
        Map<String, String> marketNameMap = wrapMarketNameMap();
        Map<String, String> tabNameMap = wrapTabNameMap();
        wrapName(list,marketNameMap,tabNameMap);
        // 封装替换名称
        return BaseResponse.success(new PageImpl<>(list, request.getPageable(), page.getTotalElements()));
    }

    @ApiOperation(value = "修改签约排序")
    @RequestMapping(value = "/company-mall/into-platform-relation/sort-batch", method = RequestMethod.POST)
    public BaseResponse<Boolean> batchSortCompanyMallRelation(@RequestBody CompanyMallContactRelationBatchSortRequest request) {
        operateLogMQUtil.convertAndSend("签约信息", "修改签约排序", "修改签约排序");
        return companyIntoPlatformProvider.batchSortCompanyMallRelation(request);
    }


    @ApiOperation(value = "签约商城的商家")
    @RequestMapping(value = "/company-mall/into-platform-relation/list-store-by-tab", method = RequestMethod.POST)
    public BaseResponse<List<CompanyMallContractRelationTabStoreVO>> listStoresByTab(@RequestBody CompanyMallContactRelationTabStoresRequest request) {
        final CompanyMallContractRelationPageRequest relationPageRequest = new CompanyMallContractRelationPageRequest();
        relationPageRequest.setPageSize(Integer.MAX_VALUE);
        relationPageRequest.setPageNum(0);
        relationPageRequest.setRelationType(MallContractRelationType.TAB.getValue());
        relationPageRequest.setRelationValue(request.getTabId().toString());
        final BaseResponse<Page<CompanyMallContractRelationVO>> pageBaseResponse = pageCompanyMallRelation(relationPageRequest);
        if (CollectionUtils.isEmpty(pageBaseResponse.getContext().getContent())) {
            return BaseResponse.success(new ArrayList<>());
        }
        return wrapRelationStores(pageBaseResponse);
    }

    @ApiOperation(value = "签约市场的商家")
    @RequestMapping(value = "/company-mall/into-platform-relation/list-store-by-market-id", method = RequestMethod.POST)
    public BaseResponse<List<CompanyMallContractRelationTabStoreVO>> listStoresByMarketId(@RequestBody CompanyMallContactRelationMarketStoresRequest request) {
        final CompanyMallContractRelationPageRequest relationPageRequest = new CompanyMallContractRelationPageRequest();
        relationPageRequest.setPageSize(Integer.MAX_VALUE);
        relationPageRequest.setPageNum(0);
        relationPageRequest.setRelationType(MallContractRelationType.MARKET.getValue());
        relationPageRequest.setRelationValue(request.getMarketId().toString());
        final BaseResponse<Page<CompanyMallContractRelationVO>> pageBaseResponse = pageCompanyMallRelation(relationPageRequest);
        if (CollectionUtils.isEmpty(pageBaseResponse.getContext().getContent())) {
            return BaseResponse.success(new ArrayList<>());
        }
        return wrapRelationStores(pageBaseResponse);
    }

    private BaseResponse<List<CompanyMallContractRelationTabStoreVO>> wrapRelationStores(BaseResponse<Page<CompanyMallContractRelationVO>> pageBaseResponse) {
        final List<Long> storeIds = pageBaseResponse.getContext().getContent().stream().map(CompanyMallContractRelationVO::getStoreId).collect(Collectors.toList());
//        final ListStoreByIdsRequest storeByIdsRequest = new ListStoreByIdsRequest();
//        storeByIdsRequest.setStoreIds(storeIds);
//        final Map<Long, StoreVO> storeMap = storeQueryProvider.listByIds(storeByIdsRequest).getContext()
//                .getStoreVOList().stream().collect(Collectors.toMap(StoreVO::getStoreId, o -> o));
        final StoreQueryRequest storeQueryRequest = new StoreQueryRequest();
        storeQueryRequest.setStoreIds(storeIds);
        final BaseResponse<List<StoreSimpleResponse>> listBaseResponse = storeQueryProvider.listSimple(storeQueryRequest);
        final Map<Long, StoreSimpleResponse> storeMap = listBaseResponse.getContext().stream().collect(Collectors.toMap(StoreSimpleResponse::getStoreId, Function.identity(), (o, n) -> o));
        return BaseResponse.success(pageBaseResponse.getContext().getContent().stream().map(o -> {
            final CompanyMallContractRelationTabStoreVO vo = KsBeanUtil.convert(o, CompanyMallContractRelationTabStoreVO.class);
            final StoreSimpleResponse storeVO = storeMap.get(o.getStoreId());
            if (storeVO == null || !Objects.equals(storeVO.getStoreState(), StoreState.OPENING)
                    && !Objects.equals(storeVO.getAuditState(), CheckState.CHECKED)) return null;
            vo.setStoreName(storeVO.getStoreName());
            vo.setSupplierName(storeVO.getSupplierName());
            vo.setAssignSort(storeVO.getAssignSort());
            return vo;
        }).filter(u -> u != null).collect(Collectors.toList()));
    }

    private void wrapName(List<CompanyMallContractRelationVO> list,
                          Map<String, String> marketNameMap,
                          Map<String, String> tabNameMap) {
        if (CollectionUtils.isEmpty(list)) return;
        list.forEach(o -> {
            if (MallContractRelationType.TAB.getValue().equals(o.getRelationType())) {
                o.setRelationName(tabNameMap.get(o.getRelationValue()));
            } else if (MallContractRelationType.MARKET.getValue().equals(o.getRelationType())) {
                o.setRelationName(marketNameMap.get(o.getRelationValue()));
            }
        });
    }

    private Map<String, String> wrapTabNameMap() {
        final CompanyMallSupplierTabPageRequest pageRequestV1 = new CompanyMallSupplierTabPageRequest();
        pageRequestV1.setPageNum(0);
        pageRequestV1.setPageSize(Integer.MAX_VALUE);
        pageRequestV1.setDeleteFlag(DeleteFlag.NO);
        final CompanyMallSupplierTabPageResponse contextV1 = companyIntoPlatformQueryProvider.pageSupplierTab(pageRequestV1).getContext();
        if (null == contextV1 || CollectionUtils.isEmpty(contextV1.getPage().getContent())) {
            return new HashMap<>();
        }
        return contextV1.getPage().getContent().stream()
                .collect(Collectors.toMap(o -> o.getId().toString(), CompanyMallSupplierTabVO::getTabName));
    }

    private Map<String, String> wrapMarketNameMap() {
        final CompanyMallBulkMarketPageRequest pageRequest = new CompanyMallBulkMarketPageRequest();
        pageRequest.setPageNum(0);
        pageRequest.setPageSize(Integer.MAX_VALUE);
        pageRequest.setDeleteFlag(DeleteFlag.NO);
        final CompanyMallBulkMarketPageResponse context = companyIntoPlatformQueryProvider.pageMarket(pageRequest).getContext();
        if (null == context || CollectionUtils.isEmpty(context.getPage().getContent())) {
            return new HashMap<>();
        }
        return context.getPage().getContent().stream()
                .collect(Collectors.toMap(o -> o.getMarketId().toString(), CompanyMallBulkMarketVO::getMarketName));
    }


}

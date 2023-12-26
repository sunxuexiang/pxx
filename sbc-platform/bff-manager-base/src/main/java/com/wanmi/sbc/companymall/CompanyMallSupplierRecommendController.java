package com.wanmi.sbc.companymall;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.*;
import com.wanmi.sbc.customer.api.request.store.ListNoDeleteStoreByIdsRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyMallContractRelationPageResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallSupplierRecommendResponse;
import com.wanmi.sbc.customer.api.response.store.ListNoDeleteStoreByIdsResponse;
import com.wanmi.sbc.customer.bean.enums.MallContractRelationType;
import com.wanmi.sbc.customer.bean.vo.CompanyMallContractRelationVO;
import com.wanmi.sbc.customer.bean.vo.CompanyMallSupplierRecommendVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-14 15:30
 **/
@RestController
@RequestMapping("/company/into-mall")
@Api(tags = "CompanyMallSupplierRecommendController", description = "S2B 平台端-商家管理API-商家推荐")
public class CompanyMallSupplierRecommendController {

    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;

    @Autowired
    private CompanyIntoPlatformProvider companyIntoPlatformProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "查询商家推荐列表")
    @RequestMapping(value = "/supplier-recommend/page", method = RequestMethod.POST)
    public BaseResponse<Page<CompanyMallSupplierRecommendResponse>> page(@RequestBody CompanyMallSupplierRecommendPageRequest request) {
        request.setDeleteFlag(DeleteFlag.NO);
        if (null != request.getAssignSort() && request.getAssignSort().equals(1)) {
            request.putSort("assignSort", SortType.ASC.toValue());
        } else {
            request.putSort("sort", SortType.ASC.toValue());
        }
        // 通过市场过滤
        final Long marketId = request.getMarketId();
        if (null != marketId) {
            request.setStoreIds(wrapMarketIdForStoreIds(marketId,request.getStoreIds()));
        }
        List<CompanyMallSupplierRecommendResponse> list = new ArrayList<>();
        Page<CompanyMallSupplierRecommendVO> page = companyIntoPlatformQueryProvider.pageSupplierRecommend(request).getContext()
                .getPage();
        if (CollectionUtils.isNotEmpty(page.getContent())) {
            final List<Long> storeIds = page.getContent().stream().filter(u -> u.getStoreId() != null)
                    .map(CompanyMallSupplierRecommendVO::getStoreId).collect(Collectors.toList());
            final ListNoDeleteStoreByIdsRequest storeByIdsRequest = new ListNoDeleteStoreByIdsRequest();
            storeByIdsRequest.setStoreIds(storeIds);
            final BaseResponse<ListNoDeleteStoreByIdsResponse> storeResponse = storeQueryProvider.listNoDeleteStoreByIds(storeByIdsRequest);
            final Map<Long, StoreVO> storeMap = storeResponse.getContext().getStoreVOList().stream().collect(Collectors.toMap(u -> u.getStoreId(), Function.identity()));
            list = KsBeanUtil.convertList(page.getContent(), CompanyMallSupplierRecommendResponse.class);
            list.forEach(o -> {
                final StoreVO storeVO = storeMap.get(o.getStoreId());
                if (null == storeVO) return;
                o.setCompanyInfoName(storeVO.getSupplierName());
                o.setStoreName(storeVO.getStoreName());
            });
        }
        return BaseResponse.success(new PageImpl<>(list, request.getPageable(), page.getTotalElements()));
    }

    private List<Long> wrapMarketIdForStoreIds(Long marketId, List<Long> storeIds) {
        final CompanyMallContractRelationPageRequest relationPageRequest = new CompanyMallContractRelationPageRequest();
        relationPageRequest.setPageNum(0);
        relationPageRequest.setPageSize(Integer.MAX_VALUE);
        relationPageRequest.setRelationType(MallContractRelationType.MARKET.getValue());
        relationPageRequest.setRelationValue(marketId.toString());
        final BaseResponse<CompanyMallContractRelationPageResponse> relationPageResponseBaseResponse = companyIntoPlatformQueryProvider.pageContractRelation(relationPageRequest);
        final List<CompanyMallContractRelationVO> content = relationPageResponseBaseResponse.getContext().getPage().getContent();
        if (CollectionUtils.isEmpty(content)) {
            return Lists.newArrayList(-1L);
        }else {
            final List<Long> collect = content.stream().map(CompanyMallContractRelationVO::getStoreId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(storeIds)){
                collect.retainAll(storeIds);
            }
            if (CollectionUtils.isEmpty(collect)){
                return Lists.newArrayList(-1L);
            }
            return collect;
        }
    }

    @ApiOperation(value = "添加商家推荐信息")
    @RequestMapping(value = "/supplier-recommend/add", method = RequestMethod.PUT)
    public BaseResponse<CompanyMallSupplierRecommendResponse> add(@RequestBody CompanyMallSupplierRecommendAddRequest request) {
        request.setOperator(commonUtil.getOperator().getName());
        operateLogMQUtil.convertAndSend("S2B 平台端-商家管理API-商家推荐", "添加商家推荐信息", "添加商家推荐信息");
        return companyIntoPlatformProvider.addSupplierRecommend(request);
    }


    @ApiOperation(value = "编辑商家推荐排序")
    @RequestMapping(value = "/supplier-recommend/sort-edit", method = RequestMethod.PUT)
    public BaseResponse<Boolean> editSort(@RequestBody CompanyMallSupplierRecommendSortRequest request) {
        operateLogMQUtil.convertAndSend("S2B 平台端-商家管理API-商家推荐", "编辑商家推荐排序", "编辑商家推荐排序");
        return companyIntoPlatformProvider.editSort(request);
    }





    @ApiOperation(value = "修改商家推荐信息")
    @RequestMapping(value = "/supplier-recommend/edit", method = RequestMethod.PUT)
    public BaseResponse<CompanyMallSupplierRecommendResponse> update(@RequestBody CompanyMallSupplierRecommendAddRequest request) {
        request.setOperator(commonUtil.getOperator().getName());
        operateLogMQUtil.convertAndSend("S2B 平台端-商家管理API-商家推荐", "修改商家推荐信息", "修改商家推荐信息");
        return companyIntoPlatformProvider.editSupplierRecommend(request);
    }

    @ApiOperation(value = "批量添加商家推荐信息")
    @RequestMapping(value = "/supplier-recommend/batch-add", method = RequestMethod.PUT)
    public BaseResponse<List<CompanyMallSupplierRecommendResponse>> batchAdd(@RequestBody CompanyMallSupplierRecommendBatchAddRequest request) {
        request.setOperator(commonUtil.getOperator().getName());
        operateLogMQUtil.convertAndSend("S2B 平台端-商家管理API-商家推荐", "批量添加商家推荐信息", "批量添加商家推荐信息");
        return companyIntoPlatformProvider.batchAddSupplierRecommend(request);
    }

    @ApiOperation(value = "获取商家推荐信息")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "商家推荐id", required = true)
    @RequestMapping(value = "/supplier-recommend/get/{id}", method = RequestMethod.GET)
    public BaseResponse<CompanyMallSupplierRecommendResponse> getById(@PathVariable Long id) {
        if (Objects.isNull(id)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        CompanyMallSupplierRecommendResponse response = new CompanyMallSupplierRecommendResponse();
        CompanyMallSupplierRecommendQueryRequest queryRequest = new CompanyMallSupplierRecommendQueryRequest();
        queryRequest.setId(id);
        CompanyMallSupplierRecommendVO companyInfo = companyIntoPlatformQueryProvider.getByIdSupplierRecommend(queryRequest).getContext();
        KsBeanUtil.copyPropertiesThird(companyInfo, response);
        return BaseResponse.success(response);
    }
}

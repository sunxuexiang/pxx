package com.wanmi.sbc.companymall;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyMallSupplierTabAddRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallSupplierTabPageRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallSupplierTabQueryRequest;
import com.wanmi.sbc.customer.api.request.store.ListStoreByIdsRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyMallSupplierTabDetailResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallSupplierTabResponse;
import com.wanmi.sbc.customer.api.response.store.ListStoreByIdsResponse;
import com.wanmi.sbc.customer.bean.vo.CompanyMallSupplierTabVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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
@Api(tags = "CompanyMallSupplierTabController", description = "S2B 平台端-商家管理API-商家商城分类")
public class CompanyMallSupplierTabController {

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

    @ApiOperation(value = "查询商家商城分类列表")
    @RequestMapping(value = "/supplier-tab/page", method = RequestMethod.POST)
    public BaseResponse<Page<CompanyMallSupplierTabResponse>> page(@RequestBody CompanyMallSupplierTabPageRequest request) {
        request.setDeleteFlag(DeleteFlag.NO);
        request.putSort("sort", SortType.ASC.toValue());
        Page<CompanyMallSupplierTabVO> page = companyIntoPlatformQueryProvider.pageSupplierTab(request).getContext()
                .getPage();
        List<CompanyMallSupplierTabResponse> list = KsBeanUtil.convertList(page.getContent(), CompanyMallSupplierTabResponse.class);
        return BaseResponse.success(new PageImpl<>(list, request.getPageable(), page.getTotalElements()));
    }

    @ApiOperation(value = "添加商家商城分类信息")
    @RequestMapping(value = "/supplier-tab/add", method = RequestMethod.PUT)
    public BaseResponse<CompanyMallSupplierTabResponse> add(@RequestBody CompanyMallSupplierTabAddRequest request) {
        request.setOperator(commonUtil.getOperator().getName());
        operateLogMQUtil.convertAndSend("S2B 平台端-商家管理API-商家商城分类", "添加商家商城分类信息", "添加商家商城分类信息");
        return companyIntoPlatformProvider.addSupplierTab(request);
    }

    @ApiOperation(value = "修改商家商城分类信息")
    @RequestMapping(value = "/supplier-tab/edit", method = RequestMethod.PUT)
    public BaseResponse<CompanyMallSupplierTabResponse> update(@RequestBody CompanyMallSupplierTabAddRequest request) {
        request.setOperator(commonUtil.getOperator().getName());
        operateLogMQUtil.convertAndSend("S2B 平台端-商家管理API-商家商城分类", "修改商家商城分类信息", "修改商家商城分类信息");
        return companyIntoPlatformProvider.editSupplierTab(request);
    }

    @ApiOperation(value = "获取商家商城分类信息")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "商家商城分类id", required = true)
    @RequestMapping(value = "/supplier-tab/get/{id}", method = RequestMethod.GET)
    public BaseResponse<CompanyMallSupplierTabDetailResponse> getById(@PathVariable Long id) {
        if (Objects.isNull(id)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        CompanyMallSupplierTabDetailResponse response = new CompanyMallSupplierTabDetailResponse();
        CompanyMallSupplierTabQueryRequest queryRequest = new CompanyMallSupplierTabQueryRequest();
        queryRequest.setId(id);
        CompanyMallSupplierTabVO companyInfo = companyIntoPlatformQueryProvider.getByIdSupplierTab(queryRequest).getContext();
        KsBeanUtil.copyPropertiesThird(companyInfo, response);
        // 封装返回商家IDs
        if (StringUtils.isNotBlank(companyInfo.getStoreIds())) {
            String[] storeIds = companyInfo.getStoreIds().split(",");
            final ListStoreByIdsRequest storeByIdsRequest = new ListStoreByIdsRequest();
            final List<String> storeIdStrList = Arrays.asList(storeIds);
            storeByIdsRequest.setStoreIds(storeIdStrList.stream().map(Long::valueOf).collect(Collectors.toList()));
            final BaseResponse<ListStoreByIdsResponse> baseResponse = storeQueryProvider.listByIds(storeByIdsRequest);
            final Map<Long, StoreVO> storeVOMap = baseResponse.getContext().getStoreVOList().stream().collect(Collectors.toMap(StoreVO::getStoreId, Function.identity()));
            List<CompanyMallSupplierTabDetailResponse.Store> storeVOList = new ArrayList<>();
            for (String storeId : storeIds) {
                if (storeVOMap.containsKey(Long.valueOf(storeId))) {
                    final StoreVO storeVO = storeVOMap.get(Long.valueOf(storeId));
                    CompanyMallSupplierTabDetailResponse.Store store = new CompanyMallSupplierTabDetailResponse.Store();
                    store.setStoreId(storeVO.getStoreId());
                    store.setStoreName(storeVO.getStoreName());
                    store.setSupplierName(storeVO.getSupplierName());
                    if (null != storeVO.getCompanyInfo())
                        store.setCompanyInfoId(storeVO.getCompanyInfo().getCompanyInfoId());
                    storeVOList.add(store);
                }
            }
            response.setStores(storeVOList);
        }
        return BaseResponse.success(response);
    }
}

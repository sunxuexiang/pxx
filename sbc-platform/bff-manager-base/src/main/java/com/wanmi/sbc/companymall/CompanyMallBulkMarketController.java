package com.wanmi.sbc.companymall;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.account.AccountPassWordProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.request.account.ResetAccountPasswordRequest;
import com.wanmi.sbc.customer.api.request.company.*;
import com.wanmi.sbc.customer.api.response.company.CompanyMallBulkMarketPageResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallBulkMarketResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyMallBulkMarketSaveResponse;
import com.wanmi.sbc.customer.bean.enums.MallOpenStatus;
import com.wanmi.sbc.customer.bean.vo.CompanyMallBulkMarketProvinceVO;
import com.wanmi.sbc.customer.bean.vo.CompanyMallBulkMarketVO;
import com.wanmi.sbc.customer.bean.vo.CompanyMallReturnGoodsAddressVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-14 15:30
 **/
@RestController
@RequestMapping("/company/into-mall")
@Api(tags = "CompanyMallBulkMarketController", description = "S2B 平台端-商家管理API-市场批发")
@Slf4j
public class CompanyMallBulkMarketController {

    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;

    @Autowired
    private CompanyIntoPlatformProvider companyIntoPlatformProvider;

    @Autowired
    CommonUtil commonUtil;

    @Autowired
    private AccountPassWordProvider accountPassWordProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    @ApiOperation(value = "查询市场批发列表")
    @RequestMapping(value = "/mall-market/page", method = RequestMethod.POST)
    public BaseResponse<Page<CompanyMallBulkMarketResponse>> page(@RequestBody CompanyMallBulkMarketPageRequest request) {
        request.setDeleteFlag(DeleteFlag.NO);
        request.putSort("sort", SortType.ASC.toValue());
        Page<CompanyMallBulkMarketVO> page = companyIntoPlatformQueryProvider.pageMarket(request).getContext()
                .getPage();
        List<CompanyMallBulkMarketResponse> list = KsBeanUtil.convertList(page.getContent(), CompanyMallBulkMarketResponse.class);
        return BaseResponse.success(new PageImpl<>(list, request.getPageable(), page.getTotalElements()));
    }

    @ApiOperation(value = "查询所有批发市场")
    @RequestMapping(value = "/mallMarket/listAllOpen", method = RequestMethod.POST)
    public BaseResponse<List<CompanyMallBulkMarketProvinceVO>> listOpenMarket() {
        // 根据签约市场查找市场信息
        final CompanyMallBulkMarketPageRequest pageRequest = new CompanyMallBulkMarketPageRequest();
        pageRequest.setPageNum(0);
        pageRequest.setPageSize(1000);
        pageRequest.setDeleteFlag(DeleteFlag.NO);
        pageRequest.setOpenStatus(MallOpenStatus.OPEN.toValue());
        final BaseResponse<CompanyMallBulkMarketPageResponse> marketResponse = companyIntoPlatformQueryProvider.pageMarket(pageRequest);
        List<CompanyMallBulkMarketVO> contentSource = marketResponse.getContext().getPage().getContent();
        final List<CompanyMallBulkMarketVO> content = KsBeanUtil.convertList(contentSource, CompanyMallBulkMarketVO.class);
        content.forEach(u -> {
            if (u.getSort() == null) {
                u.setSort(new BigDecimal("100"));
            }
        });
        content.sort(Comparator.comparing(CompanyMallBulkMarketVO::getSort));
        List<Long> p = new ArrayList<>();
        content.forEach(f -> {
            if (!p.contains(f.getProvinceId())) p.add(f.getProvinceId());
        });

        List<CompanyMallBulkMarketProvinceVO> list = new ArrayList<>();
        final Map<Long, List<CompanyMallBulkMarketVO>> collect = content.stream().collect(Collectors.groupingBy(CompanyMallBulkMarketVO::getProvinceId));
        p.forEach(pId -> {
            final List<CompanyMallBulkMarketVO> mList = collect.get(pId);
            if (CollectionUtils.isEmpty(mList)) return;
            final CompanyMallBulkMarketProvinceVO companyMallBulkMarketProvinceVO = new CompanyMallBulkMarketProvinceVO();
            list.add(companyMallBulkMarketProvinceVO);
            companyMallBulkMarketProvinceVO.setProvinceId(pId);
            companyMallBulkMarketProvinceVO.setProvinceName(mList.get(0).getProvinceName());
            mList.sort(Comparator.comparing(CompanyMallBulkMarketVO::getSort));
            companyMallBulkMarketProvinceVO.setMarkets(mList);
        });
        return BaseResponse.success(list);
    }

    @ApiOperation(value = "查询所有退货信息")
    @RequestMapping(value = "/listReturnGoodsAddress", method = RequestMethod.POST)
    public BaseResponse<List<CompanyMallReturnGoodsAddressVO>> listReturnGoodsAddress(@RequestBody CompanyMallReturnGoodsAddressRequest request) {
        return companyIntoPlatformQueryProvider.listReturnGoodsAddress(request);
    }


    @ApiOperation(value = "添加批发市场信息")
    @RequestMapping(value = "/mall-market/add", method = RequestMethod.PUT)
    public BaseResponse<CompanyMallBulkMarketSaveResponse> add(@RequestBody CompanyMallBulkMarketAddRequest request) {
        request.setOperator(commonUtil.getOperator().getName());
        operateLogMQUtil.convertAndSend("S2B 平台端-商家管理API-市场批发", "添加批发市场信息", "添加批发市场信息:市场名称" + (Objects.nonNull(request) ? request.getMarketName() : ""));
        return companyIntoPlatformProvider.addMarket(request);
    }

    @ApiOperation(value = "修改批发市场信息")
    @RequestMapping(value = "/mall-market/edit", method = RequestMethod.PUT)
    public BaseResponse<CompanyMallBulkMarketSaveResponse> update(@RequestBody CompanyMallBulkMarketAddRequest request) {
        request.setOperator(commonUtil.getOperator().getName());
        operateLogMQUtil.convertAndSend("S2B 平台端-商家管理API-市场批发", "修改批发市场信息", "修改批发市场信息:市场名称" + (Objects.nonNull(request) ? request.getMarketName() : ""));
        return companyIntoPlatformProvider.editMarket(request);
    }


    @ApiOperation(value = "获取批发市场信息")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "批发市场id", required = true)
    @RequestMapping(value = "/mall-market/get/{id}", method = RequestMethod.GET)
    public BaseResponse<CompanyMallBulkMarketSaveResponse> getById(@PathVariable Long id) {
        if (Objects.isNull(id)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        CompanyMallBulkMarketSaveResponse response = new CompanyMallBulkMarketSaveResponse();
        CompanyMallBulkMarketVO companyInfo = companyIntoPlatformQueryProvider.getByIdForMarket(
                CompanyMallBulkMarketQueryRequest.builder().marketId(id).build()
        ).getContext();
        KsBeanUtil.copyPropertiesThird(companyInfo, response);
        return BaseResponse.success(response);
    }

    @ApiOperation(value = "批量编辑排序")
    @RequestMapping(value = "/mall-sort/batch", method = RequestMethod.POST)
    public BaseResponse<Integer> batchEditSort(@RequestBody CompanyMallBatchSortEditRequest request) {
        operateLogMQUtil.convertAndSend("S2B 平台端-商家管理API-市场批发", "批量编辑排序", "批量编辑排序");
        return companyIntoPlatformProvider.batchEditSort(request);
    }

    @ApiOperation(value = "批量编辑排序")
    @RequestMapping(value = "/mall-sort/edit", method = RequestMethod.POST)
    public BaseResponse<Integer> editSor(@RequestBody CompanyMallSortEditRequest request) {
        operateLogMQUtil.convertAndSend("S2B 平台端-商家管理API-市场批发", "编辑排序", "编辑排序");
        return companyIntoPlatformProvider.eidtSort(request);
    }

    @ApiOperation(value = "批量编辑排序")
    @RequestMapping(value = "/mall-market/refresh-search-name", method = RequestMethod.POST)
    public BaseResponse<Integer> refreshSearchName(@RequestBody CompanyMallMarketRefreshNameRequest request) {
        operateLogMQUtil.convertAndSend("S2B 平台端-商家管理API-市场批发", "刷新搜索名称", "刷新搜索名称");
        return companyIntoPlatformProvider.refreshSearchName(request);
    }

    @ApiOperation(value = "重新修改密码")
    @RequestMapping(value = "/reset-pwd", method = RequestMethod.POST)
    public BaseResponse<String> resetPwd(@RequestBody ResetAccountPasswordRequest request) {
        operateLogMQUtil.convertAndSend("S2B 平台端-商家管理API-市场批发", "重新修改密码", "重新修改密码");
        final String[] split = request.getAccount().split(",");
        final StringBuilder stringBuilder = new StringBuilder();
        for (String s : split) {
            final ResetAccountPasswordRequest convert = KsBeanUtil.convert(request, ResetAccountPasswordRequest.class);
            convert.setAccount(s);
            try {
                accountPassWordProvider.resetPassword(convert);
                stringBuilder.append(s).append(",");
            } catch (Exception e) {
                log.error("Failed to reset password,{}", s);
            }
        }
        return BaseResponse.success(stringBuilder.toString());
    }
}

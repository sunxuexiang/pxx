package com.wanmi.sbc.company;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wanmi.sbc.account.api.provider.company.CompanyAccountProvider;
import com.wanmi.sbc.account.api.provider.company.CompanyAccountQueryProvider;
import com.wanmi.sbc.account.api.request.company.CompanyAccountByCompanyInfoIdAndDefaultFlagRequest;
import com.wanmi.sbc.account.api.request.company.CompanyAccountFindByAccountIdRequest;
import com.wanmi.sbc.account.api.request.company.CompanyAccountRemitRequest;
import com.wanmi.sbc.account.api.response.company.CompanyAccountFindResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.provider.employeecontract.EmployeeContractProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.*;
import com.wanmi.sbc.customer.api.request.store.ListStoreRequest;
import com.wanmi.sbc.customer.api.response.company.*;
import com.wanmi.sbc.customer.api.response.employeecontract.EmployeeContractListResponese;
import com.wanmi.sbc.customer.api.response.store.ListStoreResponse;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.enums.MallContractRelationType;
import com.wanmi.sbc.customer.bean.vo.*;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.response.goods.GoodsStoreOnSaleResponse;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.bean.vo.ReturnOrderVO;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

/**
 * 商家
 * Created by sunkun on 2017/11/6.
 */
@RestController
@RequestMapping("/company")
@Api(tags = "CompanyController", description = "S2B 平台端-商家管理API")
@Slf4j
public class CompanyController {

    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;

    @Autowired
    private CompanyInfoProvider companyInfoProvider;

    @Autowired
    private CompanyAccountQueryProvider companyAccountQueryProvider;

    @Autowired
    private CompanyAccountProvider companyAccountProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;
    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;
    @Autowired
    private EmployeeContractProvider employeeContractProvider;
    @Autowired
    private StoreQueryProvider storeQueryProvider;
    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    /**
     * 商家列表
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "查询商家列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public BaseResponse<Page<CompanyReponse>> list(@RequestBody CompanyPageRequest request) {
        // 获取推荐商家
        final Set<Long> recommendCompanyInfoIds = mapRecommendCompanyInfoIds();
        if (Objects.equals(request.getRecommendFlag(), DefaultFlag.YES.toValue())) {
            if (CollectionUtils.isEmpty(recommendCompanyInfoIds)) {
                recommendCompanyInfoIds.add(-100L);
            }
            request.setCompanyInfoIds(Lists.newArrayList(recommendCompanyInfoIds));
        } else if (Objects.equals(request.getRecommendFlag(), DefaultFlag.NO.toValue())) {
            request.setCompanyInfoIdsNotIn(Lists.newArrayList(recommendCompanyInfoIds));
        }

        if (request.getSelfManage() != null){
            final List<Long> selfManageCompanyInfoIds = mapSelfManageCompanyInfoIds();
            if (Objects.equals(request.getSelfManage(), 1)) {
                if (CollectionUtils.isNotEmpty(request.getCompanyInfoIds())){
                    selfManageCompanyInfoIds.retainAll(request.getCompanyInfoIds());
                }
                if (CollectionUtils.isEmpty(selfManageCompanyInfoIds)) {
                    selfManageCompanyInfoIds.add(-100L);
                }
                request.setCompanyInfoIds(selfManageCompanyInfoIds);
            } else if (Objects.equals(request.getSelfManage(), 0)) {
                if (CollectionUtils.isNotEmpty(request.getCompanyInfoIdsNotIn())){
                    selfManageCompanyInfoIds.addAll(request.getCompanyInfoIds());
                }
                request.setCompanyInfoIdsNotIn(selfManageCompanyInfoIds);
            }
        }
        
        if (request.getMarketId() != null){
            marketQueryWrap(request);
        }
        request.setDeleteFlag(DeleteFlag.NO);
        // request.putSort("createTime", SortType.DESC.toValue());
        Page<CompanyInfoVO> page = companyInfoQueryProvider.pageCompanyInfo(request).getContext()
                .getCompanyInfoVOPage();
        List<CompanyReponse> companyReponseList = new ArrayList<>();
        log.info("查询后台商家列表===============");
        List<String> empIdList=new ArrayList<>();
        //获取里面员工信息
        page.getContent().forEach(info -> {
                    if (CollectionUtils.isNotEmpty(info.getEmployeeVOList())) {
                        EmployeeVO employee = info.getEmployeeVOList().get(0);
                        empIdList.add(employee.getEmployeeId());
                    }
        });
        Map<String,String> investmentManagerMap=new HashMap<>();
        Map<String,String> isContractMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(empIdList)) {
            try {
                EmployeeContractListResponese employeeContractListResponese=employeeContractProvider.findByEmployeeIdList(empIdList).getContext();
                if (!employeeContractListResponese.getEmployeeContractResponeseList().isEmpty()){
                    employeeContractListResponese.getEmployeeContractResponeseList().stream().forEach(obj->{
                        isContractMap.put(obj.getEmployeeId(),obj.getStatus()+"-"+obj.getSignType());
                        investmentManagerMap.put(obj.getEmployeeId(),obj.getInvestmentManager());
                    });
                }
            } catch (Exception e) {
                log.error("查询员工合同信息异常,p:{}", JSON.toJSON(empIdList),e);
            }
        }
        final List<Long> storeIds = page.getContent().stream().filter(com -> CollectionUtils.isNotEmpty(com.getEmployeeVOList())).map(c -> c.getStoreVOList().get(0).getStoreId()).collect(Collectors.toList());
        Map<Long, String> storeMarketNameMap = wrapStoreMarketMap(storeIds);
        Map<Long, Integer> storeOnSaleGoodsNumMap = wrapStoreOnSaleGoods(storeIds);
        // 获取推荐商家
        page.getContent().forEach(info -> {
            //组装返回结构
            CompanyReponse companyReponse = new CompanyReponse();
            companyReponse.setCompanyInfoId(info.getCompanyInfoId());
            companyReponse.setCompanyCode(info.getCompanyCodeNew());
            companyReponse.setCompanyType(info.getCompanyType());
            companyReponse.setSupplierName(info.getSupplierName());
            if (recommendCompanyInfoIds.contains(info.getCompanyInfoId())) {
                companyReponse.setRecommendFlag(true);
            }else {
                companyReponse.setRecommendFlag(false);
            }
            if (CollectionUtils.isNotEmpty(info.getEmployeeVOList())) {
                EmployeeVO employee = info.getEmployeeVOList().get(0);
                companyReponse.setAccountName(employee.getAccountName());
                companyReponse.setAccountState(employee.getAccountState());
                String investmentManager=investmentManagerMap.get(employee.getEmployeeId());
                companyReponse.setInvestmentManager(Objects.isNull(investmentManager)?"":investmentManager);
                companyReponse.setAccountDisableReason(employee.getAccountDisableReason());
            }
            if (nonNull(info.getStoreVOList()) && !info.getStoreVOList().isEmpty()) {
                StoreVO store = info.getStoreVOList().get(0);
                companyReponse.setStoreId(store.getStoreId());
                companyReponse.setStoreName(store.getStoreName());
                companyReponse.setSelfManage(store.getSelfManage());
                companyReponse.setContractStartDate(store.getContractStartDate());
                companyReponse.setContractEndDate(store.getContractEndDate());
                companyReponse.setAuditState(store.getAuditState());
                companyReponse.setAuditReason(store.getAuditReason());
                companyReponse.setStoreState(store.getStoreState());
                companyReponse.setStoreClosedReason(store.getStoreClosedReason());
                companyReponse.setApplyEnterTime(store.getApplyEnterTime());
                companyReponse.setApplyTime(store.getApplyTime());
                companyReponse.setStoreType(store.getStoreType());
                companyReponse.setPileState(store.getPileState());
                companyReponse.setMarketName(storeMarketNameMap.get(store.getStoreId()));
                companyReponse.setOnSaleGoodsNum(storeOnSaleGoodsNumMap.get(store.getStoreId()));
                isContract(info,isContractMap,store,companyReponse);

            }
            if (CollectionUtils.isNotEmpty(info.getReturnGoodsAddressList())){
                companyReponse.setReturnGoodsAddress(info.getReturnGoodsAddressList().get(0));
            }
            companyReponseList.add(companyReponse);
        });
        return BaseResponse.success(new PageImpl<>(companyReponseList, request.getPageable(), page.getTotalElements()));
    }

    @RequestMapping(value = "/list/export", method = RequestMethod.POST)
    public void exportList(@RequestBody CompanyPageRequest request, HttpServletResponse response) {
        request.setPageNum(0);
        request.setPageSize(Integer.MAX_VALUE);
        final BaseResponse<Page<CompanyReponse>> baseResponse = list(request);
        if (CollectionUtils.isEmpty(baseResponse.getContext().getContent())) return;
        String headerKey = "Content-Disposition";
        LocalDateTime dateTime = LocalDateTime.now();
        String fileName = String.format("导出商家列表_%s.xls", dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("/exportList, fileName={},", fileName, e);
        }
        List<CompanyExportResponse> exportCompanyList = KsBeanUtil.convertList(baseResponse.getContext().getContent(), CompanyExportResponse.class);
        try {
            String headerValue = String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName);
            response.setHeader(headerKey, headerValue);
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            final ServletOutputStream outputStream = response.getOutputStream();
            ExcelHelper<CompanyExportResponse> excelHelper = new ExcelHelper<>();
            Column[] columns = {
                    new Column("商家账号", new SpelColumnRender<CompanyExportResponse>("accountName")),
                    new Column("商家名称", new SpelColumnRender<CompanyExportResponse>("supplierName")),
                    new Column("店铺名称", new SpelColumnRender<CompanyExportResponse>("storeName")),
                    new Column("批发市场", new SpelColumnRender<CompanyExportResponse>("marketName")),
                    new Column("入驻商家代表", new SpelColumnRender<CompanyExportResponse>("investmentManager")),
                    new Column("签约时间", new SpelColumnRender<CompanyExportResponse>("applyTime")),
                    new Column("审核时间", new SpelColumnRender<CompanyExportResponse>("applyEnterTime")),
                    new Column("上架商品数", new SpelColumnRender<CompanyExportResponse>("onSaleGoodsNum"))
            };
            excelHelper
                    .addSheet(
                            "商家列表导出",
                            columns,
                            exportCompanyList
                    );
            excelHelper.write(outputStream);
        } catch (IOException e) {
            log.error("导出商家列表失败", e);
        }
    }

    private Map<Long, Integer> wrapStoreOnSaleGoods(List<Long> storeIds) {
        final BaseResponse<List<GoodsStoreOnSaleResponse>> listBaseResponse = goodsQueryProvider.listStoreOnSaleGoodsNum(storeIds);
        return listBaseResponse.getContext().stream().collect(Collectors.toMap(GoodsStoreOnSaleResponse::getStoreId, GoodsStoreOnSaleResponse::getOnSaleNum));

    }

    private void marketQueryWrap(CompanyPageRequest request) {
        final CompanyMallContractRelationPageRequest relationPageRequest = new CompanyMallContractRelationPageRequest();
        relationPageRequest.setPageSize(0);
        relationPageRequest.setPageSize(Integer.MAX_VALUE);
        relationPageRequest.setRelationType(MallContractRelationType.MARKET.getValue());
        relationPageRequest.setRelationValue(request.getMarketId().toString());
        final BaseResponse<CompanyMallContractRelationPageResponse> contractRelation = companyIntoPlatformQueryProvider.pageContractRelation(relationPageRequest);
        List<Long> companyIds = contractRelation.getContext().getPage().getContent().stream().map(CompanyMallContractRelationVO::getCompanyInfoId).collect(Collectors.toList());
        // setCompanyInfoIds 都是为空，所以不加判断了
        if (CollectionUtils.isEmpty(companyIds)) {
            request.setCompanyInfoIds(Lists.newArrayList(-1L));
        } else {
            if (CollectionUtils.isNotEmpty(request.getCompanyInfoIds())){
                companyIds.retainAll(request.getCompanyInfoIds());
            }
            if (CollectionUtils.isEmpty(companyIds)){
                request.setCompanyInfoIds(Lists.newArrayList(-1L));
            }
            request.setCompanyInfoIds(companyIds);
        }
    }

    private Map<Long, String> wrapStoreMarketMap(List<Long> storeIds) {
        Map<Long, String> map = new HashMap<>();
        final CompanyMallContractRelationPageRequest relationPageRequest = new CompanyMallContractRelationPageRequest();
        relationPageRequest.setPageSize(0);
        relationPageRequest.setPageSize(Integer.MAX_VALUE);
        relationPageRequest.setRelationType(MallContractRelationType.MARKET.getValue());
        relationPageRequest.setStoreIds(storeIds);
        final BaseResponse<CompanyMallContractRelationPageResponse> contractRelation = companyIntoPlatformQueryProvider.pageContractRelation(relationPageRequest);
        final List<CompanyMallContractRelationVO> content = contractRelation.getContext().getPage().getContent();
        if (CollectionUtils.isEmpty(content)) return map;
        return content.stream().collect(Collectors.toMap(CompanyMallContractRelationVO::getStoreId, CompanyMallContractRelationVO::getRelationName, (o, n) -> o));
    }

    private void isContract(CompanyInfoVO info,Map<String,String> isContractMap,StoreVO store,CompanyReponse companyReponse){
        // 如果审核通过需要判断是否已经签署过合同
        // 1. 店已审核通过，合同未签署的状态为1（针对老用户）
        // 2. 店未审核通过，合同已签署的状态为1
        // 3. 店未审核通过，合同未签署的状态为1
        // 4. 线下合同签署的为0
        // 5. 店已审核通过，合同已签署状态为0
        EmployeeVO employee = info.getEmployeeVOList().get(0);
        String s = isContractMap.get(employee.getEmployeeId());
        Integer status = null;
        Integer signType = 1;
        if (StringUtils.isNotEmpty(s)) {
            status = s.split("-")[0].equals("null")?null:Integer.valueOf(s.split("-")[0]);
            signType = s.split("-")[1].equals("null")?1:Integer.valueOf(s.split("-")[1]);
        }

        CheckState auditState = store.getAuditState();
        if (status == null && (signType != 1)) {
            companyReponse.setIsContract(1);
        } else if (signType == 0 && (auditState==null || auditState.toValue()!=1) ) {
            companyReponse.setIsContract(1);
        } else {
            companyReponse.setIsContract(0);
        }
    }

    private List<Long> mapSelfManageCompanyInfoIds() {
        try {
            final ListStoreRequest listStoreRequest = new ListStoreRequest();
            listStoreRequest.setSelfManage(1);
            final BaseResponse<ListStoreResponse> listStoreResponseBaseResponse = storeQueryProvider.listStore(listStoreRequest);
            return listStoreResponseBaseResponse.getContext().getStoreVOList().stream().map(o ->
                o.getCompanyInfo().getCompanyInfoId()).distinct().collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询自营商家异常", e);
            return new ArrayList<>();
        }
    }

    private Set<Long> mapRecommendCompanyInfoIds() {
        try {
            final CompanyMallSupplierRecommendPageRequest recommendPageRequest = new CompanyMallSupplierRecommendPageRequest();
            recommendPageRequest.setDeleteFlag(DeleteFlag.NO);
            recommendPageRequest.setPageNum(0);
            recommendPageRequest.setPageSize(Integer.MAX_VALUE);
            final BaseResponse<CompanyMallSupplierRecommendPageResponse> pageSupplierRecommend =
                    companyIntoPlatformQueryProvider.pageSupplierRecommend(recommendPageRequest);
            return pageSupplierRecommend.getContext().getPage().getContent()
                    .stream().map(CompanyMallSupplierRecommendVO::getCompanyInfoId).collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("查询推荐商家异常", e);
            return new HashSet<>();
        }
    }

    /**
     * 工商信息修改
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "修改商家工商信息")
    @RequestMapping(method = RequestMethod.PUT)
    public BaseResponse<CompanyInformationModifyResponse> update(@Valid @RequestBody CompanyInformationSaveRequest request) {
        operateLogMQUtil.convertAndSend("设置","店铺信息","编辑店铺信息");
        return companyInfoProvider.modifyCompanyInformation(request);
    }

    /**
     * 查询公司信息
     *
     * @return
     */
    @ApiOperation(value = "查询商家公司信息")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "商家公司id", required = true)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public BaseResponse<CompanyInfoResponse> findOne(@PathVariable Long id) {
        if (Objects.isNull(id)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        CompanyInfoResponse response = new CompanyInfoResponse();
        CompanyInfoVO companyInfo = companyInfoQueryProvider.getCompanyInfoById(
                CompanyInfoByIdRequest.builder().companyInfoId(id).build()
        ).getContext();
        KsBeanUtil.copyPropertiesThird(companyInfo, response);
        return BaseResponse.success(response);
    }


    /**
     * 商家列表(收款账户)
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "分页查询商家公司收款账户")
    @RequestMapping(value = "/account", method = RequestMethod.POST)
    public BaseResponse<Page<CompanyAccountVO>> accountList(@RequestBody CompanyAccountPageRequest request) {
        request.setDeleteFlag(DeleteFlag.NO);
        return BaseResponse.success(companyInfoQueryProvider.pageCompanyAccount(request).getContext()
                .getCompanyAccountVOPage());
    }


    /**
     * 商家账号明细
     *
     * @param companyInfoId
     * @return
     */
    @ApiOperation(value = "根据商家公司id查询商家收款账户列表")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "companyInfoId", value = "商家公司id", required = true)
    @RequestMapping(value = "/account/detail/{companyInfoId}", method = RequestMethod.GET)
    public BaseResponse<List<com.wanmi.sbc.account.bean.vo.CompanyAccountVO>> accountDetail(@PathVariable Long companyInfoId) {
        return BaseResponse.success(companyAccountQueryProvider.listByCompanyInfoIdAndDefaultFlag(
                CompanyAccountByCompanyInfoIdAndDefaultFlagRequest.builder()
                        .companyInfoId(companyInfoId).defaultFlag(DefaultFlag.NO).build()
        ).getContext().getCompanyAccountVOList());
    }


    /**
     * 商家账号打款
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "商家账号打款")
    @RequestMapping(value = "/account/remit", method = RequestMethod.PUT)
    public BaseResponse accountRemit(@RequestBody CompanyAccountRemitRequest request) {
        //操作日志记录
        CompanyAccountFindResponse findResponse =
                companyAccountQueryProvider.getByAccountId(new CompanyAccountFindByAccountIdRequest(request.getAccountId())).getContext();
        if (Objects.nonNull(findResponse)){
            CompanyInfoByIdResponse response =
                    companyInfoQueryProvider.getCompanyInfoById(new CompanyInfoByIdRequest(findResponse.getCompanyInfoId())).getContext();
            operateLogMQUtil.convertAndSend("财务", "确认打款", "确认打款：商家编号" + (Objects.nonNull(response) ?
                    response.getCompanyCode() : ""));
        }

        return companyAccountProvider.remit(request);
    }

}

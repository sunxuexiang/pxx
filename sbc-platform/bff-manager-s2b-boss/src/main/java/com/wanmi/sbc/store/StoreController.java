package com.wanmi.sbc.store;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.EnableStatus;
import com.wanmi.sbc.common.enums.StoreType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreCustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.provider.storelevel.StoreLevelQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyPageRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByCompanyIdRequest;
import com.wanmi.sbc.customer.api.request.store.*;
import com.wanmi.sbc.customer.api.request.store.validGroups.StoreUpdate;
import com.wanmi.sbc.customer.api.request.storelevel.StoreLevelByStoreIdRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoPageResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyReponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeByCompanyIdResponse;
import com.wanmi.sbc.customer.api.response.store.StoreBaseInfoResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.api.response.storelevel.StroeLevelInfoResponse;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.bean.vo.*;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.goods.api.provider.cate.ContractCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.distributor.goods.DistributorGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateDeliveryAreaSaveProvider;
import com.wanmi.sbc.goods.api.provider.pointsgoods.PointsGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.pointsgoods.PointsGoodsSaveProvider;
import com.wanmi.sbc.goods.api.request.cate.ContractCateListRequest;
import com.wanmi.sbc.goods.api.request.distributor.goods.DistributorGoodsInfoDeleteByStoreIdRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaSaveListRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaSaveRequest;
import com.wanmi.sbc.goods.api.request.pointsgoods.PointsGoodsByStoreIdRequest;
import com.wanmi.sbc.goods.api.request.pointsgoods.PointsGoodsSwitchRequest;
import com.wanmi.sbc.goods.bean.vo.ContractCateVO;
import com.wanmi.sbc.goods.bean.vo.PointsGoodsVO;
import com.wanmi.sbc.pay.api.provider.CcbPayProvider;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.wallet.api.request.wallet.WalletByWalletIdAddRequest;
import com.wanmi.sbc.wallet.api.response.wallet.WalletStatusResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: songhanlin
 * @Date: Created In 下午2:20 2017/11/2
 * @Description: 店铺信息Controller
 */
@Api(tags = "StoreController", description = "店铺信息相关API")
@RestController("bossStoreController")
@RequestMapping("/store")
@Slf4j
public class StoreController {

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private StoreProvider storeProvider;

    @Autowired
    private StoreBaseService baseService;

    @Autowired
    private StoreSelfService selfService;

    @Autowired
    private StoreCustomerQueryProvider storeCustomerQueryProvider;

    @Autowired
    private StoreLevelQueryProvider storeLevelQueryProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private DistributorGoodsInfoProvider distributorGoodsInfoProvider;

    @Autowired
    private PointsGoodsQueryProvider pointsGoodsQueryProvider;

    @Autowired
    private PointsGoodsSaveProvider pointsGoodsSaveProvider;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private CustomerWalletProvider customerWalletProvider;

    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;

    @Autowired
    private CcbPayProvider ccbPayProvider;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private ContractCateQueryProvider contractCateQueryProvider;

    @Autowired
    private FreightTemplateDeliveryAreaSaveProvider freightTemplateDeliveryAreaSaveProvider;

    /**
     * 编辑店铺结算日期
     */
    @ApiOperation(value = "编辑店铺结算日期")
    @RequestMapping(value = "/days", method = RequestMethod.PUT)
    public BaseResponse<StoreVO> edit(@Valid @RequestBody AccountDateModifyRequest request) {
        StoreVO store = storeProvider.accountDateModify(request).getContext().getStoreVO();
        operateLogMQUtil.convertAndSend("商家", "编辑店铺结算日期", "店铺：结算日期" + request.getAccountDay());
        return BaseResponse.success(store);
    }


    /**
     * 查询店铺信息
     */
    @ApiOperation(value = "查询店铺信息")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "storeId",
            value = "店铺Id", required = true)
    @RequestMapping(value = "/{storeId}", method = RequestMethod.GET)
    public BaseResponse<StoreVO> info(@PathVariable Long storeId) {
        StoreVO store = storeQueryProvider.getNoDeleteStoreById(new NoDeleteStoreByIdRequest(storeId))
                .getContext().getStoreVO();
        return BaseResponse.success(store);
    }

    /**
     * 根据商家id获取店铺信息
     *
     * @param companyInfoId
     * @return
     */
    @ApiOperation(value = "根据商家id获取店铺信息")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "companyInfoId",
            value = "商家Id", required = true)
    @RequestMapping("/from/company/{companyInfoId}")
    public BaseResponse<StoreVO> fromCompanyInfoId(@PathVariable Long companyInfoId) {
        StoreVO store = storeProvider.initStoreByCompany(new InitStoreByCompanyRequest(companyInfoId))
                .getContext().getStoreVO();
        return BaseResponse.success(store);
    }

    /**
     * 开/关 店铺
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "开/关 店铺")
    @RequestMapping(value = "/close", method = RequestMethod.PUT)
    public BaseResponse<StoreVO> closeStore(@Valid @RequestBody StoreSwitchRequest request) {
        StoreVO store = storeProvider.switchStore(request).getContext().getStoreVO();

        if (StoreState.CLOSED.equals(request.getStoreState())) {
            // 店铺关店。同时删除分销员关联的分销商品
            DistributorGoodsInfoDeleteByStoreIdRequest distributorGoodsInfoDeleteByStoreIdRequest =
                    new DistributorGoodsInfoDeleteByStoreIdRequest();
            distributorGoodsInfoDeleteByStoreIdRequest.setStoreId(request.getStoreId());
            distributorGoodsInfoProvider.deleteByStoreId(distributorGoodsInfoDeleteByStoreIdRequest);
            // 停用该店铺关联的积分商品
            List<PointsGoodsVO> pointsGoodsVOList = pointsGoodsQueryProvider.getByStoreId(PointsGoodsByStoreIdRequest.builder()
                    .storeId(store.getStoreId())
                    .build()).getContext().getPointsGoodsVOList();
            pointsGoodsVOList.forEach(pointsGoodsVO -> pointsGoodsSaveProvider.modifyStatus(PointsGoodsSwitchRequest.builder()
                    .pointsGoodsId(pointsGoodsVO.getPointsGoodsId())
                    .status(EnableStatus.DISABLE)
                    .build()));
        }
        if (request.getStoreId() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"当前商品ID为空");
        }
        esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().storeId(request.getStoreId()).build());
        //记录操作日志
        if (request.getStoreState() == StoreState.OPENING) {
            operateLogMQUtil.convertAndSend("商家", "开店",
                    "开店：商家编号" + store.getCompanyInfo().getCompanyCode());
        } else {
            operateLogMQUtil.convertAndSend("商家", "关店",
                    "关店：商家编号" + store.getCompanyInfo().getCompanyCode());
        }
        return BaseResponse.success(store);
    }

    /**
     * 通过/驳回 审核
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "通过/驳回 审核")
    @RequestMapping(value = "/reject", method = RequestMethod.PUT)
    public BaseResponse<StoreVO> rejectStore(@Valid @RequestBody StoreAuditRequest request) {
        // 审核通过，检验参数
        if (request.getAuditState() == CheckState.CHECKED) {
            if (null == request.getCompanyType()) {
                throw new SbcRuntimeException("审核通过店铺公司类型不能为空");
            }

            if (StringUtils.isBlank(request.getConstructionBankMerchantNumber())) {
                return BaseResponse.error("建行商家编码不能为空");
            }
            BaseResponse<StoreVO> errorMsg = checkCCB(request.getShareRatio(), request.getConstructionBankMerchantNumber(),request.getSettlementCycle());
            if (errorMsg != null) return errorMsg;
        }
        StoreVO store = selfService.rejectOrPass(request);
        // 审核通过，推送店铺信息到erp
        if (Objects.equals(store.getAuditState(), CheckState.CHECKED)) {
            final StoreSupplierPushErpRequest storeSupplierPushErpRequest = new StoreSupplierPushErpRequest();
            storeSupplierPushErpRequest.setStoreIds(Lists.newArrayList(store.getStoreId()));
            pushStoreForErpWrap(storeSupplierPushErpRequest);
        }
        //记录操作日志
        final String params = JSON.toJSONString(request);
        if (request.getAuditState() == CheckState.CHECKED) {
            operateLogMQUtil.convertAndSend("商家", "审核商家",
                    "审核商家：商家编号" + store.getCompanyInfo().getCompanyCode()+",参数:"+ params);
        } else if (request.getAuditState() == CheckState.NOT_PASS) {
            operateLogMQUtil.convertAndSend("商家", "驳回商家",
                    "驳回商家：商家编号" + store.getCompanyInfo().getCompanyCode()+",参数:"+ params);
        }
        return BaseResponse.success(store);
    }

    private BaseResponse<StoreVO> checkCCB(BigDecimal shareRatio,String constructionBankMerchantNumber, Integer settlementCycle ) {
        String errorMsg = verifyShareRatio(shareRatio);
        if (StringUtils.isNotBlank(errorMsg)) {
            return BaseResponse.error(errorMsg);
        }
        // 校验建行商家编码是否合法
        Boolean verify = ccbPayProvider.validCcbMerchantNo(constructionBankMerchantNumber).getContext();
        if (!verify) {
            return BaseResponse.error("建行商家不存在");
        }
        if (settlementCycle == null || settlementCycle < 1) {
            return BaseResponse.error("天数不能小于1");
        }
        return null;
    }

    @RequestMapping(value = "/pushStoreForErp", method = RequestMethod.PUT)
    public BaseResponse<Boolean> pushStoreForErp(@Valid @RequestBody StoreSupplierPushErpRequest storeSupplierPushRequest) {
        return pushStoreForErpWrap(storeSupplierPushRequest);
    }

    private BaseResponse<Boolean> pushStoreForErpWrap(StoreSupplierPushErpRequest storeSupplierPushRequest) {
        try {
            if (CollectionUtils.isEmpty(storeSupplierPushRequest.getStoreIds())) {
                return BaseResponse.error("店铺id不能为空");
            }
            Map<Long, String> storeCatMap = new HashMap<>();
            storeSupplierPushRequest.getStoreIds().forEach(storeId -> {
                ContractCateListRequest contractCateQueryRequest = new ContractCateListRequest();
                contractCateQueryRequest.setStoreId(storeId);
                final List<ContractCateVO> contractCateList = contractCateQueryProvider.list(contractCateQueryRequest).getContext().getContractCateList();
                Set<String> sbStr = new HashSet<>();
                contractCateList.forEach(contractCateVO -> {
                    StringBuilder sb = new StringBuilder();
                    final String cateName = contractCateVO.getCateName();
                    if (StringUtils.isBlank(contractCateVO.getParentGoodCateNames())) return;
                    final String[] split = contractCateVO.getParentGoodCateNames().split("/");
                    if (split.length == 0) return;
                    sb.append(split[split.length - 1]).append("/").append(cateName);
                    sbStr.add(sb.toString());
                });
                storeCatMap.put(storeId, String.join("，", sbStr));
            });
            storeSupplierPushRequest.setStoreContactCatStrMap(storeCatMap);
            return storeProvider.pushErp(storeSupplierPushRequest);
        } catch (Exception e) {
            log.error("pushStoreForErpWrap error,p:{}", JSON.toJSONString(storeSupplierPushRequest), e);
            return BaseResponse.success(false);
        }
    }

    private String verifyShareRatio(BigDecimal shareRatio) {
        if (Objects.isNull(shareRatio)) {
            return "佣金比例不能为空";
        }
        if (shareRatio.compareTo(new BigDecimal("0.60")) < 0 || shareRatio.compareTo(new BigDecimal("100")) >= 0) {
            return "佣金比例需在0.6%~100% 范围内";
        }
        return null;
    }

    @ApiOperation(value = "更行建行信息")
    @RequestMapping(value = "/update-jh", method = RequestMethod.POST)
    public BaseResponse updateStoreForJh(@RequestBody StoreAuditJHInfoRequest request) {
        BaseResponse<StoreVO> errorMsg = checkCCB(request.getShareRatio(), request.getConstructionBankMerchantNumber(),request.getSettlementCycle());
        if (errorMsg != null) return errorMsg;
        storeProvider.updateJhInfo(request);
        operateLogMQUtil.convertAndSend("商家", "更行建行信息", JSON.toJSONString(request));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 查询店铺基本信息
     *
     * @return
     */
    @ApiOperation(value = "查询店铺基本信息")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "storeId",
            value = "店铺Id", required = true)
    @RequestMapping(value = "/store-info/{storeId}", method = RequestMethod.GET)
    public BaseResponse<StoreInfoResponse> queryStore(@PathVariable Long storeId) {
        StoreInfoResponse storeInfoResponse = storeQueryProvider.getStoreInfoById(new StoreInfoByIdRequest(storeId))
                .getContext();
        // 查询商家鲸币权限
        WalletByWalletIdAddRequest request = new WalletByWalletIdAddRequest();
        request.setStoreId(String.valueOf(storeId));
        request.setCustomerId(String.valueOf(storeId));
        request.setCustomerAccount(String.valueOf(storeId));
        WalletStatusResponse context = customerWalletProvider.queryWalletStatus(request).getContext();
        storeInfoResponse.setJingBiState(context.getJingBiState());
        log.info("查询store返回3：{}",storeInfoResponse);
        return BaseResponse.success(storeInfoResponse);
    }

    /**
     * 修改店铺基本信息
     *
     * @param saveRequest
     * @return
     */
    @ApiOperation(value = "修改店铺基本信息")
    @RequestMapping(value = "/store-info", method = RequestMethod.PUT)
    public BaseResponse<StoreVO> updateStore(@Validated({StoreUpdate.class}) @RequestBody StoreSaveRequest saveRequest) {
        //saveRequest.setAccountType(AccountType.s2bSupplier);
        StoreVO store = baseService.updateStore(saveRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("商家", "编辑商家信息",
                "编辑商家信息：商家编号" + store.getCompanyInfo().getCompanyCode()+",参数:"+ JSON.toJSONString(saveRequest));
        return BaseResponse.success(store);
    }

    /**
     * 修改签约日期和商家类型
     *
     * @param saveRequest
     * @return
     */
    @ApiOperation(value = "修改签约日期和商家类型")
    @RequestMapping(value = "/contract/date", method = RequestMethod.PUT)
    public BaseResponse<StoreVO> updateStoreContract(@RequestBody StoreContractModifyRequest saveRequest) {
        StoreVO store = storeProvider.modifyStoreContract(saveRequest).getContext().getStoreVO();
        //如果店铺审核通过
        if (Objects.equals(CheckState.CHECKED.toValue(), store.getAuditState().toValue())) {
            esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().storeId(saveRequest.getStoreId()).build());
        }
        operateLogMQUtil.convertAndSend("商家", "修改签约日期和商家类型", "店铺ID:" + store.getStoreId() );
        return BaseResponse.success(store);
    }

    /**
     * 查询店铺的会员信息，不区分会员的禁用状态
     * bail 2017-11-16
     *
     * @return 会员信息
     */
    @ApiOperation(value = "查询店铺的会员信息，不区分会员的禁用状态")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "storeId",
            value = "店铺Id", required = true)
    @RequestMapping(value = "/allCustomers/{storeId}", method = RequestMethod.POST)
    public BaseResponse<List<StoreCustomerVO>> customers(@PathVariable Long storeId) {
        StoreCustomerQueryRequest request = new StoreCustomerQueryRequest();
        request.setStoreId(storeId);

        return BaseResponse.success(storeCustomerQueryProvider.listAllCustomer(request).getContext().getStoreCustomerVOList());
    }

    /**
     * 查询所有会员等级
     *
     * @return
     */
    @ApiOperation(value = "查询所有会员等级")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "storeId",
            value = "店铺Id", required = true)
    @RequestMapping(value = "/levels/{storeId}", method = RequestMethod.GET)
    public BaseResponse<List<CustomerLevelVO>> levels(@PathVariable Long storeId) {
        BaseResponse<StroeLevelInfoResponse> stroeLevelInfoResponseBaseResponse
                = storeLevelQueryProvider.queryStoreLevelInfo(StoreLevelByStoreIdRequest.builder().storeId(storeId).build());
        StroeLevelInfoResponse context = stroeLevelInfoResponseBaseResponse.getContext();
        if (Objects.nonNull(context)) {
            return BaseResponse.success(context.getCustomerLevelVOList());
        }
        return BaseResponse.success(Collections.emptyList());
    }

    @ApiOperation(value = "根据店铺名称模糊匹配店铺列表，自动关联5条信息", notes = "返回Map, 以店铺Id为key, 店铺名称为value")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "storeName", value = "店铺名称", required = true)
    @RequestMapping(value = "/name", method = RequestMethod.GET)
    public BaseResponse<Map<Long, String>> queryStoreByNameForAutoComplete(@RequestParam("storeName") String storeName) {
        List<StoreVO> storeList = storeQueryProvider.listByNameForAutoComplete(
                ListStoreByNameForAutoCompleteRequest.builder().storeName(storeName).build()).getContext().getStoreVOList();
        Map<Long, String> storeMap = new HashMap<>();
        storeList.stream().forEach(store -> storeMap.put(store.getStoreId(), store.getStoreName()));
        return BaseResponse.success(storeMap);
    }

    @ApiOperation(value = "根据店铺名称模糊匹配店铺列表，自动关联5条信息", notes = "返回Map, 以店铺Id为key, 店铺名称为value")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "storeName", value = "店铺名称", required = true)
    @RequestMapping(value = "/provider/name", method = RequestMethod.GET)
    public BaseResponse<Map<Long, String>> queryProviderStoreByNameForAutoComplete(@RequestParam("storeName") String storeName) {
        List<StoreVO> storeList = storeQueryProvider.listByNameForAutoComplete(ListStoreByNameForAutoCompleteRequest.builder().storeName(storeName).storeType(StoreType.PROVIDER).build()).getContext().getStoreVOList();
        Map<Long, String> storeMap = new HashMap<>();
        storeList.stream().forEach(store -> storeMap.put(store.getStoreId(), store.getStoreName()));
        return BaseResponse.success(storeMap);
    }

    @ApiOperation(value = "根据店铺名称模糊匹配店铺列表，自动关联5条信息", notes = "返回Map, 以店铺Id为key, 店铺名称为value")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "storeName", value = "店铺名称", required = true)
    @RequestMapping(value = "/supplier/name", method = RequestMethod.GET)
    public BaseResponse<Map<Long, String>> querySupplierStoreByNameForAutoComplete(@RequestParam("storeName") String storeName) {
        List<StoreVO> storeList = storeQueryProvider.listByNameForAutoComplete(ListStoreByNameForAutoCompleteRequest.builder().storeName(storeName).storeType(StoreType.SUPPLIER).build()).getContext().getStoreVOList();
        Map<Long, String> storeMap = new HashMap<>();
        storeList.stream().forEach(store -> storeMap.put(store.getStoreId(), store.getStoreName()));
        return BaseResponse.success(storeMap);
    }

    /**
     * 店铺列表
     */
    @ApiOperation(value = "店铺列表")
    @RequestMapping(method = RequestMethod.GET)
    public BaseResponse<List<StoreBaseInfoResponse>> list() {
        ListStoreRequest queryRequest = new ListStoreRequest();
        queryRequest.setAuditState(CheckState.CHECKED);
        queryRequest.setStoreState(StoreState.OPENING);
        queryRequest.setGteContractStartDate(LocalDateTime.now());
        queryRequest.setLteContractEndDate(LocalDateTime.now());
        List<StoreBaseInfoResponse> list =
                storeQueryProvider.listStore(queryRequest).getContext().getStoreVOList().stream().map(s -> {
                    StoreBaseInfoResponse response = new StoreBaseInfoResponse().convertFromEntity(s);
                    return response;
                }).collect(Collectors.toList());
        return BaseResponse.success(list);
    }

    /**
     * 商家店铺列表，排除平台这条数据
     */
    @ApiOperation(value = "商家店铺列表")
    @RequestMapping(value = "/supplierStoreList", method = RequestMethod.GET)
    public BaseResponse<List<CompanyReponse>> supplierStoreList() {
        CompanyPageRequest request = new CompanyPageRequest();
        request.setDeleteFlag(DeleteFlag.NO);
        request.setStoreState(0);
        request.setAuditState(1);
        request.setPageSize(9999);
        BaseResponse<CompanyInfoPageResponse> pageCompanyInfo = companyInfoQueryProvider.pageCompanyInfo(request);
        List<CompanyReponse> collect = pageCompanyInfo.getContext().getCompanyInfoVOPage().stream().map(this::convertCompanyInfoVO).collect(Collectors.toList());
        return BaseResponse.success(collect);
    }

    private CompanyReponse convertCompanyInfoVO(CompanyInfoVO info) {
        CompanyReponse companyReponse = new CompanyReponse();
        companyReponse.setCompanyInfoId(info.getCompanyInfoId());
        companyReponse.setCompanyCode(info.getCompanyCodeNew());
        companyReponse.setCompanyType(info.getCompanyType());
        companyReponse.setSupplierName(info.getSupplierName());
        if (CollectionUtils.isNotEmpty(info.getEmployeeVOList())) {
            EmployeeVO employee = info.getEmployeeVOList().get(0);
            companyReponse.setAccountName(employee.getAccountName());
            companyReponse.setAccountState(employee.getAccountState());
            companyReponse.setAccountDisableReason(employee.getAccountDisableReason());
        }
        if (CollectionUtils.isNotEmpty(info.getStoreVOList())) {
            StoreVO store = info.getStoreVOList().get(0);
            companyReponse.setStoreId(store.getStoreId());
            companyReponse.setStoreName(store.getStoreName());
            companyReponse.setContractStartDate(store.getContractStartDate());
            companyReponse.setContractEndDate(store.getContractEndDate());
            companyReponse.setAuditState(store.getAuditState());
            companyReponse.setAuditReason(store.getAuditReason());
            companyReponse.setStoreState(store.getStoreState());
            companyReponse.setStoreClosedReason(store.getStoreClosedReason());
            companyReponse.setApplyEnterTime(store.getApplyEnterTime());
            companyReponse.setStoreType(store.getStoreType());
            companyReponse.setPileState(store.getPileState());
        }
        return companyReponse;
    }

    /**
     * 查询店铺信息
     *
     * @return
     */
    @ApiOperation(value = "查询店铺信息")
    @RequestMapping(value = "/storeInfo", method = RequestMethod.GET)
    public BaseResponse<StoreInfoResponse> queryStore() {
        return storeQueryProvider.getStoreInfoById(new StoreInfoByIdRequest(commonUtil
                .getStoreIdWithDefault()));
    }


    /**
     * 修改店铺运费计算方式
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "修改店铺运费计算方式")
    @RequestMapping(value = "/storeInfo/freightType", method = RequestMethod.PUT)
    public BaseResponse updateStoreFreightType(@RequestBody StoreSaveRequest request) {
        if (commonUtil.getStoreIdWithDefault() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        baseService.updateStoreFreightType(commonUtil.getStoreIdWithDefault(), request.getFreightTemplateType());
        operateLogMQUtil.convertAndSend("设置", "设置运费计算模式",
                "设置运费计算模式：" + (request.getFreightTemplateType().equals(DefaultFlag.YES) ? "单品运费" : "店铺运费"));
        return BaseResponse.SUCCESSFUL();
    }


    @ApiOperation(value = "更新商家囤货状态")
    @RequestMapping(value = "/updatePileState", method = RequestMethod.POST)
    public BaseResponse updatePileState(@RequestBody StorePlieRequest request) {
        storeProvider.updatePileState(request);
        //修改商家状态
        WalletByWalletIdAddRequest walletByWalletIdAddRequest = new WalletByWalletIdAddRequest();
        // 获取公司ID
        StoreByIdRequest store = new StoreByIdRequest();
        store.setStoreId(request.getStoreId());
        StoreVO storeVO = storeQueryProvider.getById(store).getContext().getStoreVO();
        EmployeeByCompanyIdRequest employeeByCompanyIdRequest = new EmployeeByCompanyIdRequest();
        employeeByCompanyIdRequest.setCompanyInfoId(storeVO.getCompanyInfo().getCompanyInfoId());
        EmployeeByCompanyIdResponse context = employeeQueryProvider.getByCompanyId(employeeByCompanyIdRequest).getContext();
        walletByWalletIdAddRequest.setCustomerAccount(context != null ? context.getAccountName():storeVO.getContactMobile());
        walletByWalletIdAddRequest.setJingBiState(request.getJingBiState());
        walletByWalletIdAddRequest.setMerchantFlag(true);
        walletByWalletIdAddRequest.setStoreId(String.valueOf(request.getStoreId()));
        customerWalletProvider.addUserWallet(walletByWalletIdAddRequest);
        List<StoreDeliveryAreaRequest> storeDeliveryAreaRequests = request.getStoreDeliveryAreaRequests();
        if(CollectionUtils.isNotEmpty(storeDeliveryAreaRequests)){
            FreightTemplateDeliveryAreaSaveListRequest listRequest = new FreightTemplateDeliveryAreaSaveListRequest();
            List<FreightTemplateDeliveryAreaSaveRequest> freightTemplateDeliveryAreaList = new ArrayList<>(storeDeliveryAreaRequests.size());
            storeDeliveryAreaRequests.forEach(d->{
                if (d.getDestinationType().toValue() != 5 || (d.getDestinationType().toValue()==5 && d.getOpenFlag()==1)) {
                    FreightTemplateDeliveryAreaSaveRequest saveRequest = KsBeanUtil.copyPropertiesThird(d,FreightTemplateDeliveryAreaSaveRequest.class);
                    saveRequest.setStoreId(storeVO.getStoreId());
                    freightTemplateDeliveryAreaList.add(saveRequest);
                }
            });
            listRequest.setFreightTemplateDeliveryAreaList(freightTemplateDeliveryAreaList);
            freightTemplateDeliveryAreaSaveProvider.saveOpenFlag(listRequest);
        }
        operateLogMQUtil.convertAndSend("商家", "更新商家囤货状态", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "修改商家自营状态")
    @RequestMapping(value = "/editSelfManage", method = RequestMethod.POST)
    public BaseResponse editSelfManage(@RequestBody StoreSupplierSelfManageEditRequest request) {
        storeProvider.editSelfManage(request);
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "修改商家指定位置排序")
    @RequestMapping(value = "/editAssignSort", method = RequestMethod.POST)
    public BaseResponse editAssignSort(@RequestBody StoreSupplierAssignSortEditRequest request) {
        storeProvider.editAssignSort(request);
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "更新商家预售状态")
    @RequestMapping(value = "/updatePresellState", method = RequestMethod.POST)
    public BaseResponse updatePresellState(@RequestBody StorePresellRequest request) {
        storeProvider.updatePresellState(request);
        return BaseResponse.SUCCESSFUL();
    }
}

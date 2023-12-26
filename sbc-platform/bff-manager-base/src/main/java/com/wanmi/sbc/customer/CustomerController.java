package com.wanmi.sbc.customer;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.aop.EmployeeCheck;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.enums.VASConstants;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.IteratorUtils;
import com.wanmi.sbc.customer.api.provider.account.CustomerAccountQueryProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.detail.CustomerDetailProvider;
import com.wanmi.sbc.customer.api.provider.detail.CustomerDetailQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.enterpriseinfo.EnterpriseInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.level.CustomerLevelQueryProvider;
import com.wanmi.sbc.customer.api.provider.parentcustomerrela.ParentCustomerRelaQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreCustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.storelevel.StoreLevelQueryProvider;
import com.wanmi.sbc.customer.api.request.CustomerEditRequest;
import com.wanmi.sbc.customer.api.request.account.CustomerAccountListRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoQueryRequest;
import com.wanmi.sbc.customer.api.request.customer.*;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailModifyRequest;
import com.wanmi.sbc.customer.api.request.detail.CustomerStateBatchModifyRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeAccountByIdRequest;
import com.wanmi.sbc.customer.api.request.enterpriseinfo.EnterpriseInfoByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.level.CustomerLevelByIdsRequest;
import com.wanmi.sbc.customer.api.request.level.CustomerLevelWithDefaultByIdRequest;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaListRequest;
import com.wanmi.sbc.customer.api.request.store.StoreCustomerQueryByEmployeeRequest;
import com.wanmi.sbc.customer.api.request.storelevel.StoreLevelByIdRequest;
import com.wanmi.sbc.customer.api.request.storelevel.StoreLevelListRequest;
import com.wanmi.sbc.customer.api.response.account.CustomerAccountListResponse;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoGetResponse;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetForSupplierResponse;
import com.wanmi.sbc.customer.api.response.customer.CustomerListByConditionResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeAccountByIdResponse;
import com.wanmi.sbc.customer.api.response.enterpriseinfo.EnterpriseInfoByCustomerIdResponse;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.ParentCustomerRelaListResponse;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.enums.CustomerStatus;
import com.wanmi.sbc.customer.bean.enums.CustomerType;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.vo.*;
import com.wanmi.sbc.customer.validator.CustomerValidator;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.HttpClientUtil;
import com.wanmi.sbc.util.HttpResult;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

/**
 * 会员
 * Created by CHENLI on 2017/4/19.
 */
@Api(tags = "CustomerController", description = "会员 Api")
@RestController
@Slf4j
public class CustomerController {

    @Autowired
    private CustomerProvider customerProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CustomerDetailProvider customerDetailProvider;

    @Autowired
    private CustomerDetailQueryProvider customerDetailQueryProvider;

    @Autowired
    private CustomerAccountQueryProvider customerAccountQueryProvider;

    @Autowired
    private CustomerLevelQueryProvider customerLevelQueryProvider;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private StoreLevelQueryProvider storeLevelQueryProvider;

    @Autowired
    private CustomerValidator customerValidator;

    @Autowired
    private StoreCustomerQueryProvider storeCustomerQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private EnterpriseInfoQueryProvider enterpriseInfoQueryProvider;

    @Autowired
    private ParentCustomerRelaQueryProvider parentCustomerRelaQueryProvider;

    @InitBinder
    public void initBinder(DataBinder binder) {
        if (binder.getTarget() instanceof CustomerEditRequest) {
            binder.setValidator(customerValidator);
        }
    }

    @Value("${d2p.erp-url}")
    public String pushCustomerUrl;

    /**
     * 多条件查询会员详细信息
     *
     * @param request
     * @return 会员详细信息
     */
    @ApiOperation(value = "多条件查询会员详细信息")
    @RequestMapping(value = "/customer/customerDetails", method = RequestMethod.POST)
    public List<CustomerDetailVO> findCustomerDetailList(@RequestBody CustomerDetailListByConditionRequest request) {
        return customerQueryProvider.listCustomerDetailByCondition(request).getContext().getDetailResponseList();
    }

    /**
     * 多条件查询会员信息
     *
     * @param request
     * @return 会员信息
     */
    @ApiOperation(value = "多条件查询会员信息")
    @RequestMapping(value = "/customerList", method = RequestMethod.POST)
    public List<CustomerVO> findCustomerList(@RequestBody CustomerListByConditionRequest request) {
        return customerQueryProvider.listCustomerByCondition(request).getContext().getCustomerVOList();
    }

    /**
     * 查询单条会员信息
     *
     * @param customerId
     * @return
     */
    @ApiOperation(value = "查询单条会员信息")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customerId", value = "会员ID", required = true)
    @RequestMapping(value = "/customer/{customerId}", method = RequestMethod.GET)
    public ResponseEntity<CustomerGetForSupplierResponse> findById(@PathVariable String customerId) {
        CustomerGetByIdResponse customer =
                customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(customerId)).getContext();
        CustomerGetForSupplierResponse customerResponse = new CustomerGetForSupplierResponse();
        BeanUtils.copyProperties(customer, customerResponse);
        CustomerDetailVO customerDetail = customerDetailQueryProvider.getCustomerDetailByCustomerId(
                CustomerDetailByCustomerIdRequest.builder()
                        .customerId(customerId).build()).getContext();
        customerResponse.getCustomerDetail().setBeaconStar(customerDetail.getBeaconStar());
        customerResponse.getCustomerDetail().setManagerName(customerDetail.getManagerName());
        if(Objects.nonNull(customerDetail) && StringUtils.isNotEmpty(customer.getUpdatePerson())){
            BaseResponse<EmployeeAccountByIdResponse> accountById = employeeQueryProvider.getAccountById(EmployeeAccountByIdRequest.builder().employeeId(customer.getUpdatePerson()).build());
            // log.info("CustomerDetailVO------------------>"+JSONObject.toJSONString(accountById.getContext()));
            customerResponse.getCustomerDetail().setUpdatePersonName(accountById.getContext().getAccountName());
        }

        if (customerResponse.getCustomerType() == CustomerType.SUPPLIER) {
            CompanyInfoQueryRequest request = new CompanyInfoQueryRequest();
            request.setCustomerId(customerId);

            CompanyInfoGetResponse companyInfo =
                    storeCustomerQueryProvider.getCompanyInfoBelongByCustomerId(request).getContext();
            customerResponse.setSupplierName(companyInfo.getSupplierName());
        }
        CustomerLevelVO customerLevel = customerLevelQueryProvider.getCustomerLevelWithDefaultById(
                CustomerLevelWithDefaultByIdRequest.builder().customerLevelId(customer.getCustomerLevelId()).build())
                .getContext();
        customerResponse.setCustomerLevelName(customerLevel.getCustomerLevelName());
        //查询企业信息
        if(commonUtil.findVASBuyOrNot(VASConstants.VAS_IEP_SETTING)){
            BaseResponse<EnterpriseInfoByCustomerIdResponse> enterpriseInfo = enterpriseInfoQueryProvider.getByCustomerId(EnterpriseInfoByCustomerIdRequest.builder()
                    .customerId(customerId)
                    .build());
            if(Objects.nonNull(enterpriseInfo.getContext())){
                customerResponse.setEnterpriseInfo(enterpriseInfo.getContext().getEnterpriseInfoVO());
            }
        }
        //查询会员关联的子账户信息
        if(EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())){
            CustomerListByConditionResponse response = customerQueryProvider
                    .queryChildsByParentId(new CustomerGetByIdRequest(customerId)).getContext();
            if(CollectionUtils.isNotEmpty(response.getCustomerVOList())){
                customerResponse.setChildCustomers(response.getCustomerVOList());
            }
        }
        return ResponseEntity.ok(customerResponse);
    }

    /**
     * 查询客户是否被删除了
     *
     * @param customerId
     * @return
     */
    @ApiOperation(value = "查询客户是否被删除了", notes = "true: 删除, false: 未删除")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customerId", value = "会员ID", required = true)
    @RequestMapping(value = "/customer/customerDelFlag/{customerId}", method = RequestMethod.GET)
    public ResponseEntity<Boolean> findCustomerDelFlag(@PathVariable String customerId) {
        return ResponseEntity.ok(customerQueryProvider.getCustomerDelFlag(new CustomerDelFlagGetRequest(customerId))
                .getContext().getDelFlag());
    }

    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customerId", value = "会员ID", required = true)
    @RequestMapping(value = "/customer/checkCustomerIsVip/{customerId}", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCustomerIsVip(@PathVariable String customerId) {
        return ResponseEntity.ok(customerQueryProvider.checkCustomerIsVip(new CustomerGetByIdRequest(customerId))
                .getContext());
    }

    /**
     * 审核客户状态
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "审核客户状态")
    @RequestMapping(value = "/customer/customerState", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> updateCheckState(@RequestBody CustomerCheckStateModifyRequest request) {
        if (null == request.getCheckState() || StringUtils.isEmpty(request.getCustomerId())) {
            throw new SbcRuntimeException("K-000009");
        }
        customerProvider.modifyCustomerCheckState(request);

        //获取会员
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                (request.getCustomerId())).getContext();
        if (nonNull(customer)) {
            if (request.getCheckState() == 1) {
                operateLogMQUtil.convertAndSend("客户", "审核客户", "审核客户：" + customer.getCustomerAccount());
            } else {
                operateLogMQUtil.convertAndSend("客户", "驳回客户", "驳回客户：" + customer.getCustomerAccount());
            }
        }
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**
     * 批量删除会员
     *
     * @param customerQueryRequest
     * @return
     */
    @ApiOperation(value = "批量删除会员")
    @RequestMapping(value = "/customer", method = RequestMethod.DELETE)
    public ResponseEntity<BaseResponse> delete(@RequestBody CustomersDeleteRequest customerQueryRequest) {
        if (CollectionUtils.isEmpty(customerQueryRequest.getCustomerIds())) {
            throw new SbcRuntimeException("K-000009");
        }
        customerProvider.deleteCustomers(customerQueryRequest);
        operateLogMQUtil.convertAndSend("会员", "批量删除会员", "操作成功");
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**
     * 批量启用/禁用会员详情
     *
     * @param queryRequest
     * @return
     */
    @ApiOperation(value = "批量启用/禁用会员详情")
    @RequestMapping(value = "/customer/detailState", method = RequestMethod.POST)
    public BaseResponse updateCustomerState(@RequestBody CustomerStateBatchModifyRequest queryRequest) {
        if (null == queryRequest.getCustomerStatus() || CollectionUtils.isEmpty(queryRequest.getCustomerIds())) {
            throw new SbcRuntimeException("K-000009");
        }

        Claims claims = (Claims) HttpUtil.getRequest().getAttribute("claims");
        //操作日志记录
        if (CustomerStatus.DISABLE.equals(queryRequest.getCustomerStatus())) {
            //获取会员
            CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                    (queryRequest.getCustomerIds().get(0))).getContext();
            if (nonNull(customer)) {
                String opContext = "禁用客户：";
                if (nonNull(claims)) {
                    String platform = Objects.toString(claims.get("platform"), "");
                    if (Platform.SUPPLIER.toValue().equals(platform)) {
                        opContext += "禁用客户：客户账号";
                    }
                }

                operateLogMQUtil.convertAndSend("客户", "禁用客户",
                        opContext + customer.getCustomerAccount());
            }
        } else {
            operateLogMQUtil.convertAndSend("客户", "批量启用客户", "批量启用客户");
        }

        //子账户也一同禁用和启用
        ParentCustomerRelaListResponse relaListResponse = parentCustomerRelaQueryProvider
                .list(ParentCustomerRelaListRequest.builder().parentIdList(queryRequest.getCustomerIds()).build()).getContext();
        if(Objects.nonNull(relaListResponse) && CollectionUtils.isNotEmpty(relaListResponse.getParentCustomerRelaVOList())){
            List<String> customerIds = relaListResponse.getParentCustomerRelaVOList().stream().map(ParentCustomerRelaVO::getCustomerId).collect(toList());
            queryRequest.getCustomerIds().addAll(customerIds);
        }

        return customerDetailProvider.modifyCustomerStateByCustomerId(
                CustomerStateBatchModifyRequest.builder()
                        .customerIds(queryRequest.getCustomerIds())
                        .customerStatus(queryRequest.getCustomerStatus())
                        .forbidReason(queryRequest.getForbidReason()).build()
        );
    }

    /**
     * 返现用户设置
     *
     * @param queryRequest
     * @return
     */
    @ApiOperation(value = "返现用户设置")
    @RequestMapping(value = "/customer/cashback", method = RequestMethod.POST)
    public BaseResponse updateCustomerCashBack(@RequestBody CustomerDetailModifyRequest queryRequest) {
        operateLogMQUtil.convertAndSend("会员", "返现用户设置",
                "返现用户设置：会员ID" + (StringUtils.isNotEmpty(queryRequest.getCustomerId()) ? queryRequest.getCustomerId() : ""));
        return customerDetailProvider.modifyCustomerCashBackByCustomerId(queryRequest);
    }

    /**
     * 根据customerId查询会员账号
     *
     * @param customerId customerId
     * @return 会员账号
     */
    @ApiOperation(value = "根据customerId查询会员账号")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customerId", value = "会员ID", required = true)
    @RequestMapping(value = "/customerAccounts/{customerId}", method = RequestMethod.GET)
    public ResponseEntity<List<CustomerAccountVO>> findCustomerAccountById(@PathVariable("customerId") String customerId) {
        CustomerAccountListRequest customerAccountListRequest = new CustomerAccountListRequest();
        customerAccountListRequest.setCustomerId(customerId);
        BaseResponse<CustomerAccountListResponse> customerAccountListResponseBaseResponse =
                customerAccountQueryProvider.listByCustomerId(customerAccountListRequest);
        CustomerAccountListResponse customerAccountListResponse = customerAccountListResponseBaseResponse.getContext();
        if (Objects.nonNull(customerAccountListResponse)) {
            return ResponseEntity.ok(customerAccountListResponse.getCustomerAccountVOList());
        }
        return ResponseEntity.ok(Collections.emptyList());
    }


    /**
     * 查询所有的有效的会员的id和accoutName，给前端autocomplete
     *
     * @return
     */
    @ApiOperation(value = "查询所有的有效的会员的id和accoutName，给前端autocomplete",
            notes = "customerId: 会员Id, customerAccount: 账号, customerName: 会员名称, customerLevelId: 等级Id")
    @EmployeeCheck
    @RequestMapping(value = "/customer/customerAccount/list", method = RequestMethod.POST)
    public ResponseEntity<List<Map<String, Object>>> findAllCustomers(@RequestBody StoreCustomerQueryByEmployeeRequest queryRequest) {
        if (StringUtils.isBlank(queryRequest.getCustomerAccount())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        return ResponseEntity.ok(getCustomerAccount(queryRequest.getCustomerAccount(), commonUtil.getOperatorId()));
    }

    /**
     * 查询所有的有效的会员的id和accoutName，给前端autocomplete
     *
     * @return
     */
    @ApiOperation(value = "查询所有的有效的会员的id和accoutName，给前端autocomplete",
            notes = "customerId: 会员Id, customerAccount: 账号, customerName: 会员名称, customerLevelId: 等级Id")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customerAccount", value = "账号",
            required = true)
    @RequestMapping(value = "/customer/list/{customerAccount}", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, Object>>> findAllCustomers(@PathVariable String customerAccount) {
        return ResponseEntity.ok(getCustomerAccount(customerAccount, null));
    }

    /**
     * 代客下单-搜索客户
     *
     * @param customerAccount 账号
     * @param employeeId      业务员
     * @return
     */
    private List<Map<String, Object>> getCustomerAccount(String customerAccount, String employeeId) {
        List<Map<String, Object>> collect = new ArrayList<>();
        //查询所有的合法的会员账户
        //非自营店铺查询店铺会员 自营店铺查询平台所有会员
        List<StoreCustomerVO> customerByCondition;
        //已审核
        StoreCustomerQueryByEmployeeRequest request = new StoreCustomerQueryByEmployeeRequest();
        request.setCustomerAccount(customerAccount);
        request.setEmployeeId(employeeId);
        request.setPageSize(5);
        if (commonUtil.getCompanyType() == CompanyType.SUPPLIER.toValue()) {
            request.setStoreId(commonUtil.getStoreId());

            customerByCondition = storeCustomerQueryProvider.listCustomer(request).getContext().getStoreCustomerVOList();
        } else {
            customerByCondition = storeCustomerQueryProvider.listBossCustomer(request).getContext().getStoreCustomerVOList();
        }

        if (CollectionUtils.isEmpty(customerByCondition)) {
            return collect;
        }

        collect = customerByCondition.stream()
                .map(v -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("customerId", v.getCustomerId());
                    map.put("customerAccount", v.getCustomerAccount());
                    map.put("customerName", v.getCustomerName());
                    map.put("customerLevelId", v.getCustomerLevelId());
                    return map;
                }).collect(toList());

        List<Long> levelIds = collect.stream().filter(v -> v.get("customerLevelId") != null).map(v -> (Long) v.get(
                "customerLevelId")).collect(toList());
        if (levelIds != null && !levelIds.isEmpty()) {
            List<CustomerLevelVO> customerLevels = customerLevelQueryProvider.listCustomerLevelByIds(
                    CustomerLevelByIdsRequest.builder().customerLevelIds(levelIds).build()).getContext().getCustomerLevelVOList();
            IteratorUtils.zip(collect, customerLevels
                    , (collect1, levels1) -> collect1.get("customerLevelId") != null && collect1.get("customerLevelId"
                    ).equals(levels1.getCustomerLevelId())
                    , (collect2, levels2) -> {
                        if (levels2.getCustomerLevelName() != null) {
                            collect2.put("customerLevelName", levels2.getCustomerLevelName());
                        }
                    }
            );

            if (commonUtil.getCompanyType() == CompanyType.SUPPLIER.toValue()) {
                List<StoreLevelVO> storeLevels = storeLevelQueryProvider.list(StoreLevelListRequest.builder()
                        .storeLevelIdList(levelIds)
                        .build()).getContext().getStoreLevelVOList();

                IteratorUtils.zip(collect, storeLevels,
                        (collect1, levels1) -> Objects.nonNull(collect1.get("customerLevelId")) &&
                                collect1.get("customerLevelId").equals(levels1.getStoreLevelId()),
                        (collect2, levels2) -> {
                            collect2.put("customerLevelName", levels2.getLevelName());
                        }
                );
            }
        }
        return collect;
    }

    /**
     * 根据客户ID查询相关信息，编辑代客下单用
     * add Transactional的意思是为了hibernate懒加载，后期重构要放到service
     *
     * @param customerId
     * @return
     */
    @ApiOperation(value = "根据客户ID查询相关信息，编辑代客下单用",
            notes = "customerId: 用户Id, customerAccount: 账号, customerName: 会员名称, customerLevelName: 等级名称")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customerId", value = "会员ID", required = true)
    @RequestMapping(value = "/customer/single/{customerId}", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<Map<String, String>> findCustomerById(@PathVariable("customerId") String customerId) {
        Map<String, String> resultMap = new HashMap<>();
        CustomerGetByIdResponse customer =
                customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(customerId)).getContext();
        String companyInfoId = commonUtil.getOperator().getAdminId();
        if (null != customer && nonNull(companyInfoId)) {
            resultMap.put("customerId", customer.getCustomerId());
            resultMap.put("customerAccount", customer.getCustomerAccount());
            resultMap.put("customerName", customer.getCustomerDetail().getCustomerName());
            List<Long> levelIds = customer.getStoreCustomerRelaListByAll()
                    .parallelStream()
                    .filter(storeCustomerRela -> companyInfoId.equals(storeCustomerRela.getCompanyInfoId().toString()))
                    .map(StoreCustomerRelaVO::getStoreLevelId)
                    .collect(toList());
            // 非自营店铺
            if (CollectionUtils.isNotEmpty(levelIds)) {
                StoreLevelVO storeLevelVO = storeLevelQueryProvider.getById(StoreLevelByIdRequest.builder().storeLevelId(levelIds.get(0)).build()).getContext().getStoreLevelVO();
                resultMap.put("customerLevelName", storeLevelVO.getLevelName());
            } else {// 自营店铺
                CustomerLevelVO customerLevel = customerLevelQueryProvider.getCustomerLevelWithDefaultById(
                        CustomerLevelWithDefaultByIdRequest.builder().customerLevelId(customer.getCustomerLevelId()).build()).getContext();
                resultMap.put("customerLevelName", customerLevel.getCustomerLevelName());
            }
        }
        return ResponseEntity.ok(resultMap);
    }


    /**
     * 查询所有的有效的会员的id和accoutName，给前端autocomplete
     *
     * @return
     */
    @ApiOperation(value = "查询所有的有效的会员的id和accoutName，给前端autocomplete",
            notes = "customerId: 用户Id, customerAccount: 账号, customerName: 会员名称")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customerId", value = "会员ID", required = true)
    @RequestMapping(value = "/customer/all/{customerAccount}", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, Object>>> autoCustomerInfo(@PathVariable String customerAccount) {
        List<Map<String, Object>> collect = new ArrayList<>();
        //查询所有的合法的会员账户
        CustomerDetailListForOrderRequest cqr = new CustomerDetailListForOrderRequest();
        //已审核
        cqr.setCheckState(CheckState.CHECKED.toValue());
        cqr.setCustomerStatus(CustomerStatus.ENABLE);
        cqr.setCustomerAccount(customerAccount);
//        List<CustomerDetail> customerByCondition = customerService.findDetailForOrder(cqr);
        List<CustomerDetailVO> customerByCondition = customerQueryProvider.listCustomerDetailForOrder(cqr).getContext()
                .getDetailResponseList();

        if (CollectionUtils.isEmpty(customerByCondition)) {
            return ResponseEntity.ok(collect);
        }

        collect = customerByCondition.stream()
                .map(v -> {
                    CustomerGetByIdRequest request = new CustomerGetByIdRequest();
                    request.setCustomerId(v.getCustomerId());
                    String account = customerQueryProvider.getCustomerById(request).getContext().getCustomerAccount();
                    Map<String, Object> map = new HashMap<>();
                    map.put("customerId", v.getCustomerId());
                    map.put("customerAccount", account);
                    map.put("customerName", v.getCustomerName());
                    return map;
                }).collect(toList());

        return ResponseEntity.ok(collect);
    }


    /**
     * 批量校验会员是否被绑定
     * @param request
     * @return
     */
    @ApiModelProperty(value = "批量校验会员是否被绑定")
    @RequestMapping(value = "/customer/batch/validateBindStatus", method = RequestMethod.POST)
    public BaseResponse<List<String>> batchValidateCustomerBindStatus(@RequestBody @Valid CustomerValidateByIdsRequest request){
        return customerQueryProvider.batchValidateCustomerBindStatus(request);
    }

    /**
     * 校验会员是否被绑定
     * @param request
     * @return
     */
    @ApiModelProperty(value = "校验会员是否被绑定")
    @RequestMapping(value = "/customer/validateBindStatus", method = RequestMethod.POST)
    public BaseResponse validateCustomerBindStatus(@RequestBody @Valid CustomerValidateByIdRequest request){
        operateLogMQUtil.convertAndSend("会员", "校验会员是否被绑定",
                "校验会员是否被绑定：会员ID" + (StringUtils.isNotEmpty(request.getCustomerId()) ? request.getCustomerId() : ""));
        return customerQueryProvider.validateCustomerBindStatus(request);
    }

    /**
     * 解除会员的绑定关系绑定
     * @param request
     * @return
     */
    @ApiModelProperty(value = "解除会员的绑定关系绑定")
    @RequestMapping(value = "/customer/releaseBind", method = RequestMethod.POST)
    public BaseResponse releaseBindCustomer(@RequestBody @Valid CustomerReleaseByIdRequest request){
        operateLogMQUtil.convertAndSend("会员", "解除会员的绑定关系绑定",
                "解除会员的绑定关系绑定：会员ID" + (StringUtils.isNotEmpty(request.getCustomerId()) ? request.getCustomerId() : ""));
        return customerProvider.releaseBindCustomers(request);
    }

    /**
     * 校验社会信用代码
     * @param request
     * @return
     */
    @ApiModelProperty(value = "校验社会信用代码")
    @RequestMapping(value = "/customer/verifySocialCode", method = RequestMethod.POST)
    public BaseResponse verifySocialCode(@RequestBody @Valid CustomerSocialCodeValidateRequest request){
        operateLogMQUtil.convertAndSend("会员", "校验社会信用代码",
                "校验社会信用代码：统一社会信用代码" + (StringUtils.isNotEmpty(request.getSocialCreditCode()) ? request.getSocialCreditCode() : ""));
        return customerQueryProvider.verifySocialCode(request);
    }

    /**
     * 手动同步至erp
     *
     * @param customerId
     * @return
     */
    @ApiOperation(value = "手动同步至erp", notes = "true: 删除, false: 未删除")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customerId", value = "会员ID", required = true)
    @RequestMapping(value = "/customer/async/{customerId}", method = RequestMethod.GET)
    public BaseResponse asyncCustomerToErp(@PathVariable String customerId){
        //获取会员
        CustomerVO customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(customerId)).getContext();
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("customerType","TB");
        requestMap.put("customerErpId",customer.getCustomerErpId());
        requestMap.put("customerDescr1",customer.getCustomerAccount());
        HttpResult result = HttpClientUtil.post(pushCustomerUrl,requestMap);
        operateLogMQUtil.convertAndSend("会员", "手动同步至erp",
                "手动同步至erp：会员ID" + (StringUtils.isNotEmpty(customerId) ? customerId : ""));
        return this.afterDelResult(customer,result);
    }

    @Async
    public BaseResponse afterDelResult(CustomerVO customer , HttpResult result){
        if(Objects.nonNull(result) && "200".equals(result.getResultCode())){
            if(!org.springframework.util.StringUtils.isEmpty(result.getResultData())){
                BaseResponse response = JSONObject.parseObject(result.getResultData(), BaseResponse.class);
                if(CommonErrorCode.SUCCESSFUL.equals(response.getCode())){
                    customerProvider.asyncErpFlag(CustomerSynFlagRequest.builder()
                            .customerAccount(customer.getCustomerAccount())
                            .build());
                    return BaseResponse.SUCCESSFUL();
                }
            }
        }
        return BaseResponse.FAILED();
    }
}

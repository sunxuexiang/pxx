package com.wanmi.sbc.customer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.account.api.request.wallet.InitiateWithdrawalWithoutCheckRequest;
import com.wanmi.sbc.aop.EmployeeCheck;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.SecurityUtil;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.customer.api.provider.customer.CustomerProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.detail.CustomerDetailProvider;
import com.wanmi.sbc.customer.api.provider.detail.CustomerDetailQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.growthvalue.CustomerGrowthValueQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreCustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.workorder.WorkOrderQueryProvider;
import com.wanmi.sbc.customer.api.request.CustomerEditRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoQueryRequest;
import com.wanmi.sbc.customer.api.request.customer.*;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailModifyRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeListRequest;
import com.wanmi.sbc.customer.api.request.growthvalue.CustomerGrowthValuePageRequest;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaAddRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyInfoGetResponse;
import com.wanmi.sbc.customer.api.response.customer.*;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.CustomerMergeFlagResponse;
import com.wanmi.sbc.customer.bean.enums.CustomerStatus;
import com.wanmi.sbc.customer.bean.enums.CustomerType;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailForPageVO;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import com.wanmi.sbc.customer.bean.vo.CustomerGrowthValueVO;
import com.wanmi.sbc.customer.bean.vo.EmployeeListVO;
import com.wanmi.sbc.customer.validator.CustomerValidator;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.live.api.provider.host.LiveHostProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.returnorder.ReturnOrderModifyEmployeeIdRequest;
import com.wanmi.sbc.order.api.request.trade.TradeByCustomerIdRequest;
import com.wanmi.sbc.order.api.request.trade.TradeUpdateEmployeeIdRequest;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 会员
 * Created by hht on 2017/4/19.
 */
@Api(description = "平台会员API", tags = "BossCustomerController")
@RestController
@RequestMapping(value = "/customer")
@Slf4j
public class BossCustomerController {

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CustomerProvider customerProvider;

    @Autowired
    private CustomerValidator customerValidator;

    @Autowired
    private StoreCustomerQueryProvider storeCustomerQueryProvider;

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private ReturnOrderProvider returnOrderProvider;

    @Autowired
    private CustomerDetailQueryProvider customerDetailQueryProvider;

    @Autowired
    private CustomerDetailProvider customerDetailProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private CustomerGrowthValueQueryProvider customerGrowthValueQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private WorkOrderQueryProvider workOrderQueryProvider;

    @Autowired
    private LiveHostProvider liveHostProvider;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private CustomerWalletProvider customerWalletProvider;

    @InitBinder
    public void initBinder(DataBinder binder) {
        if (binder.getTarget() instanceof CustomerEditRequest) {
            binder.setValidator(customerValidator);
        }
    }

    @ApiOperation(value = "修改账户密码")
    @RequestMapping(value = "/modifyPassword", method = RequestMethod.POST)
    public BaseResponse modifyPassword(@RequestBody ModifyPwdRequest request) {
        List<String> customerIds = request.getCustomerIds();

        Map<String, String> objectObjectHashMap = Maps.newHashMap();
        for (String customerId : customerIds) {
            NoDeleteCustomerGetByAccountRequest noDeleteCustomerGetByAccountRequest = new NoDeleteCustomerGetByAccountRequest();
            noDeleteCustomerGetByAccountRequest.setCustomerAccount(customerId);
            BaseResponse<NoDeleteCustomerGetByAccountResponse> noDeleteCustomerByAccount = customerQueryProvider.getNoDeleteCustomerByAccount(noDeleteCustomerGetByAccountRequest);
            NoDeleteCustomerGetByAccountResponse customer = noDeleteCustomerByAccount.getContext();
            if(Objects.isNull(customer)){
                continue;
            }
            String encryptPwd = SecurityUtil.getStoreLogpwd(String.valueOf(customer.getCustomerId()), "123456", customer.getCustomerSaltVal());
            objectObjectHashMap.put(customerId, encryptPwd);
        }
        //操作日志记录
        operateLogMQUtil.convertAndSend("平台会员", "修改账户密码","修改账户密码");
        return BaseResponse.success(JSON.toJSONString(objectObjectHashMap));
    }

    @ApiOperation(value = "移动端发起提现")
    @RequestMapping(value = "/initiateWithdrawalWhithoutCheck", method = RequestMethod.POST)
    public BaseResponse initiateWithdrawalWhithoutCheck(@RequestBody InitiateWithdrawalWithoutCheckRequest request) {
        BaseResponse baseResponse = null;
        DisableCustomerDetailGetByAccountRequest disableCustomerDetailGetByAccountRequest = new DisableCustomerDetailGetByAccountRequest();
        disableCustomerDetailGetByAccountRequest.setCustomerAccount(request.getCustomerPhone());
        BaseResponse<DisableCustomerDetailGetByAccountResponse> customerDetailByAccount = customerQueryProvider.getCustomerDetailByAccount(disableCustomerDetailGetByAccountRequest);

        DisableCustomerDetailGetByAccountResponse disableCustomerDetailGetByAccountResponse = Optional.ofNullable(customerDetailByAccount).map(BaseResponse::getContext).orElse(null);
        if(Objects.isNull(disableCustomerDetailGetByAccountResponse)){
            throw new SbcRuntimeException("参数错误");
        }
        String customerId = disableCustomerDetailGetByAccountResponse.getCustomerId();
        if(Objects.isNull(customerId)){
            throw new SbcRuntimeException("参数错误");
        }
        Exception exception = null;
        RLock rLock = redissonClient.getFairLock(customerId);
        rLock.lock();
        try {
            request.setCustomerId(customerId);
            //根据逻辑,先保存
            baseResponse = customerWalletProvider.initiateWithdrawalWhthoutCheck(request);

        } catch (Exception e) {
            exception = e;
        } finally {
            rLock.unlock();
        }
        if (exception != null) {
            throw new SbcRuntimeException(exception);
        }
        //操作日志记录
        operateLogMQUtil.convertAndSend("平台会员", "移动端发起提现","操作成功");
        return baseResponse;
    }

    @Autowired
    EmployeeQueryProvider employeeQueryProvider;

    /**
     * S2b-Boss端修改会员
     * 修改会员表，修改会员详细信息
     *
     * @return
     */
    @ApiOperation(value = "平台端修改会员")
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse> updateCustomerAll(@RequestBody CustomerModifyRequest customerModifyRequest) {
        String employeeId = commonUtil.getOperatorId();
        customerModifyRequest.setOperator(employeeId);
        //获取原业务员
        CustomerDetailVO detail = customerDetailQueryProvider.getCustomerDetailByCustomerId(
                CustomerDetailByCustomerIdRequest.builder().customerId(customerModifyRequest.getCustomerId()).build()
        ).getContext();
        String oldEmployeeId = Objects.nonNull(detail) ? detail.getEmployeeId() : null;
        customerProvider.modifyCustomer(customerModifyRequest);
        //如更换业务员，将历史订单和历史退单的负责业务员更新为新业务员
        if (StringUtils.isNotBlank(customerModifyRequest.getEmployeeId()) && (!customerModifyRequest.getEmployeeId().equals(oldEmployeeId))) {
            tradeProvider.updateEmployeeId(TradeUpdateEmployeeIdRequest.builder()
                    .employeeId(customerModifyRequest.getEmployeeId())
                    .customerId(customerModifyRequest.getCustomerId())
                    .build());
            returnOrderProvider.modifyEmployeeId(ReturnOrderModifyEmployeeIdRequest.builder()
                    .employeeId(customerModifyRequest.getEmployeeId())
                    .customerId(customerModifyRequest.getCustomerId()).build());
        }

        boolean managerChanged = !StringUtils.equals(detail.getManagerId(), customerModifyRequest.getManagerId());
        String managerChangedLog = "";
        if (managerChanged) {
            EmployeeListRequest employeeListRequest = new EmployeeListRequest();
            employeeListRequest.setEmployeeIds(Arrays.asList(detail.getManagerId(), customerModifyRequest.getManagerId()));
            Map<String, String> empNameByIdMap = employeeQueryProvider.list(employeeListRequest).getContext().getEmployeeList().stream()
                    .collect(Collectors.toMap(EmployeeListVO::getEmployeeId, EmployeeListVO::getEmployeeName, (k1, k2) -> k1));
            managerChangedLog = "，白鲸管家变更：" + empNameByIdMap.getOrDefault(detail.getManagerId(), "")
                    + "->" + empNameByIdMap.getOrDefault(customerModifyRequest.getManagerId(), "");
        }

        //操作日志记录
        operateLogMQUtil.convertAndSend("客户", "编辑客户",
                "编辑客户：" + customerModifyRequest.getCustomerAccount() + managerChangedLog);
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }


    /**
     * 分页查询会员
     *
     * @param customerDetailQueryRequest
     * @return 会员信息
     */
    @ApiOperation(value = "分页查询会员")
    @EmployeeCheck
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> page(@RequestBody CustomerDetailPageRequest customerDetailQueryRequest) {
        customerDetailQueryRequest.putSort("createTime", SortType.DESC.toValue());
        CustomerDetailPageResponse response = customerQueryProvider.page(customerDetailQueryRequest).getContext();
        //封装客户最新下单时间
        List<CustomerDetailForPageVO> detailResponseList = response.getDetailResponseList();
        List<String> customerIds = detailResponseList.stream().map(CustomerDetailForPageVO::getCustomerId).collect(Collectors.toList());
        Map<String, String> lastPayOrderTimeMap = tradeQueryProvider.getOrderTimeByCustomerIds(
                TradeByCustomerIdRequest.builder().customerIds(customerIds).build()).getContext();
        detailResponseList.forEach(customer ->{
            if (Objects.nonNull(lastPayOrderTimeMap.get(customer.getCustomerId()))){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                customer.setLastPayOrderTime(LocalDateTime.parse(lastPayOrderTimeMap.get(customer.getCustomerId()), formatter));
            }
        });

        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /**
     * 导出商品列表
     *
     * @param encrypted
     * @param response
     */
    @ApiOperation(value = "导出会员")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted", value = "加密", required = true)
    @RequestMapping(value = "/export/params/{encrypted}", method = RequestMethod.GET)
    public void exportByParams(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));

        CustomerDetailPageRequest request = JSON.parseObject(decrypted, CustomerDetailPageRequest.class);
        request.putSort("createTime", SortType.DESC.toValue());

        JSONObject jsonObject = JSON.parseObject(decrypted);
        Integer beaconStar = jsonObject.getInteger("beaconStar");
        if (Objects.nonNull(beaconStar)) {
            request.setBeaconStar(DefaultFlag.fromValue(beaconStar));
        }
        Integer customerStatus = jsonObject.getInteger("customerStatus");
        if (Objects.nonNull(customerStatus)) {
            request.setCustomerStatus(CustomerStatus.DISABLE.fromValue(customerStatus));
        }


        request.setPageNum(0);
        request.setPageSize(3000);

        CustomerDetailPageResponse customerResponse = customerQueryProvider.page(request).getContext();

        //封装客户最新下单时间
        List<CustomerDetailForPageVO> detailResponseList = customerResponse.getDetailResponseList();
        List<String> customerIds = detailResponseList.stream().map(CustomerDetailForPageVO::getCustomerId).collect(Collectors.toList());
        Map<String, String> lastPayOrderTimeMap = tradeQueryProvider.getOrderTimeByCustomerIds(
                TradeByCustomerIdRequest.builder().customerIds(customerIds).build()).getContext();
        detailResponseList.forEach(customer ->{
            if (Objects.nonNull(lastPayOrderTimeMap.get(customer.getCustomerId()))){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                customer.setLastPayOrderTime(LocalDateTime.parse(lastPayOrderTimeMap.get(customer.getCustomerId()), formatter));
            }
        });

        String headerKey = "Content-Disposition";
        LocalDateTime dateTime = LocalDateTime.now();
        String fileName = String.format("客户导出_%s.xls", dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("/customer/export/params, fileName={}, error={}", fileName, e);
        }
        String headerValue = String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName);
        response.setHeader(headerKey, headerValue);
        try {
            export(customerResponse.getDetailResponseList(), response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new SbcRuntimeException(e);
        }
        //操作日志记录
        operateLogMQUtil.convertAndSend("平台会员", "导出会员","操作成功");
    }

    private void export(List<CustomerDetailForPageVO> list, ServletOutputStream outputStream) {
        // 1、导出表单字段：客户名称、账号、账号状态、注册时间、最近登录时间、最近支付订单时间、业务代表、白鲸管家
        ExcelHelper<CustomerDetailForPageVO> excelHelper = new ExcelHelper<>();
        excelHelper.addSheet(
                "客户列表",
                new Column[]{
                        new Column("客户名称", new SpelColumnRender<GoodsInfoVO>("contactName")),
                        new Column("账号", new SpelColumnRender<GoodsInfoVO>("customerAccount")),
                        new Column("账号状态", (cell, object) -> {
                            CustomerStatus customerStatus = ((CustomerDetailForPageVO) object).getCustomerStatus();
                            if(Objects.nonNull(customerStatus)){
                                cell.setCellValue(customerStatus.toValue() == 0 ? "启用中" : "禁用中");
                            }else{
                                cell.setCellValue("");
                            }
                        }),
                        new Column("注册时间", (cell, object) -> {
                            LocalDateTime createTime = ((CustomerDetailForPageVO) object).getCreateTime();
                            if(Objects.nonNull(createTime)){
                                cell.setCellValue(DateUtil.format(createTime, DateUtil.FMT_TIME_1));
                            }else{
                                cell.setCellValue("");
                            }
                        }),

                        new Column("最近登录时间", (cell, object) -> {
                            LocalDateTime time = ((CustomerDetailForPageVO) object).getLastLoginTime();
                            if(Objects.nonNull(time)){
                                cell.setCellValue(DateUtil.format(time, DateUtil.FMT_TIME_1));
                            }else{
                                cell.setCellValue("");
                            }
                        }),

                        new Column("最近支付订单时间", (cell, object) -> {
                            LocalDateTime time = ((CustomerDetailForPageVO) object).getLastPayOrderTime();
                            if(Objects.nonNull(time)){
                                cell.setCellValue(DateUtil.format(time, DateUtil.FMT_TIME_1));
                            }else{
                                cell.setCellValue("");
                            }
                        }),

                        new Column("业务代表", (cell, object) -> {
                            String employeeName = ((CustomerDetailForPageVO) object).getEmployeeName();
                            if(StringUtils.isNotBlank(employeeName)){
                                cell.setCellValue(employeeName);
                            }else{
                                cell.setCellValue("");
                            }
                        }),
                        new Column("白鲸管家", (cell, object) -> {
                            String managerName = ((CustomerDetailForPageVO) object).getManagerName();
                            if(StringUtils.isNotBlank(managerName)){
                                cell.setCellValue(managerName);
                            }else{
                                cell.setCellValue("");
                            }
                        }),
                },
                list
        );
        excelHelper.write(outputStream);
    }

    /**
     * 分页查询会员
     *
     * @param customerDetailQueryRequest
     * @return 会员信息
     */
    @ApiOperation(value = "分页查询直播账号")
    @EmployeeCheck
    @RequestMapping(value = "/pageLive", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> pageLive(@RequestBody CustomerDetailPageRequest customerDetailQueryRequest) {
        customerDetailQueryRequest.putSort("createTime", SortType.DESC.toValue());
        customerDetailQueryRequest.setIsLive(1);
        CustomerDetailPageResponse response = customerQueryProvider.page(customerDetailQueryRequest).getContext();
        List<String> enableCustomerAccountList = liveHostProvider.getEnableCustomerAccountList().getContext();

        //过滤已使用的账户
        if(response.getDetailResponseList()!=null && enableCustomerAccountList!=null){
            List<CustomerDetailForPageVO> list= Lists.newArrayList();
            response.getDetailResponseList().stream().forEach(item -> {
                //不存在
                if(enableCustomerAccountList.contains(item.getCustomerId())){
                    item.setMyCustomer(true);
                }
            });
        }
        return ResponseEntity.ok(BaseResponse.success(response));
    }


    /**
     * 分页查询会员
     *
     * @param customerDetailQueryRequest
     * @return 会员信息
     */
    @ApiOperation(value = "分页查询会员")
    @EmployeeCheck
    @RequestMapping(value = "/pageByChild", method = RequestMethod.POST)
    public BaseResponse<CustomerDetailListPageResponse> pageByChild(@RequestBody CustomerDetailListByConditionRequest customerDetailQueryRequest) {
        if (Objects.isNull(customerDetailQueryRequest.getCustomerId())){
            throw new SbcRuntimeException(CommonErrorCode.FAILED,"未知的会员");
        }
        return customerQueryProvider.pageQueryChildAccountByCondition(customerDetailQueryRequest);
    }

    /**
     * 验证是否处在工单内
     *
     * @param customerDetailQueryRequest
     * @return 会员信息
     */
    @ApiOperation(value = "验证是否处在工单内")
    @EmployeeCheck
    @RequestMapping(value = "/checkMegerFlag", method = RequestMethod.POST)
    public BaseResponse<CustomerMergeFlagResponse> checkMegerFlag(@RequestBody @Valid ParentCustomerRelaAddRequest customerDetailQueryRequest) {
        //操作日志记录
        operateLogMQUtil.convertAndSend("平台会员", "验证是否处在工单内","验证是否处在工单内");
        return workOrderQueryProvider.checkMegerFlag(customerDetailQueryRequest);
    }

    /**
     * Boss端保存会员
     *
     * @return
     */
    @ApiOperation(value = "平台端保存会员")
    @EmployeeCheck
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> addCustomerAll(@Valid @RequestBody CustomerAddRequest customerAddRequest,
                                                       HttpServletRequest request) {
        //账号已存在
        NoDeleteCustomerGetByAccountResponse customer = customerQueryProvider.getNoDeleteCustomerByAccount(new NoDeleteCustomerGetByAccountRequest
                (customerAddRequest.getCustomerAccount())).getContext();
        if (customer != null) {
            throw new SbcRuntimeException("K-010002");
        }
        String employeeId = ((Claims) request.getAttribute("claims")).get("employeeId").toString();
        customerAddRequest.setOperator(employeeId);
        customerAddRequest.setCustomerType(CustomerType.PLATFORM);
        customerProvider.saveCustomer(customerAddRequest);

        //操作日志记录
        operateLogMQUtil.convertAndSend("客户", "新增客户",
                "新增客户：" + customerAddRequest.getCustomerAccount());
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }


    /**
     * 获取客户归属商家的商家名称
     *
     * @param customerId
     * @return
     */
    @ApiOperation(value = "获取客户归属商家的商家名称")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "customerId", value = "客户id", required = true)
    @RequestMapping(value = "/supplier/name/{customerId}", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse<String>> getBelongSupplier(@PathVariable("customerId") String customerId) {
        CompanyInfoQueryRequest request = new CompanyInfoQueryRequest();
        request.setCustomerId(customerId);
        CompanyInfoGetResponse companyInfo = storeCustomerQueryProvider.getCompanyInfoBelongByCustomerId(request).getContext();
        return ResponseEntity.ok(BaseResponse.success(companyInfo.getSupplierName()));
    }

    @ApiOperation(value = "分页查询会员成长值")
    @RequestMapping(value = "/queryToGrowthValue", method = RequestMethod.POST)
    public ResponseEntity<MicroServicePage<CustomerGrowthValueVO>> queryGrowthValue(@RequestBody CustomerGrowthValuePageRequest customerGrowthValuePageRequest) {
        return ResponseEntity.ok(customerGrowthValueQueryProvider.page(customerGrowthValuePageRequest).getContext()
                .getCustomerGrowthValueVOPage());
    }

    @ApiOperation(value = "编辑会员标签")
    @RequestMapping(value = "/modifyTag", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse> modifyTag(@RequestBody CustomerModifyRequest request) {
        customerProvider.modifyCustomerTag(request);
        //操作日志记录
        operateLogMQUtil.convertAndSend("会员", "编辑会员标签","操作成功");
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }


    /**
     * 修改会员的企业信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/modifyEnterpriseInfo", method = RequestMethod.POST)
    public BaseResponse<CustomerGetByIdResponse> modifyEnterpriseInfo(@RequestBody @Valid CustomerEnterpriseRequest request){
        //操作日志记录
        operateLogMQUtil.convertAndSend("会员", "修改会员的企业信息","修改会员的企业信息");
        return customerProvider.modifyEnterpriseInfo(request);
    }

    @ApiOperation(value = "编辑是否大客户")
    @RequestMapping(value = "/modifyVipFlag", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse> modifyVipFlag(@RequestBody CustomerModifyRequest request){
        customerProvider.modifyVipFlag(request);
        //操作日志记录
        operateLogMQUtil.convertAndSend("会员", "编辑是否大客户","操作成功");
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    @ApiOperation(value = "编辑是否是直播账号")
    @RequestMapping(value = "/modifyIsLive", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse> modifyIsLive(@RequestBody CustomerDetailModifyRequest request){
        customerDetailProvider.modifyCustomerLiveByCustomerId(request);
        //操作日志记录
        operateLogMQUtil.convertAndSend("会员", "编辑是否是直播账号","操作成功");
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

}

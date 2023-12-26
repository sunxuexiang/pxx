package com.wanmi.sbc.wallet;


import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.wanmi.sbc.account.api.provider.wallet.TicketsFormProvider;
import com.wanmi.sbc.account.api.request.wallet.CreateRechargeRequest;
import com.wanmi.sbc.account.api.request.wallet.TicketsFormAdoptRequest;
import com.wanmi.sbc.account.api.request.wallet.TicketsFormPageRequest;
import com.wanmi.sbc.account.api.response.wallet.TicketsFormListResponse;
import com.wanmi.sbc.account.api.response.wallet.TicketsFormQueryResponse;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.account.bean.vo.TicketsFormLogVo;
import com.wanmi.sbc.account.bean.vo.TicketsFormQueryVO;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.RoleInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByAccountNameRequest;
import com.wanmi.sbc.customer.bean.vo.EmployeeVO;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.request.trade.PickGoodsRequest;
import com.wanmi.sbc.order.api.request.trade.TicketsFormPushPayOrderKingdeeRequest;
import com.wanmi.sbc.order.response.StatisticRecordItemPriceNumNoPileUserExcel;
import com.wanmi.sbc.pay.api.provider.PayWithDrawProvider;
import com.wanmi.sbc.pay.api.response.PayWithDrawResponse;
import com.wanmi.sbc.pay.bean.dto.PayWithDrawIdDTO;
import com.wanmi.sbc.pay.bean.vo.PayWithDrawVO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import com.wanmi.sbc.wallet.response.TicketsFormExcel;
import io.jsonwebtoken.impl.DefaultClaims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.runtime.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Api(description = "工单控制器", tags = "TicketsFormController")
@RestController
@RequestMapping(value = "/ticketsForm")
@Slf4j
public class TicketsFormController {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    TicketsFormProvider ticketsFormProvider;

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    OperateLogMQUtil operateLogMQUtil;

    @Autowired
    EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    CommonUtil commonUtil;

    @Autowired
    RoleInfoQueryProvider roleInfoQueryProvider;

    @Autowired
    RedisService redisService;

    @Autowired
    StoreQueryProvider storeQueryProvider;

    @Autowired
    private PayWithDrawProvider payWithDrawProvider;

    @ApiOperation(value = "工单列表")
    @RequestMapping(value = "/ticketsFormAllList", method = RequestMethod.POST)
    public BaseResponse<TicketsFormQueryResponse> ticketsFormAllList(@RequestBody TicketsFormPageRequest ticketsFormPageRequest) {
        BaseResponse<TicketsFormQueryResponse> response = ticketsFormProvider.ticketsFormAllList(ticketsFormPageRequest);
        return response;
    }

    @ApiOperation(value = "工单列表导出")
    @RequestMapping(value = "/ticketsFormAllListExport/{encrypted}", method = RequestMethod.GET)
    public void ticketsFormAllListExport(@PathVariable String encrypted, HttpServletResponse response) throws IOException {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        log.info("工单列表导出接受方法ticketsFormAllListExport,参数：{}"+ decrypted);

        TicketsFormPageRequest request = JSON.parseObject(decrypted, TicketsFormPageRequest.class);
        BaseResponse<TicketsFormListResponse> baseResponse = ticketsFormProvider.ticketsFormAll(request);
        TicketsFormListResponse context = baseResponse.getContext();

        List<TicketsFormExcel> exportList = new ArrayList<>();
        if(Objects.nonNull(context) && CollectionUtils.isNotEmpty(context.getTicketsFormQueryVOList())){
            List<TicketsFormQueryVO> queryVOList = context.getTicketsFormQueryVOList();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (TicketsFormQueryVO formQueryVO : queryVOList) {
                TicketsFormExcel formExcel = new TicketsFormExcel();

                formExcel.setCustomerAccount(formQueryVO.getCustomerWallet().getCustomerAccount());
                formExcel.setCustomerPhone(formQueryVO.getCustomerPhone());
                formExcel.setCustomerName(formQueryVO.getCustomerName());
                // 提现申请时间
                formExcel.setApplyStartTime(timeFormatter.format(formQueryVO.getApplyTime()));
                // 取消时间
                if(formQueryVO.getExtractStatus() == 6){
                    formExcel.setCancelTime(Objects.nonNull(formQueryVO.getAuditTime()) ? timeFormatter.format(formQueryVO.getAuditTime()) : "-");
                }
                // 提现金额
                formExcel.setFadeInPrice(formQueryVO.getApplyPrice());
                // 到账金额
                formExcel.setArrivalPrice(formQueryVO.getArrivalPrice());
                // 收款账户
                String acceptAccount = formQueryVO.getBankCode();
                if(StringUtils.isNotBlank(formQueryVO.getBankName())){
                    acceptAccount = formQueryVO.getBankName()+" "+acceptAccount;
                }
                if(StringUtils.isNotBlank(formQueryVO.getBankBranch())){
                    acceptAccount = formQueryVO.getBankBranch()+" "+acceptAccount;
                }
                formExcel.setAcceptAccount(acceptAccount);

                // 客服审核时间
                TicketsFormLogVo cusLogoVo = formQueryVO.getCustomerServiceTicketsFormLogVo();
                if(Objects.nonNull(cusLogoVo) && Objects.nonNull(cusLogoVo.getAuditTime())){
                    LocalDateTime auditTime = cusLogoVo.getAuditTime();
                    formExcel.setAuditStartTime(timeFormatter.format(auditTime));
                }
                //财务审核时间
                TicketsFormLogVo financialLogVo = formQueryVO.getFinancialTicketsFormLogVo();
                if(Objects.nonNull(financialLogVo) && Objects.nonNull(financialLogVo.getAuditTime())){
                    LocalDateTime auditTime = financialLogVo.getAuditTime();
                    formExcel.setAuditEndTime(timeFormatter.format(auditTime));
                }

                // 设置当前状态
                Integer status = formQueryVO.getExtractStatus();
                //提现申请单状态【1待审核，2已审核，3已打款，4已拒绝】
                String statusInfo = "";
                switch (status) {
                    case 1: statusInfo = "待审核"; break;
                    case 2: statusInfo = "待打款"; break;
                    case 3: statusInfo = "已打款"; break;
                    case 4: statusInfo = "已拒绝"; break;
                    case 5: statusInfo = "打款失败"; break;
                    default: statusInfo = "用户撤回";
                }
                formExcel.setState(statusInfo);
                exportList.add(formExcel);
            }
        }
        SimpleDateFormat fDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("提现列表数据" + fDate.format(new Date()), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), TicketsFormExcel.class).sheet("模板").doWrite(exportList);
    }


    @ApiOperation(value = "提现工单列表")
    @RequestMapping(value = "/withdrawalList", method = RequestMethod.POST)
    public BaseResponse<TicketsFormQueryResponse> ticketsFormList(@RequestBody TicketsFormPageRequest ticketsFormPageRequest) {
        ticketsFormPageRequest.setApplyType(2);
        BaseResponse<TicketsFormQueryResponse> response = ticketsFormProvider.withdrawalList(ticketsFormPageRequest);
        return response;
    }

//    @ApiOperation(value = "充值工单")
//    @RequestMapping(value = "/rechargeList",method = RequestMethod.POST)
//    public BaseResponse<TicketsFormQueryResponse> rechargeList(@RequestBody TicketsFormPageRequest ticketsFormPageRequest){
//        ticketsFormPageRequest.setApplyType(1);
//        BaseResponse<TicketsFormQueryResponse> response = ticketsFormProvider.withdrawalList(ticketsFormPageRequest);
//        return response;
//    }

    @ApiOperation(value = "提现审核通过")
    @RequestMapping(value = "/adopt", method = RequestMethod.POST)
    public BaseResponse adopt(@RequestBody TicketsFormAdoptRequest request) {
        //记录操作信息
//        this.record("财务管理","提现申请","审核通过");
        if(StringUtils.isNotBlank(request.getRemark()) && request.getRemark().length() > 100){
            throw new SbcRuntimeException("备注长度过长，请不要超过100个字符");
        }
        RLock rLock = redissonClient.getFairLock(request.getFormIds().get(0).toString());
        rLock.lock();
        try {
            String accountName = commonUtil.getAccountName();
            request.setAuditAdmin(accountName);
            request.setRemark(request.getBackRemark());
            ticketsFormProvider.adopt(request);
        } catch (Exception e) {
            log.error("提现审核:/ticketsForm/adopt->error:{}",e.getMessage());
            throw new SbcRuntimeException(e);
        } finally {
            rLock.unlock();
        }
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "打款")
    @RequestMapping(value = "/payment", method = RequestMethod.POST)
    public BaseResponse payment(@RequestBody TicketsFormAdoptRequest request) {
//        this.record("财务管理","提现申请","打款");
        List<TicketsFormQueryVO> list = null;
        log.info("/ticketsForm/payment-->TicketsFormAdoptRequest:{}",JSON.toJSONString(request));
        RLock rLock = redissonClient.getFairLock(request.getFormIds().get(0).toString());
        rLock.lock();
        try {
            if(StringUtils.isNotBlank(request.getRemark()) && request.getRemark().length() > 100){
                throw new SbcRuntimeException("备注长度过长，请不要超过100个字符");
            }
            if(isAfterToday(request.getTransferDate())){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "转账日期不可大于当前日期！");
            }

            // 查询鲸币提现收款账户
            PayWithDrawResponse payWithDrawResponse = payWithDrawProvider.findWithDrawById(PayWithDrawIdDTO.builder().withdrawId(request.getAccountId().intValue()).build()).getContext();
            log.info("/ticketsForm/payment-->payWithDrawResponse:{}",JSON.toJSONString(payWithDrawResponse));
            if (payWithDrawResponse == null || payWithDrawResponse.getPayWithDrawVO() == null) {
                throw new SbcRuntimeException("没有查询到转账用户银行卡信息, 请重试！");
            }
            PayWithDrawVO payWithDrawVO = payWithDrawResponse.getPayWithDrawVO();
            String accountName = commonUtil.getAccountName();
            request.setAuditAdmin(accountName);
            request.setBankNo(payWithDrawVO.getBankAccount());
            list = ticketsFormProvider.payment(request).getContext();
            if (null != list && !list.isEmpty()) {
                List<com.wanmi.sbc.order.bean.vo.TicketsFormQueryVO> TicketsForms = new ArrayList<>();
                //推送金蝶收款退款单
                for (TicketsFormQueryVO TicketsFormQueryVO : list) {
                    com.wanmi.sbc.order.bean.vo.TicketsFormQueryVO vo = new com.wanmi.sbc.order.bean.vo.TicketsFormQueryVO();
                    BeanUtils.copyProperties(TicketsFormQueryVO, vo);
                    vo.setBankNo(TicketsFormQueryVO.getBankNo());
                    TicketsForms.add(vo);
                }
                String payType = getPayTypeByAccountName(payWithDrawVO.getAccountName());
                TicketsFormPushPayOrderKingdeeRequest ticketsFormPushPayOrderKingdeeRequest = TicketsFormPushPayOrderKingdeeRequest.builder()
                        .ticketsForms(TicketsForms).payType(payType)
                        .build();
                tradeProvider.ticketsFormPushPayOrderKingdee(ticketsFormPushPayOrderKingdeeRequest);
            }
            return BaseResponse.SUCCESSFUL();
        } catch (SbcRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new SbcRuntimeException(e.getMessage());
        } finally {
            rLock.unlock();
        }
    }

    private boolean isAfterToday(LocalDateTime ldt) {
        return Objects.nonNull(ldt) && ldt.isAfter(LocalDateTime.now().with(LocalTime.MAX));
    }

    private String getPayTypeByAccountName(String accountName){
        String payType = null;
        List<PayWay> payWays = Arrays.asList(PayWay.values());
        for (PayWay payWay : payWays) {
            if(payWay.getDesc().equals(accountName)){
                payType = payWay.toValue();
                break;
            }
        }
        if(StringUtils.isBlank(payType)){
            payType = "14";
        }
        return payType;
    }

    @ApiOperation(value = "提现审核拒绝/失败")
    @RequestMapping(value = "/reject", method = RequestMethod.POST)
    public BaseResponse reject(@RequestBody TicketsFormAdoptRequest request) {
//        this.record("财务管理","提现申请","提现审核驳回");
        String accountName = commonUtil.getAccountName();
        request.setAuditAdmin(accountName);

        //状态变更推金蝶
        ticketsFormProvider.reject(request).getContext();
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @description  运营后台-财务-提现列表-已完成的单据支持修改支付、打款凭证
     * @author  shiy
     * @date    2023/3/21 10:14
     * @params  [com.wanmi.sbc.account.api.request.wallet.TicketsFormAdoptRequest]
     * @return  com.wanmi.sbc.common.base.BaseResponse
    */
    @ApiOperation(value = "运营后台-财务-提现列表-已完成的单据支持修改支付、打款凭证")
    @RequestMapping(value = "/updateImgAfterReject", method = RequestMethod.POST)
    public BaseResponse updateImgAfterReject(@RequestBody TicketsFormAdoptRequest request) {
//        this.record("财务管理","提现申请","提现审核驳回");
        ticketsFormProvider.updateImgAfterReject(request).getContext();
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "充值工单列表")
    @RequestMapping(value = "/rechargeList", method = RequestMethod.POST)
    public BaseResponse<TicketsFormQueryResponse> rechargeList(@RequestBody TicketsFormPageRequest ticketsFormPageRequest) {
        ticketsFormPageRequest.setApplyType(1);
        BaseResponse<TicketsFormQueryResponse> response = ticketsFormProvider.rechargeList(ticketsFormPageRequest);
        return response;
    }

    @ApiOperation(value = "创建充值工单申请")
    @RequestMapping(value = "/createRecharge", method = RequestMethod.POST)
    public BaseResponse createRecharge(@RequestBody CreateRechargeRequest request) {
        //        this.record("财务管理","账户余额","创建充值工单申请");
        return ticketsFormProvider.createRecharge(request);
    }

    @ApiOperation(value = "充值审核通过")
    @RequestMapping(value = "/rechargeAdopt", method = RequestMethod.POST)
    public BaseResponse rechargeAdopt(@RequestBody TicketsFormAdoptRequest request) {
        //        this.record("财务管理","充值申请","充值审核通过");
        return ticketsFormProvider.rechargeAdopt(request);
    }

    @ApiOperation(value = "充值审核驳回")
    @RequestMapping(value = "/rechargeReject", method = RequestMethod.POST)
    public BaseResponse rechargeReject(@RequestBody TicketsFormAdoptRequest request) {
//                this.record("财务管理","充值申请","充值审核驳回");
        return ticketsFormProvider.rechargeReject(request);
    }

    /**
     * @param modular   操作模块
     * @param column    操作栏目
     * @param operation 操作事项
     */
    public void record(String modular, String column, String operation) {
        String accountName = commonUtil.getAccountName();
        EmployeeVO employee = employeeQueryProvider.getByAccountName(EmployeeByAccountNameRequest.builder().accountName(accountName).accountType(AccountType.s2bBoss).build()).getContext().getEmployee();
        Long companyInfoId = employee.getCompanyInfo().getCompanyInfoId();
        Map<String, Object> claims = Maps.newHashMap();
        claims.put("employeeId", employee.getEmployeeId());
        claims.put("EmployeeName", employee.getAccountName());
        claims.put("adminId", companyInfoId);
        claims.put("platform", Platform.PROVIDER.toValue());
        claims.put("ip", HttpUtil.getIpAddr());
        operateLogMQUtil.convertAndSend(modular, column, operation, new DefaultClaims(claims));
    }

//    /**
//     * 调账操作
//     * @return
//     */
//    @ApiOperation(value = "调账操作")
//    @RequestMapping(value = "accountRegulation",method = RequestMethod.POST)
//    public BaseResponse accountRegulation(@Valid @RequestBody AccountRegulationRequest request){
//        String accountName = commonUtil.getAccountName();
//        request.setAccountName(accountName);
//        ticketsFormProvider.accountRegulation(request);
//        this.record("财务管理","调账操作","调账操作->"+request.getChangeBalance());
//        return BaseResponse.SUCCESSFUL();
//    }
//
//
//    /**
//     * 调账操作
//     * @return
//     */
//    @ApiOperation(value = "调账明细列表")
//    @RequestMapping(value = "accountRegulationList",method = RequestMethod.POST)
//    public BaseResponse accountRegulationList(@Valid @RequestBody TicketsFormPageRequest request){
//        return ticketsFormProvider.accountRegulationList(request);
//    }
}

package com.wanmi.sbc.job;

import com.google.common.collect.Lists;
import com.wanmi.sbc.account.api.provider.funds.CustomerFundsDetailQueryProvider;
import com.wanmi.sbc.account.api.request.funds.CustomerFundsAmountRequest;
import com.wanmi.sbc.account.api.response.funds.CustomerFundsTodayResponse;
import com.wanmi.sbc.common.base.MessageMQRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.node.AccoutAssetsType;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.growthvalue.CustomerGrowthValueQueryProvider;
import com.wanmi.sbc.customer.api.provider.points.CustomerPointsDetailQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.customer.CustomerIdsListRequest;
import com.wanmi.sbc.customer.api.request.growthvalue.CustomerGrowthValueQueryRequest;
import com.wanmi.sbc.customer.api.request.points.CustomerPointsDetailQueryRequest;
import com.wanmi.sbc.customer.api.response.points.CustomerPointsExpireResponse;
import com.wanmi.sbc.customer.bean.enums.OperateType;
import com.wanmi.sbc.customer.bean.vo.CustomerPointsDetailVO;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeQueryProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponInfoQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeQueryRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponInfoQueryRequest;
import com.wanmi.sbc.marketing.bean.dto.CouponCodeDTO;
import com.wanmi.sbc.marketing.bean.vo.CouponInfoVO;
import com.wanmi.sbc.message.bean.enums.NodeType;
import com.wanmi.sbc.mq.MessageSendProducer;
import com.wanmi.sbc.setting.api.provider.SystemPointsConfigQueryProvider;
import com.wanmi.sbc.setting.api.response.SystemPointsConfigQueryResponse;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 优惠券和积分通知节点定时任务
 */
@Component
@Slf4j
@JobHandler(value="CouponAndIntegralMessageJobHandler")
public class CouponAndIntegralMessageJobHandler extends IJobHandler {

    @Autowired
    private CouponCodeQueryProvider couponCodeQueryProvider;

    @Autowired
    private MessageSendProducer messageSendProducer;

    @Autowired
    private CouponInfoQueryProvider couponInfoQueryProvider;

    @Autowired
    private CustomerPointsDetailQueryProvider customerPointsDetailQueryProvider;

    @Autowired
    private SystemPointsConfigQueryProvider systemPointsConfigQueryProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CustomerGrowthValueQueryProvider customerGrowthValueQueryProvider;

    @Autowired
    private CustomerFundsDetailQueryProvider customerFundsDetailQueryProvider;

    @Override
    public ReturnT<String> execute(String s) throws Exception {

        //查询当天到账的优惠券
        LocalDateTime startTime = LocalDate.now().atTime(LocalTime.MIN);
        LocalDateTime endTime = LocalDate.now().atTime(19,0);
        CouponCodeQueryRequest acquireRequest = CouponCodeQueryRequest.builder()
                .acquireStartTime(startTime)
                .acquireEndTime(endTime)
                .build();
        CouponCodeQueryRequest expireRequest = CouponCodeQueryRequest.builder()
                .endTime(LocalDate.now().atTime(23,59,59))
                .useStatus(DefaultFlag.NO)
                .build();

        //优惠券到账
        handleCoupon(acquireRequest, AccoutAssetsType.COUPON_RECEIPT);
        //优惠券过期
        handleCoupon(expireRequest, AccoutAssetsType.COUPON_EXPIRED);
        //积分到账
        handleIntegralRecept(startTime, endTime, AccoutAssetsType.INTEGRAL_RECEIPT);
        //积分过期
        handleIntegralExpire();
        //成长值到账
        handleGrowthValue(startTime, endTime);
        //余额账户变更
        handleBalanceChange(startTime, endTime);


        return SUCCESS;
    }

    /**
     * 积分到账
     */
    private void handleIntegralRecept(LocalDateTime startTime, LocalDateTime endTime, AccoutAssetsType nodeCode){
        CustomerPointsDetailQueryRequest request = CustomerPointsDetailQueryRequest.builder()
                .type(OperateType.GROWTH)
                .opTimeBegin(startTime)
                .opTimeEnd(endTime).build();
        List<CustomerPointsDetailVO> customerPointsDetailVOList = customerPointsDetailQueryProvider.list(request)
                .getContext().getCustomerPointsDetailVOList();
        if(CollectionUtils.isNotEmpty(customerPointsDetailVOList)){
            //按用户分组
            Map<String, List<CustomerPointsDetailVO>> map = customerPointsDetailVOList.stream()
                    .collect(Collectors.groupingBy(CustomerPointsDetailVO::getCustomerId));
            for(Map.Entry<String, List<CustomerPointsDetailVO>> entry : map.entrySet()){
                List<String>  params = new ArrayList<>();
                List<CustomerPointsDetailVO> customerPointsDetailVOS = entry.getValue();
                long sum = customerPointsDetailVOList.stream().mapToLong(CustomerPointsDetailVO::getPoints).sum();
                params.add(String.valueOf(sum));
                String mobile = null;
                if(CollectionUtils.isNotEmpty(customerPointsDetailVOS)){
                    mobile = customerPointsDetailVOS.get(0).getCustomerAccount();
                }
                this.sendMessage(NodeType.ACCOUNT_ASSETS, nodeCode, params, null, entry.getKey(), mobile);
            }
        }
    }

    /**
     * 积分过期
     */
    private void handleIntegralExpire(){
        SystemPointsConfigQueryResponse pointsConfig = systemPointsConfigQueryProvider.querySystemPointsConfig().getContext();
        Integer pointsExpireMonth = pointsConfig.getPointsExpireMonth();
        Integer pointsExpireDay = pointsConfig.getPointsExpireDay();
        if(pointsExpireMonth.equals(LocalDate.now().getMonthValue()) && pointsExpireDay.equals(LocalDate.now().plusDays(7).getDayOfMonth())){
            // 1. 查询用户id列表
            List<String> customerIds = customerQueryProvider.listCustomerId().getContext().getCustomerIdList();
            Map<String, String> customerMobile = getCustomerAccountMap(customerIds);
            // 2 创建线程池，暂时是每50个用户分配一个线程
            ExecutorService executor = Executors.newFixedThreadPool(50);
            // 3 遍历用户列表
            customerIds.forEach(customerId -> {
                executor.submit(() -> {
                    // 3.查询用户即将过期积分
                    CustomerPointsExpireResponse response = customerPointsDetailQueryProvider
                            .queryWillExpirePointsForCronJob(new CustomerGetByIdRequest(customerId))
                            .getContext();
                    if (response.getWillExpirePoints() > 0) {
                        List<String> params = Lists.newArrayList(String.valueOf(response.getWillExpirePoints()));
                       this.sendMessage(NodeType.ACCOUNT_ASSETS, AccoutAssetsType.INTEGRAL_EXPIRED, params, null, customerId, customerMobile.get(customerId));
                    }
                });
            });
            executor.shutdown();
        }

    }

    /**
     * 优惠券到账和过期
     */
    private void handleCoupon(CouponCodeQueryRequest request, AccoutAssetsType nodeCode){
        List<CouponCodeDTO> couponCodeList = couponCodeQueryProvider.listCouponCodeByCondition(request).getContext().getCouponCodeList();
        if(CollectionUtils.isNotEmpty(couponCodeList)){
            //按照会员分组
            Map<String, List<CouponCodeDTO>> map = couponCodeList.stream().collect(Collectors.groupingBy(CouponCodeDTO::getCustomerId));
            Map<String, String> customerMobile = getCustomerAccountMap(new ArrayList<>(map.keySet()));
            for (Map.Entry<String, List<CouponCodeDTO>> entry : map.entrySet()){
                List<String> params = new ArrayList<>();
                List<CouponCodeDTO> couponCodeDTOS = entry.getValue();
                if(CollectionUtils.isNotEmpty(couponCodeDTOS)){
                    //查询优惠券信息，计算总面值
                    List<String> couponIds = couponCodeDTOS.stream().map(CouponCodeDTO::getCouponId).collect(Collectors.toList());
                    CouponInfoQueryRequest couponInfoQueryRequest = CouponInfoQueryRequest.builder().couponIds(couponIds).build();
                    List<CouponInfoVO> couponInfoVOS = couponInfoQueryProvider.queryCouponInfos(couponInfoQueryRequest)
                            .getContext().getCouponCodeList();
                    BigDecimal sum = couponInfoVOS.stream().map(CouponInfoVO::getDenomination).reduce(BigDecimal::add).get();
                    params.add(String.valueOf(couponCodeDTOS.size()));
                    params.add(sum.toString());
                    this.sendMessage(NodeType.ACCOUNT_ASSETS, nodeCode, params, null, entry.getKey(), customerMobile.get(entry.getKey()));
                }
            }
        }
    }

    /**
     * 成长值到账
     */
    private void handleGrowthValue(LocalDateTime startTime, LocalDateTime endTime){
        // 1. 查询用户id列表
        List<String> customerIds = customerQueryProvider.listCustomerId().getContext().getCustomerIdList();
        // 2 创建线程池，暂时是每50个用户分配一个线程
        ExecutorService executor = Executors.newFixedThreadPool(50);
        Map<String, String> customerMobile = getCustomerAccountMap(customerIds);
        // 3 遍历用户列表
        customerIds.forEach(customerId -> {
            executor.submit(() -> {
                // 3.查询用户到账成长值
                CustomerGrowthValueQueryRequest queryRequest = CustomerGrowthValueQueryRequest.builder()
                        .customerId(customerId)
                        .type(OperateType.GROWTH)
                        .gteGainStartDate(startTime)
                        .lteGainEndDate(endTime).build();
                Integer growthValueSum = customerGrowthValueQueryProvider.getGrowthValueToday(queryRequest).getContext().getGrowthValueSum();
                if (growthValueSum > 0) {
                    List<String> params = Lists.newArrayList(String.valueOf(growthValueSum));
                    this.sendMessage(NodeType.ACCOUNT_ASSETS, AccoutAssetsType.GROWTH_VALUE_RECEIPT, params, null, customerId, customerMobile.get(customerId));
                }
            });
        });
        executor.shutdown();
    }

    /**
     * 余额账户变更
     * @param startTime
     * @param endTime
     */
    public void handleBalanceChange(LocalDateTime startTime, LocalDateTime endTime){
        // 1. 查询用户id列表
        List<String> customerIds = customerQueryProvider.listCustomerId().getContext().getCustomerIdList();
        // 2 创建线程池，暂时是每50个用户分配一个线程
        ExecutorService executor = Executors.newFixedThreadPool(50);
        Map<String, String> customerMobile = getCustomerAccountMap(customerIds);
        // 3 遍历用户列表
        customerIds.forEach(customerId -> {
            executor.submit(() -> {
                // 3.查询用户的收入和支出金额
                CustomerFundsAmountRequest request = new CustomerFundsAmountRequest();
                request.setCustomerId(customerId);
                request.setStartTime(startTime);
                request.setEndTime(endTime);
                CustomerFundsTodayResponse response = customerFundsDetailQueryProvider.getFundsChange(request).getContext();
                if (response.getPaymentAmount().compareTo(BigDecimal.ZERO) == 1 || response.getReceiptAmount().compareTo(BigDecimal.ZERO) == 1)  {
                    List<String> params = Lists.newArrayList(response.getReceiptAmount().toString(), response.getPaymentAmount().toString());
                    this.sendMessage(NodeType.ACCOUNT_ASSETS, AccoutAssetsType.BALANCE_CHANGE, params, null, customerId, customerMobile.get(customerId));
                }
            });
        });
        executor.shutdown();
    }

    /**
     * 发送消息
     * @param nodeType
     * @param nodeCode
     * @param params
     * @param routeParam
     * @param customerId
     */
    private void sendMessage(NodeType nodeType, AccoutAssetsType nodeCode, List<String> params, String routeParam, String customerId, String mobile){
        Map<String, Object> map = new HashMap<>();
        map.put("type", nodeType.toValue());
        map.put("node", nodeCode.toValue());
        MessageMQRequest messageMQRequest = new MessageMQRequest();
        messageMQRequest.setNodeCode(nodeCode.getType());
        messageMQRequest.setNodeType(nodeType.toValue());
        messageMQRequest.setParams(params);
        messageMQRequest.setRouteParam(map);
        messageMQRequest.setCustomerId(customerId);
        messageMQRequest.setMobile(mobile);
        messageSendProducer.sendMessage(messageMQRequest);
    }

    /**
     * 获取批量会员账号map
     * @param customerIds
     * @return
     */
    private Map<String, String> getCustomerAccountMap(List<String> customerIds){
        CustomerIdsListRequest listRequest = new CustomerIdsListRequest();
        listRequest.setCustomerIds(customerIds);
        List<CustomerVO> customerVOList = customerQueryProvider.getCustomerListByIds(listRequest).getContext().getCustomerVOList();
        Map<String, String> customerMobile = new HashMap<>();
        if(CollectionUtils.isNotEmpty(customerVOList)){
            customerMobile.putAll(customerVOList.stream().collect(Collectors.toMap(CustomerVO::getCustomerId, CustomerVO::getCustomerAccount)));
        }
        return customerMobile;
    }
}

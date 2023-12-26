package com.wanmi.sbc.datecenter;

import com.wanmi.ares.provider.ReplayTradeQueryProvider;
import com.wanmi.ares.request.replay.BossCustomerTradeRequest;
import com.wanmi.ares.request.replay.CustomerTradeRequest;
import com.wanmi.ares.response.datecenter.BossCustomerTradeResponse;
import com.wanmi.ares.response.datecenter.CustomerTradeItemResponse;
import com.wanmi.ares.response.datecenter.CustomerTradeResponse;
import com.wanmi.ms.autoconfigure.JwtProperties;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.loginregister.EmployeeCopyQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerDetailListForOrderRequest;
import com.wanmi.sbc.customer.api.request.customer.CustomerIdsListRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeCopyQueryRequest;
import com.wanmi.sbc.customer.api.response.customer.*;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailCopyVO;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import com.wanmi.sbc.customer.bean.vo.EmployeeCopyVo;
import com.wanmi.sbc.datecenter.request.CustomerTradeItemRequest;
import com.wanmi.sbc.datecenter.request.DataCenterStatisticForBossRequest;
import com.wanmi.sbc.datecenter.response.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lm
 * @date 2022/09/16 17:39
 */
@RestController
@Slf4j
@Api(tags = "DateCenterStatisticController", description = "白鲸管家数据监控中心接口")
@RequestMapping("/dataCenter")
public class DateCenterStatisticController {

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private ReplayTradeQueryProvider replayTradeQueryProvider;

    @Autowired
    private EmployeeCopyQueryProvider employeeCopyQueryProvider;

    @ApiOperation("白鲸管家业务员统计数据查询")
    @PostMapping("/queryDataCenterStatistic")
    public BaseResponse<DataCenterStatisticResponse> queryDataCenterStatistic(@RequestBody CustomerTradeItemRequest params) {
        String date = params.getDate();
        log.info("数据面板统计数据接口,接受参数-月份：{}", date);
        Assert.notNull(date, "日期参数不能为空");

        Claims claims = getClaims();

        // 获取员工管理区域
        String employeeManageCity = claims.get("cityCode", String.class);
        String employeeManageArea = claims.get("areaCode", String.class);

        /*构建请求参数*/
        CustomerDetailListForOrderRequest customerDetailRequest = new CustomerDetailListForOrderRequest();
        customerDetailRequest.setCityId(Long.valueOf(employeeManageCity));
        // 查询当前管家负责区域内所有用户
        CustomerDetailCopyListResponse detailListForOrderResponse = customerQueryProvider.listCustomerDetailCopy(customerDetailRequest).getContext();
        List<CustomerDetailCopyVO> detailResponseList = detailListForOrderResponse.getDetailResponseList();
        if (!StringUtils.isEmpty(employeeManageCity) && employeeManageCity.startsWith("4301")) {// 长沙市内白鲸管家特殊处理，具体到区
            if (!StringUtils.isEmpty(employeeManageArea)) {
                List<String> aresList = Arrays.asList(employeeManageArea.split(","));
                detailResponseList = detailResponseList.stream().filter(item -> aresList.contains(item.getAreaId())).collect(Collectors.toList());
            }
        }
        if (detailResponseList.isEmpty()) {// 不存在管理的用户
            return BaseResponse.SUCCESSFUL();
        }
        // 根据客户ID查询该月有下单的客户
        List<String> customerIds = detailResponseList.stream().map(item -> item.getCustomerId()).collect(Collectors.toList());
        List<CustomerTradeResponse> customerTradeList = replayTradeQueryProvider.queryCustomerTradeDetail(CustomerTradeRequest.builder()
                .customerIds(customerIds)
                .date(date).build());

        MonthStatisticResponse monthStatisticResponse = new MonthStatisticResponse();

        for (CustomerTradeResponse customerTrade : customerTradeList) {
            // 设置客户名称
            CustomerDetailCopyVO customerDetailCopyVO = detailResponseList.stream().filter(item -> item.getCustomerId().equals(customerTrade.getCustomerId())).findFirst()
                    .get();
            customerTrade.setCustomerAccount(customerDetailCopyVO.getContactPhone());
            customerTrade.setCustomerName(customerDetailCopyVO.getCustomerName());

            // 统计本月总箱数、总单数、总金额
            monthStatisticResponse.setTradePrice(monthStatisticResponse.getTradePrice().add(customerTrade.getTotalPrice()));
            monthStatisticResponse.setTradeNum(monthStatisticResponse.getTradeNum() + customerTrade.getTradeNum());
            monthStatisticResponse.setTradeItemNum(monthStatisticResponse.getTradeItemNum() + customerTrade.getBuyNum());
        }
        // 统计本月总下单人数
        monthStatisticResponse.setTradeUser(customerTradeList.size());

        DataCenterStatisticResponse statisticResponse = new DataCenterStatisticResponse();
        statisticResponse.setMonthStatisticResponse(monthStatisticResponse);
        statisticResponse.setCustomerDetailStatistic(customerTradeList);
        return BaseResponse.success(statisticResponse);
    }


    @ApiOperation("查询当月下单客户详细数据")
    @PostMapping("/customerDetail")
    public BaseResponse<CustomerDetailStatisticResponse> queryCustomerDetail(@RequestBody CustomerTradeItemRequest params) {
        log.info("queryCustomerDetail接受参数；客户id：{}，时间：{}", params.getCustomerId(), params.getDate());

        /*查询客户基本信息*/
        CustomerIdsListRequest customerIdsListRequest = new CustomerIdsListRequest();
        customerIdsListRequest.setCustomerIds(Arrays.asList(params.getCustomerId()));
        CustomerDetailCopyByIdResponse customerDetailCopyByIdResponse = customerQueryProvider.listCustomerDetailByIds(customerIdsListRequest).getContext();
        CustomerDetailCopyVO detailResponse = customerDetailCopyByIdResponse.getDetailResponseList().get(0);


        CustomerTradeRequest customerTradeRequest = CustomerTradeRequest.builder()
                .customerIds(Arrays.asList(params.getCustomerId()))
                .date(params.getDate()).build();
        List<CustomerTradeItemResponse> customerTradeItemResponses = replayTradeQueryProvider.queryCustomerTradeItemByCid(customerTradeRequest);


        CustomerDetailBaseResponse baseResponse = new CustomerDetailBaseResponse();
        baseResponse.setCustomerAccount(detailResponse.getContactPhone());
        baseResponse.setCustomerName(detailResponse.getCustomerName());
        baseResponse.setTradeNum(customerTradeItemResponses.size());

        for (CustomerTradeItemResponse tradeItem : customerTradeItemResponses) {
            // 统计本月下单总金额、总箱数
            baseResponse.setTradePrice(baseResponse.getTradePrice().add(tradeItem.getTotalPrice()));
            baseResponse.setTradeItemNum(baseResponse.getTradeItemNum() + tradeItem.getBuyNum());
        }

        CustomerDetailStatisticResponse statisticResponse = new CustomerDetailStatisticResponse();
        statisticResponse.setCustomerDetail(baseResponse);
        statisticResponse.setCustomerTradeItemList(customerTradeItemResponses);
        return BaseResponse.success(statisticResponse);
    }

    @ApiOperation("查询客户详细订单数据")
    @PostMapping("/queryDataCenterStatisticForBoss")
    public BaseResponse<List<StatisticForBossResponse>> queryDataCenterStatisticForBoss(@RequestBody DataCenterStatisticForBossRequest request) {

        // 查询当月所有下单信息
        List<BossCustomerTradeResponse> bossCustomerTradeList = replayTradeQueryProvider.queryAllTradeByDate(BossCustomerTradeRequest.builder()
                .orderType(request.getSortedType())
                .date(request.getDate()).provinceCode(request.getProvinceCode()).build());

        // 查询所有负责该区域的白鲸管家
        List<EmployeeCopyVo> employeeCopyVos = employeeCopyQueryProvider.queryEmployeeCopyByProvinceCode(EmployeeCopyQueryRequest.builder()
                .provinceCode(request.getProvinceCode()).build()).getContext();

        List<StatisticForBossResponse> result = new ArrayList<>();
        List<String> allocationCustomerId = new ArrayList<>();// 记录已被分配地区的用户
        for (EmployeeCopyVo employeeCopyVo : employeeCopyVos) {
            StatisticForBossResponse statistic = new StatisticForBossResponse();
            statistic.setEmployeeName(employeeCopyVo.getEmployeeName());// 业务员名称
            statistic.setManageArea(employeeCopyVo.getManageArea()); // 负责区域
            // 查询当前业务员负责区域用户
            List<CustomerDetailVO> customerDetailList = employeeCopyVo.getCustomerDetailList();
            List<String> customerIds = customerDetailList.stream().map(item -> item.getCustomerId()).collect(Collectors.toList());

            List<BossCustomerTradeResponse> collect = bossCustomerTradeList.stream().filter(item -> customerIds.contains(item.getCustomerId())).collect(Collectors.toList());
            // 设置本月下单人数
            statistic.setTradeUserNum(collect.size());
            allocationCustomerId.addAll(collect.stream().map(item -> item.getCustomerId()).collect(Collectors.toList()));
            // 设置本月下单总金额
            BigDecimal totalPrice = collect.stream().distinct().map(item -> item.getTradePrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
            statistic.setTradePrice(totalPrice);
            // 设置本月下单数
            statistic.setTradeNum(collect.stream().map(item -> item.getTradeNum()).reduce(0, (a, b) -> a + b));
            // 设置本月下单箱数
            statistic.setTradeItemNum(collect.stream().map(item -> item.getTradeItemNum()).reduce(0, (a, b) -> a + b));
            result.add(statistic);
        }
        if (request.getSortedType() == 1) { //按箱排序
            result.sort((a, b) -> b.getTradeItemNum() - a.getTradeItemNum());
        } else {
            result.sort((a, b) -> b.getTradeNum() - a.getTradeNum());
        }
        // 数据分页
        Integer startPage = (request.getPage() - 1) * request.getPageSize();
        Integer endPage = startPage + request.getPageSize();
        return BaseResponse.success(result.stream().skip(startPage).limit(endPage).collect(Collectors.toList()));
    }

    private Claims getClaims() {
        String jwtHeaderKey = !StringUtils.isEmpty(jwtProperties.getJwtHeaderKey()) ? jwtProperties.getJwtHeaderKey
                () : "Authorization";
        String jwtHeaderPrefix = !StringUtils.isEmpty(jwtProperties.getJwtHeaderPrefix()) ? jwtProperties
                .getJwtHeaderPrefix() : "Bearer ";
        String authHeader = HttpUtil.getRequest().getHeader(jwtHeaderKey);
        String token = authHeader.substring(jwtHeaderPrefix.length());
        Claims claims = Jwts.parser().setSigningKey(this.jwtSecretKey).parseClaimsJws(token).getBody();
        return claims;
    }

}

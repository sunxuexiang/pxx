package com.wanmi.sbc.customer;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerIdListRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByMobileRequest;
import com.wanmi.sbc.customer.api.response.employee.EmployeeByMobileNewResponse;
import com.wanmi.sbc.customer.bean.enums.AccountState;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.invitationhistorysummary.InvitationHistorySummaryQueryProvider;
import com.wanmi.sbc.customer.api.provider.invitationstatistics.InvitationStatisticsQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByMobileRequest;
import com.wanmi.sbc.customer.api.request.invitationhistorysummary.InvitationHistorySummaryByIdRequest;
import com.wanmi.sbc.customer.api.request.invitationstatistics.InvitationRegisterStatisticsRequest;
import com.wanmi.sbc.customer.api.response.employee.EmployeeByMobileNewResponse;
import com.wanmi.sbc.customer.api.response.invitationhistorysummary.InvitationHistorySummaryByIdResponse;
import com.wanmi.sbc.customer.api.response.invitationhistorysummary.InvitationHistorySummaryResponse;
import com.wanmi.sbc.customer.api.response.invitationstatistics.InvitationStatisticsListResponse;
import com.wanmi.sbc.customer.bean.enums.AccountState;
import com.wanmi.sbc.customer.bean.vo.InvitationHistorySummaryVO;
import com.wanmi.sbc.customer.bean.vo.InvitationStatisticsVO;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerIdListRequest;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.trade.PurchaseQueryCountRequest;
import com.wanmi.sbc.order.bean.dto.TradeQueryDTO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>描述<p>
 *
 * @author zhaowei
 * @date 2021/4/14
 */

@Api(description = "采购员查询API", tags = "ShoppingGuideController")
@RestController
@RequestMapping(value = "/customer")
public class ShoppingGuideController {

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private InvitationHistorySummaryQueryProvider invitationHistorySummaryQueryProvider;
    @Autowired
    private InvitationStatisticsQueryProvider invitationStatisticsQueryProvider;


    @ApiOperation(value = "查询当前业务员下的邀新历史汇总数据")
    @RequestMapping(value = "/queryPurchaseCount/{employeeId}", method = RequestMethod.GET)
    public BaseResponse<InvitationHistorySummaryResponse> queryPurchaseCount(@PathVariable String employeeId) {
        InvitationHistorySummaryResponse response = InvitationHistorySummaryResponse.builder().build();
        //历史邀新数据
        InvitationHistorySummaryByIdResponse summaryByIdResponseBaseResponse =
                invitationHistorySummaryQueryProvider.getById(InvitationHistorySummaryByIdRequest.builder().employeeId(employeeId).build()).getContext();

        //查询当月邀新数据
        InvitationStatisticsListResponse statisticsListResponse =
                invitationStatisticsQueryProvider.getMonthByEmployeeId(InvitationRegisterStatisticsRequest.builder().employeeId(employeeId).build()).getContext();

        List<InvitationStatisticsVO> invitationStatisticsVOList =
                statisticsListResponse.getInvitationStatisticsVOList();
        String nowFormat = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        //当天
        InvitationHistorySummaryVO today = new InvitationHistorySummaryVO();
        //当月
        InvitationHistorySummaryVO month = new InvitationHistorySummaryVO();
        //当前历史+今天
        InvitationHistorySummaryVO totalHistory = new InvitationHistorySummaryVO();

        if (CollectionUtils.isNotEmpty(invitationStatisticsVOList)) {
            //设置当天
            Long todayResultsCount = 0L;
            Long todayTradeGoodsTotal = 0L;
            BigDecimal todayTradePriceTotal = BigDecimal.ZERO;
            Long todayTradeTotal = 0L;
            Optional<InvitationStatisticsVO> statisticsVO =
                    invitationStatisticsVOList.stream().filter(c -> nowFormat.equals(c.getDate())).findFirst();
            if (statisticsVO.isPresent()) {
                InvitationStatisticsVO invitationStatisticsVO = statisticsVO.get();
                todayResultsCount = invitationStatisticsVO.getResultsCount();
                todayTradeGoodsTotal = invitationStatisticsVO.getTradeGoodsTotal();
                todayTradePriceTotal = invitationStatisticsVO.getTradePriceTotal();
                todayTradeTotal = invitationStatisticsVO.getTradeTotal();
            }
            today.setTotalCount(todayResultsCount);
            today.setTotalGoodsCount(todayTradeGoodsTotal);
            today.setTotalTradePrice(todayTradePriceTotal);
            today.setTradeTotal(todayTradeTotal);

            Long resultsCount =
                    invitationStatisticsVOList.stream().mapToLong(InvitationStatisticsVO::getResultsCount).sum();
            Long tradeGoodsTotal =
                    invitationStatisticsVOList.stream().mapToLong(InvitationStatisticsVO::getTradeGoodsTotal).sum();
            Long tradeTotal =
                    invitationStatisticsVOList.stream().mapToLong(InvitationStatisticsVO::getTradeTotal).sum();
            BigDecimal tradePriceTotal =
                    invitationStatisticsVOList.stream().map(InvitationStatisticsVO::getTradePriceTotal).reduce(BigDecimal.ZERO,
                            BigDecimal::add);
            month.setTotalCount(resultsCount);
            month.setTotalGoodsCount(tradeGoodsTotal);
            month.setTradeTotal(tradeTotal);
            month.setTotalTradePrice(tradePriceTotal);

            //总计邀新数据  历史数据+今天的数据
            InvitationHistorySummaryVO totalInvitationSummaryVO =
                    summaryByIdResponseBaseResponse.getTotalInvitationSummaryVO();

            Long totalCount = totalInvitationSummaryVO.getTotalCount();
            Long totalGoodsCount = totalInvitationSummaryVO.getTotalGoodsCount();
            BigDecimal totalTradePrice = totalInvitationSummaryVO.getTotalTradePrice();
            Long tradeTotal1 = totalInvitationSummaryVO.getTradeTotal();

            totalHistory.setTotalCount(todayResultsCount + totalCount);
            totalHistory.setTotalGoodsCount(todayTradeGoodsTotal + totalGoodsCount);
            totalHistory.setTradeTotal(todayTradeTotal + tradeTotal1);
            totalHistory.setTotalTradePrice(todayTradePriceTotal.add(totalTradePrice));
        } else {

            InvitationHistorySummaryVO totalInvitationSummaryVO =
                    summaryByIdResponseBaseResponse.getTotalInvitationSummaryVO();

            Long totalCount = totalInvitationSummaryVO.getTotalCount();
            Long totalGoodsCount = totalInvitationSummaryVO.getTotalGoodsCount();
            BigDecimal totalTradePrice = totalInvitationSummaryVO.getTotalTradePrice();
            Long tradeTotal = totalInvitationSummaryVO.getTradeTotal();
            totalHistory.setTotalCount(totalCount);
            totalHistory.setTotalGoodsCount(totalGoodsCount);
            totalHistory.setTradeTotal(tradeTotal);
            totalHistory.setTotalTradePrice(totalTradePrice);

        }
        response.setTodayInvitationSummaryVO(today);
        response.setMonthInvitationSummaryVO(month);
        response.setTotalInvitationSummaryVO(totalHistory);
        return BaseResponse.success(response);
    }

    @ApiOperation(value = "查看用户是否是员工")
    @RequestMapping(value = "/queryBuyerByPhone/{phone}", method = RequestMethod.GET)
    public BaseResponse<EmployeeByMobileNewResponse> queryBuyerByPhone(@PathVariable String phone) {
        EmployeeByMobileNewResponse employee =
                Optional.ofNullable(
                        employeeQueryProvider.getBuyerByMobile(
                                EmployeeByMobileRequest.builder().accountType(AccountType.s2bBoss).mobile(phone).build()
                        ).getContext()).orElse(null);
        if (Objects.isNull(employee) || DeleteFlag.YES.equals(employee.getDelFlag())
                || AccountState.DISABLE.equals(employee.getAccountState()) || employee.getIsEmployee() != 0) {
            employee = null;
        }
        return BaseResponse.success(employee);
    }

}

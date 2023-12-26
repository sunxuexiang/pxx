package com.wanmi.sbc.job.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.response.customer.CustomerIdAllResponse;
import com.wanmi.sbc.order.api.provider.trade.ProviderTradeQueryProvider;
import com.wanmi.sbc.order.api.response.trade.TradeListExportResponse;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.order.service.TradeExportService;
import com.wanmi.sbc.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jeffrey
 * @create 2021-08-19 10:17
 */

@Service
@Slf4j
public class GetNewUserService {

    @Autowired
    private ProviderTradeQueryProvider providerTradeQueryProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;


    public void getNewUser() {
        //1. 把前天所有的订单成功有用户查出来(从2018年开始到前天的所有订单)
        BaseResponse<TradeListExportResponse> beforeYesterdayData = providerTradeQueryProvider.getBeforeYesterdayData();
        List<TradeVO> tradeVOLists = beforeYesterdayData.getContext().getTradeVOList();
        Map<String, List<TradeVO>> map = tradeVOLists.stream()
                .collect(Collectors.groupingBy(tradeVO -> tradeVO.getBuyer().getId()));
        List<String> buyerIds = map.keySet().stream().map(x -> x).collect(Collectors.toList());
        //2.把所有的用户查出来
        BaseResponse<CustomerIdAllResponse> allCustomerId = customerQueryProvider.getAllCustomerId();
        List<String> customerIds = allCustomerId.getContext().getCustomerIds();
        //取差集,得到新用户ID
        customerIds.removeAll(buyerIds);
        log.info("查找出新用户: {}", JSON.toJSONString(customerIds));
        //3.在订单中查找T-1天中是否有下单记录(订单状态已支付,且第一单)
        BaseResponse<TradeListExportResponse> yesterdayData = providerTradeQueryProvider.getYesterdayData();
        List<TradeVO> newTrades = yesterdayData.getContext().getTradeVOList();

        //4. 过滤出新用户的订单
        Map<String, List<TradeVO>> newMap = newTrades.stream().collect(Collectors.groupingBy(tradeVO -> tradeVO.getBuyer().getId()));
        //5. 得到最终的订单数据
        List<TradeVO> resultTradeList = Lists.newArrayList();
        for (String customId : newMap.keySet()) {
            if (customerIds.contains(customId)) {
                List<TradeVO> tradeVOS = newMap.get(customId);
                if (!tradeVOS.isEmpty() && tradeVOS.size() >= 1) {
                    //取首单
                    resultTradeList.add(tradeVOS.get(0));
                }
            }
        }
        //6. 转换bean
        List<TradeVO> tradeVOS = KsBeanUtil.convert(resultTradeList, TradeVO.class);
        //按下单时间降序排列
        Comparator<TradeVO> c = Comparator.comparing(a -> a.getTradeState().getCreateTime());
        tradeVOS = tradeVOS.stream().sorted(
                c.reversed()
        ).collect(Collectors.toList());
       //封装成excel并发邮件
        try {
            providerTradeQueryProvider.sendNewUserOrder(TradeListExportResponse.builder().tradeVOList(tradeVOS).build());
        } catch (Exception e) {
            log.error("发送邮件失败",e);
        }
    }
}

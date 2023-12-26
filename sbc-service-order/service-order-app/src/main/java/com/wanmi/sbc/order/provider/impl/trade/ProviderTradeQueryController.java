package com.wanmi.sbc.order.provider.impl.trade;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.order.api.provider.trade.ProviderTradeQueryProvider;
import com.wanmi.sbc.order.api.request.trade.*;
import com.wanmi.sbc.order.api.response.trade.TradeGetByIdResponse;
import com.wanmi.sbc.order.api.response.trade.TradeListByParentIdResponse;
import com.wanmi.sbc.order.api.response.trade.TradeListExportResponse;
import com.wanmi.sbc.order.api.response.trade.TradePageCriteriaResponse;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.order.trade.model.entity.TradeState;
import com.wanmi.sbc.order.trade.model.entity.value.Buyer;
import com.wanmi.sbc.order.trade.model.root.ProviderTrade;
import com.wanmi.sbc.order.trade.model.root.Trade;
import com.wanmi.sbc.order.trade.request.ProviderTradeQueryRequest;
import com.wanmi.sbc.order.trade.service.ProviderTradeService;
import com.wanmi.sbc.order.trade.service.TradeEmailService;
import com.wanmi.sbc.setting.api.provider.EmailConfigProvider;
import com.wanmi.sbc.setting.api.response.EmailConfigQueryResponse;
import com.wanmi.sbc.setting.bean.enums.EmailStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 供应商订单处理
 * @Autho qiaokang
 * @Date：2020-03-27 09:17
 */
@Validated
@RestController
@Slf4j
public class ProviderTradeQueryController implements ProviderTradeQueryProvider {

    @Autowired
    private ProviderTradeService providerTradeService;

    @Value("${send.order.newUser}")
    private String emailList;

    /**
     * 分页查询供应商订单
     *
     * @param tradePageCriteriaRequest 带参分页参数
     * @return
     */
    @Override
    public BaseResponse<TradePageCriteriaResponse> providerPageCriteria(@RequestBody @Valid ProviderTradePageCriteriaRequest tradePageCriteriaRequest) {
        ProviderTradeQueryRequest tradeQueryRequest = KsBeanUtil.convert(
                tradePageCriteriaRequest.getTradePageDTO(), ProviderTradeQueryRequest.class);
        Criteria criteria;
        if (tradePageCriteriaRequest.isReturn()) {
            criteria = tradeQueryRequest.getCanReturnCriteria();
        } else {
            criteria = tradeQueryRequest.getWhereCriteria();
        }
        Page<ProviderTrade> page = providerTradeService.providerPage(criteria, tradeQueryRequest);
        MicroServicePage<TradeVO> tradeVOS = KsBeanUtil.convertPage(page, TradeVO.class);
        return BaseResponse.success(TradePageCriteriaResponse.builder().tradePage(tradeVOS).build());
    }

    /**
     * 查询供应商订单
     *
     * @param tradeGetByIdRequest 交易单id {@link TradeGetByIdRequest}
     * @return
     */
    @Override
    public BaseResponse<TradeGetByIdResponse> providerGetById(@RequestBody @Valid TradeGetByIdRequest tradeGetByIdRequest) {
        ProviderTrade trade = providerTradeService.providerDetail(tradeGetByIdRequest.getTid());

        TradeVO tradeVO = KsBeanUtil.convert(trade, TradeVO.class);
        if (tradeVO != null && CollectionUtils.isNotEmpty(tradeVO.getTradeItems())) {
            List<ProviderTrade> providerTradeList = providerTradeService.findListByParentId(trade.getParentId());

            StringBuffer subOrderNo = new StringBuffer();
            if (CollectionUtils.isNotEmpty(providerTradeList)) {
                for (int i = 0; i < providerTradeList.size(); i++) {
                    if (i == providerTradeList.size() - 1) {
                        subOrderNo.append(providerTradeList.get(i).getId());
                    } else {
                        subOrderNo.append(providerTradeList.get(i).getId() + ",");
                    }
                }
            }

//            for (TradeItemVO item : tradeVO.getTradeItems()) {
//                item.setSubOrderNo(subOrderNo.toString());
//                Long providerId = item.getProviderId();
//                item.setProviderTradeId(providerTradeList.stream().filter(providerTrade ->
//                        providerTrade.getProvider().getStoreId().equals(providerId)
//                ).collect(Collectors.toList()).get(0).getId());
//            }

        }

        BaseResponse<TradeGetByIdResponse> baseResponse = BaseResponse.success(TradeGetByIdResponse.builder().
                tradeVO(tradeVO).build());
        return baseResponse;
    }

    /**
     * 根据父订单号查询供应商订单
     *
     * @param tradeListByParentIdRequest 父交易单id {@link TradeListByParentIdRequest}
     * @return
     */
    @Override
    public BaseResponse<TradeListByParentIdResponse> getProviderListByParentId(@RequestBody @Valid TradeListByParentIdRequest tradeListByParentIdRequest) {
        List<ProviderTrade> tradeList =
                providerTradeService.findListByParentId(tradeListByParentIdRequest.getParentTid());
        if (tradeList.isEmpty()) {
            return BaseResponse.success(TradeListByParentIdResponse.builder().tradeVOList(Collections.emptyList()).build());
        }
        // 父订单号对应的子订单的买家信息应该是相同的
        ProviderTrade trade = tradeList.get(0);

        final Buyer buyer = trade.getBuyer();
        //统一设置账号加密后的买家信息
        List<TradeVO> tradeVOList = tradeList.stream().map(i -> {
            i.setBuyer(buyer);
            return KsBeanUtil.convert(i, TradeVO.class);
        }).collect(Collectors.toList());
        return BaseResponse.success(TradeListByParentIdResponse.builder().tradeVOList(tradeVOList).build());
    }

    /**
     * 查询导出数据
     *
     * @param tradeListExportRequest 查询条件 {@link TradeListExportRequest}
     * @return
     */
    @Override
    public BaseResponse<TradeListExportResponse> providerListTradeExport(@RequestBody @Valid TradeListExportRequest tradeListExportRequest) {
        ProviderTradeQueryRequest tradeQueryRequest = KsBeanUtil.convert(tradeListExportRequest.getTradeQueryDTO(),
                ProviderTradeQueryRequest.class);
        List<ProviderTrade> tradeList = providerTradeService.listProviderTradeExport(tradeQueryRequest);
        return BaseResponse.success(TradeListExportResponse.builder().tradeVOList(KsBeanUtil.convert(tradeList,
                TradeVO.class)).build());
    }


    /**
     * 查询导出订单数据(需求是:导出T-1天的新用户首单数据)
     * 1. 查询T-2天所有的订单数据
     * 2. 最终要排队掉这些已下单成功的老用户
     */
    @Override
    public BaseResponse<TradeListExportResponse> getBeforeYesterdayData() {

        List<Trade> tradeList = providerTradeService.queryOldUser();

        return BaseResponse.success(TradeListExportResponse.builder().tradeVOList(KsBeanUtil.convert(tradeList,
                TradeVO.class)).build());
    }


    /**
     * 1. 查询T-1天所有的订单数据
     * 2. 传新用户的条件进来
     */
    @Override
    public BaseResponse<TradeListExportResponse> getYesterdayData() {
        List<Trade> tradeList = providerTradeService.queryNewUser();
        return BaseResponse.success(TradeListExportResponse.builder().tradeVOList(KsBeanUtil.convert(tradeList,
                TradeVO.class)).build());
    }

    /**
     * 发送新用户首单的邮件
     * @return
     */
    @Override
    public BaseResponse sendNewUserOrder(TradeListExportResponse tradeListExportResponse) {
        log.info("获取邮件地址列表用来发送新用户首单,{}",emailList);
        List<String> emails = Lists.newArrayList();
        if(StringUtils.isNotBlank(emailList)){
            String[] split = emailList.split(",");
            for (String s : split) {
                emails.add(s);
            }
        }
        providerTradeService.sendNewUserOrder(tradeListExportResponse.getTradeVOList(),emails);
        return BaseResponse.SUCCESSFUL();
    }
}

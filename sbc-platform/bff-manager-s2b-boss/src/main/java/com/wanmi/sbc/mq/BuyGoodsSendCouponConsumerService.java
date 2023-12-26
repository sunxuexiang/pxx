package com.wanmi.sbc.mq;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.account.bean.enums.WalletDetailsType;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MessageMQRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.node.AccoutAssetsType;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.api.provider.coupon.CoinActivityProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityProvider;
import com.wanmi.sbc.marketing.api.request.coupon.BuyGoodsOrFullOrderSendCouponRequest;
import com.wanmi.sbc.marketing.api.response.coupon.BuyGoodsOrFullOrderSendCouponResponse;
import com.wanmi.sbc.marketing.api.response.coupon.GetCouponGroupResponse;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDetailDto;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDto;
import com.wanmi.sbc.marketing.bean.dto.TradeItemInfoDTO;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import com.wanmi.sbc.marketing.bean.vo.CoinActivityVO;
import com.wanmi.sbc.marketing.bean.vo.CouponCodeVO;
import com.wanmi.sbc.message.bean.enums.NodeType;
import com.wanmi.sbc.order.api.constant.JmsDestinationConstants;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.trade.CoinActivityPushKingdeeRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.api.request.trade.TradeUpdateRequest;
import com.wanmi.sbc.order.bean.dto.TradeUpdateDTO;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.bean.vo.BuyerVO;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.util.OperateLogMQUtil;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.WalletMerchantProvider;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletGiveRequest;
import com.wanmi.sbc.wallet.api.request.wallet.WalletInfoRequest;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description: 购买指定商品赠券 service 消费者
 * @author: XinJiang
 * @time: 2022/2/11 10:14
 */
@Service
@Slf4j
@EnableBinding(BossSink.class)
public class BuyGoodsSendCouponConsumerService {

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private CouponActivityProvider couponActivityProvider;

    @Autowired
    private MessageSendProducer messageSendProducer;


    @Autowired
    private CoinActivityProvider coinActivityProvider;
    
    @Autowired
    private WalletMerchantProvider walletMerchantProvider;
    
    @Autowired
    private CustomerWalletProvider customerWalletProvider;
    
	@Autowired
	private OperateLogMQUtil operateLogMQUtil;



    @StreamListener(JmsDestinationConstants.Q_BUY_GOODS_SEND_COUPON)
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public void buyGoodsSendCoupon(String tid){
        log.info("==========购买指定商品赠券开始：：：{}，订单号：{}", LocalDateTime.now(),tid);

        TradeVO tradeVO = tradeQueryProvider.getOrderById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        if (CollectionUtils.isNotEmpty(tradeVO.getSendCouponCodeIds())) {
            //throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"已赠券!!!!!!!!");
            log.info("==========购买指定商品赠券:::订单号：{}, 已赠券!!!!!!!!", tid);
        }else {
            if (tradeVO.getTradeState().getPayState().equals(PayState.PAID)) {
                BuyGoodsOrFullOrderSendCouponRequest request = new BuyGoodsOrFullOrderSendCouponRequest();
                request.setCustomerId(tradeVO.getBuyer().getId());
                request.setStoreId(tradeVO.getSupplier().getStoreId());
                request.setType(CouponActivityType.BUY_ASSIGN_GOODS_COUPON);
                request.setTradeItemInfoDTOS(KsBeanUtil.convert(tradeVO.getTradeItems(), TradeItemInfoDTO.class));
                List<BuyGoodsOrFullOrderSendCouponResponse> listResponse = couponActivityProvider.getBuyGoodsSendCouponGroup(request).getContext();
                if (CollectionUtils.isNotEmpty(listResponse)) {
                    //todo 短信/站内信通知发送券信息
                    List<GetCouponGroupResponse> listCoupons = new ArrayList<>();
                    listResponse.forEach(coupons -> {
                        if (CollectionUtils.isNotEmpty(coupons.getCouponList())) {
                            listCoupons.addAll(coupons.getCouponList());
                        }
                    });
                    if (CollectionUtils.isNotEmpty(listCoupons)) {
                        this.handleCoupon(listCoupons,AccoutAssetsType.COUPON_RECEIPT,tradeVO.getBuyer());
                    }

                    List<CouponCodeVO> couponCodeVOS = new ArrayList<>();
                    listResponse.forEach(coupons -> {
                        if (CollectionUtils.isNotEmpty(coupons.getCouponCodeVOS())) {
                            couponCodeVOS.addAll(coupons.getCouponCodeVOS());
                        }
                    });
                    if (CollectionUtils.isNotEmpty(couponCodeVOS)) {
                        tradeVO.setSendCouponCodeIds(couponCodeVOS.stream().map(CouponCodeVO::getCouponCodeId).collect(Collectors.toList()));
                        tradeProvider.update(TradeUpdateRequest.builder().trade(KsBeanUtil.convert(tradeVO, TradeUpdateDTO.class)).build());
                    }
                    log.info("==========赠券完成：：：{}",LocalDateTime.now());
                } else {
                    log.info("==========未满足赠券条件：：：{}",LocalDateTime.now());
                }
            }
        }

        log.info("==========购买指定商品赠券结束：：：{}",LocalDateTime.now());


        if (CollectionUtils.isNotEmpty(tradeVO.getSendCoinRecordIds())) {
            log.info("==========购买指定商品赠鲸币:::订单号：{}, 已赠鲸币!!!!!!!!", tid);
        } else {
            if (tradeVO.getTradeState().getPayState().equals(PayState.PAID) && Objects.equals("0", tradeVO.getActivityType())) {
                this.sendCoin(tradeVO);
            }

        }
        
        // 订单返鲸币
        coinActivityProvider.sendOrderCoin(tid);

    }

    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public void sendCoin(TradeVO tradeVO) {
        String tid = tradeVO.getId();
        log.info("==========购买指定商品赠鲸币开始：：：{}，订单号：{}", LocalDateTime.now(),tid);
        // 是否满足赠鲸币活动
        List<String> skuIds = tradeVO.getTradeItems().stream().map(TradeItemVO::getSkuId).collect(Collectors.toList());
        List<CoinActivityVO> coinActivityVOS = coinActivityProvider.queryCoinActivityBySkuIds(skuIds).getContext();
        if (CollectionUtils.isNotEmpty(coinActivityVOS)) {
            Boolean exist = coinActivityProvider.queryCoinActivityRecordIsExist(tid).getContext();
            if (Objects.nonNull(exist) && exist) {
                log.info("==========购买指定商品赠鲸币：重复消费，订单号：{}",tid);
                return;
            }
            
            String customerAccount = tradeVO.getBuyer().getAccount();
            LocalDateTime now = LocalDateTime.now();
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtil.FMT_TIME_3, Locale.UK);
//            String sendNo = "ZS" + now.format(dateTimeFormatter) + RandomStringUtils.randomNumeric(4);
            BigDecimal orderPrice = tradeVO.getTradePrice().getTotalPrice();
            LocalDateTime orderTime = tradeVO.getTradeState().getCreateTime();

            Map<String, TradeItemVO> itemVOMap = tradeVO.getTradeItems().stream().collect(Collectors.toMap(TradeItemVO::getSkuId, Function.identity(), (o1, o2) -> o1));

            // 赠送记录
            Map<String, List<CoinActivityVO>> map = coinActivityVOS.stream().collect(Collectors.groupingBy(CoinActivityVO::getActivityId));
            List<CoinActivityRecordDto> request = new ArrayList<>();
            map.forEach((activityId,activityList) -> {
                CoinActivityRecordDto recordDto = new CoinActivityRecordDto();
//                recordDto.setSendNo(sendNo);
                recordDto.setActivityId(activityId);
                recordDto.setCustomerAccount(customerAccount);
                recordDto.setOrderNo(tid);
                recordDto.setOrderTime(orderTime);
                recordDto.setOrderPrice(orderPrice);
                recordDto.setRecordType(1);
                recordDto.setRecordTime(now);

                List<CoinActivityRecordDetailDto> detailDtoList = new ArrayList<>();
                for (CoinActivityVO vo : activityList) {
                    String goodsInfoId = vo.getGoodsInfoId();
                    CoinActivityRecordDetailDto detailDto = new CoinActivityRecordDetailDto();
                    detailDto.setActivityId(activityId);
                    detailDto.setOrderNo(tid);
                    detailDto.setGoodsInfoId(goodsInfoId);
                    detailDto.setRecordType(1);
                    detailDto.setRecordTime(now);
                    BigDecimal coinNum = vo.getCoinNum();
                    detailDto.setIsOverlap(vo.getIsOverlap());
                    Long buyNum = itemVOMap.get(goodsInfoId).getNum();
                    detailDto.setGoodsNum(buyNum);
                    if (Objects.equals(DefaultFlag.YES, vo.getIsOverlap())) {
                        detailDto.setSingleCoinNum(vo.getCoinNum());
                        coinNum = coinNum.multiply(BigDecimal.valueOf(buyNum)).setScale(2, RoundingMode.HALF_UP);
                    }
                    detailDto.setCoinNum(coinNum);
                    detailDtoList.add(detailDto);
                    itemVOMap.get(goodsInfoId).setReturnCoin(coinNum);
                }
                BigDecimal totalCoinNum = detailDtoList.stream().map(CoinActivityRecordDetailDto::getCoinNum).reduce(BigDecimal.ZERO, BigDecimal::add);
                recordDto.setCoinNum(totalCoinNum);
                recordDto.setDetailList(detailDtoList);
                request.add(recordDto);
            });

            // 修改钱包
            BigDecimal totalCoin = request.stream().map(CoinActivityRecordDto::getCoinNum).reduce(BigDecimal.ZERO, BigDecimal::add);          
			// 返鲸币商家鲸币余额减少，用户鲸币余额增加
            Long storeId = tradeVO.getSupplier().getStoreId();
			WalletInfoRequest req = WalletInfoRequest.builder().storeFlag(true).storeId(storeId.toString()).build();
			BaseResponse<CusWalletVO> walletResp = customerWalletProvider.queryCustomerWallet(req);
			if (walletResp.getContext().getBalance().compareTo(totalCoin) >= 0) {
				String remark = WalletDetailsType.BUY_GOODS_SEND.getDesc() + "-" + tid;
				String customerId = tradeVO.getBuyer().getId();
				CustomerWalletGiveRequest walletGiveRequest = CustomerWalletGiveRequest.builder().customerId(customerId)
						.storeId(storeId.toString()).relationOrderId(tid).tradeRemark(remark).remark(remark).balance(totalCoin)
						.opertionType(0)
						.walletRecordTradeType(WalletRecordTradeType.ORDER_CASH_BACK)
						.build();
				BaseResponse<WalletRecordVO> merchantGiveUser = walletMerchantProvider.merchantGiveUser(walletGiveRequest);
				
				// 保存赠送记录
				request.stream().forEach(e -> e.setSendNo(merchantGiveUser.getContext().getSendNo()));
	            List<Long> recordIdList = coinActivityProvider.saveCoinRecord(request).getContext();
				
	            // 推送金蝶
//	            CoinActivityPushKingdeeRequest kingdeeRequest = CoinActivityPushKingdeeRequest
//	                    .builder()
//	                    .tid(tid)
//	                    .buyerAccount(customerAccount)
//	                    .applyPrice(totalCoin)
//	                    .saleType(tradeVO.getSaleType())
//	                    .sendNo(sendNo)
//	                    .build();
//
//	            tradeProvider.pushOrderKingdeeForCoin(kingdeeRequest);

	            tradeVO.setSendCoinRecordIds(recordIdList);
	            tradeVO.setReturnCoin(totalCoin);
	            tradeProvider.update(TradeUpdateRequest.builder().trade(KsBeanUtil.convert(tradeVO, TradeUpdateDTO.class)).build());
			} else {
				// 商家鲸币不足无法赠送鲸币
				// 商家鲸币不足时自动终止返鲸币活动，这里简单处理终止该订单相关的所有返鲸币活动
				map.keySet().forEach(k -> {
					String operatorId = "boss";
					coinActivityProvider.terminationById(k, operatorId);
					String format = MessageFormat.format("订单[{0}]商家[{1}]鲸币[{2}]不足无法赠送鲸币[{3}],系统自动终止返鲸币活动[{4}]", tid, storeId.toString(), walletResp.getContext().getBalance(), totalCoin, k);
					log.warn(format);
					operateLogMQUtil.convertAndSend("营销", "终止返鲸币活动", format, null);
				});
		
			}

        }else {
            log.info("==========未满足赠券条件：：：{},订单号：：{}",LocalDateTime.now(),tid);
        }


    }

    /**
     * 优惠券到账通知
     * @param listCoupons
     * @param nodeCode
     * @param buyer
     */
    private void handleCoupon(List<GetCouponGroupResponse> listCoupons, AccoutAssetsType nodeCode, BuyerVO buyer){
        List<String> params = new ArrayList<>();
        Long total = listCoupons.stream().mapToLong(GetCouponGroupResponse::getTotalCount).sum();
        BigDecimal sum = listCoupons.stream().reduce(BigDecimal.ZERO,(x,y)-> x.add(y.getDenomination().multiply(new BigDecimal(y.getTotalCount()))),BigDecimal::add);
        params.add(total.toString());
        params.add(sum.toString());
        this.sendMessage(NodeType.ACCOUNT_ASSETS, nodeCode, params, null, buyer.getId(), buyer.getAccount());
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
    

}

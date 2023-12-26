package com.wanmi.sbc.advertising.service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.advertising.api.request.activity.ActAuditRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActPayCallBackRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActPayRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryActiveActRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryAddedListRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryConflictListRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActQueryListPageRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActSaveRequest;
import com.wanmi.sbc.advertising.api.request.activity.ActUpdateRequest;
import com.wanmi.sbc.advertising.api.request.activity.AdRefundCallbackRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotBatchSetPriceRequest;
import com.wanmi.sbc.advertising.api.request.slot.SlotSaveRequest;
import com.wanmi.sbc.advertising.bean.constant.AdConstants;
import com.wanmi.sbc.advertising.bean.dto.AdActivityDTO;
import com.wanmi.sbc.advertising.bean.dto.AdSlotDTO;
import com.wanmi.sbc.advertising.bean.dto.AdSlotPriceDTO;
import com.wanmi.sbc.advertising.bean.dto.SlotSeqDTO;
import com.wanmi.sbc.advertising.bean.enums.ActivityState;
import com.wanmi.sbc.advertising.bean.enums.ClickJumpType;
import com.wanmi.sbc.advertising.bean.enums.PayType;
import com.wanmi.sbc.advertising.bean.enums.SlotSeqState;
import com.wanmi.sbc.advertising.bean.enums.SlotState;
import com.wanmi.sbc.advertising.bean.enums.SlotType;
import com.wanmi.sbc.advertising.bean.enums.UnitType;
import com.wanmi.sbc.advertising.config.ConfigUtil;
import com.wanmi.sbc.advertising.model.AdActivity;
import com.wanmi.sbc.advertising.model.AdPayRecord;
import com.wanmi.sbc.advertising.model.AdRefundRecord;
import com.wanmi.sbc.advertising.model.AdSlot;
import com.wanmi.sbc.advertising.model.AdSlotPrice;
import com.wanmi.sbc.advertising.redis.RedisCache;
import com.wanmi.sbc.advertising.redis.RedisKeyConstants;
import com.wanmi.sbc.advertising.repository.AdActivityRepository;
import com.wanmi.sbc.advertising.repository.AdPayRecordRepository;
import com.wanmi.sbc.advertising.repository.AdRefundRecordRepository;
import com.wanmi.sbc.advertising.repository.AdSlotPriceRepository;
import com.wanmi.sbc.advertising.repository.AdSlotRepository;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.tencent.TencentImCustomerUtil;
import com.wanmi.sbc.common.util.tencent.TencentImMessageType;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyMallSupplierTabListQueryRequest;
import com.wanmi.sbc.customer.bean.enums.MallOpenStatus;
import com.wanmi.sbc.customer.bean.vo.CompanyMallSupplierTabVO;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateListByConditionRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateListByConditionResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.pay.api.provider.CcbPayProvider;
import com.wanmi.sbc.pay.api.request.CcbPayOrderRequest;
import com.wanmi.sbc.pay.api.request.CcbRefundAdRequest;
import com.wanmi.sbc.pay.api.request.CcbSubPayOrderRequest;
import com.wanmi.sbc.pay.api.response.CcbRefundAdResponse;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.WalletMerchantProvider;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletGiveRequest;
import com.wanmi.sbc.wallet.api.request.wallet.WalletInfoRequest;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@EnableBinding
public class AdActivityService {

	@Autowired
	private AdActivityRepository adActivityRepository;

	@Autowired
	private AdSlotRepository adSlotRepository;

	@Autowired
	private GeneratorService generatorService;

	@Autowired
	private CustomerWalletProvider customerWalletProvider;

	@Autowired
	private AdPayRecordRepository adPayRecordRepository;

	@Autowired
	private AdRefundRecordRepository adRefundRecordRepository;


	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private BinderAwareChannelResolver resolver;

	@Autowired
	private ConfigUtil configUtil;

	@Autowired
	private WalletMerchantProvider walletMerchantProvider;

	@Autowired
	private SystemConfigQueryProvider systemConfigQueryProvider;
	
	@Autowired
	private CcbPayProvider ccbPayProvider;

	@Autowired
	private ThreadPoolTaskExecutor sendMqMsgTaskExecutor;
	
	@Autowired
	private RedisCache redisCache;
	
	@Autowired
	private AdSlotPriceRepository adSlotPriceRepository;
	
    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;
    
    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;
	
	
	public List<SlotSeqDTO> queryAvailableSlotSeq(ActQueryAddedListRequest request) {
		// 查出所有上架的广告位
		List<AdSlotDTO> addedList = adSlotRepository.queryAddedList(request);
		List<SlotSeqDTO> list = new ArrayList<>();
		for (AdSlotDTO slot : addedList) {
			SlotSeqDTO seqDTO = new SlotSeqDTO();
			seqDTO.setSlotId(slot.getId());
			seqDTO.setSlotGroupSeq(slot.getSlotGroupSeq());
			List<AdSlotPrice> availableList = adSlotPriceRepository.queryAvailableList(slot.getId());
			if (CollectionUtils.isNotEmpty(availableList)) {
				seqDTO.setSlotSeqState(SlotSeqState.AVAILABLE);
			}else {
				List<AdSlotPrice> occupiedList = adSlotPriceRepository.queryOccupiedList(slot.getId());
				if (CollectionUtils.isNotEmpty(occupiedList)) {
					seqDTO.setSlotSeqState(SlotSeqState.OCCUPIED);
				}else {
					seqDTO.setSlotSeqState(SlotSeqState.NOT_AVAILABLE);
				}
			}
			list.add(seqDTO);
		}
		return list;
	}
	
	public List<AdSlotPrice> queryAvailableTime(Integer slotId) {
		List<AdSlotPrice> queryAvailableList = adSlotPriceRepository.queryAvailableList(slotId);
		return queryAvailableList;
	}

//	public List<AdSlotDTO> queryAvailableTime(ActQueryAddedListRequest request) {
		// 查出所有上架的广告位时间，结束适用时间要大于 当前时间+1天
//		List<AdSlotDTO> addedList = adSlotRepository.queryAddedList(request);
//		// 已经上架的广告位开始适用时间小于当前时间的替换为明天开始
//		addedList.forEach(e -> {
//			Date now = new Date();
//			if (e.getApplicableTime1().compareTo(now) < 0) {
//				Date truncate = DateUtils.truncate(now, Calendar.DAY_OF_MONTH);
//				Date addDays = DateUtils.addDays(truncate, 1);
//				e.setApplicableTime1(addDays);
//			}
//		});
//
//		// 查出不可用的时间，包括待支付，待审核，投放成功
//		List<AdActivityDetailDTO> unavailableList = adActivityRepository.queryUnavailableTimeList(request);
//		// 计算
//		calcAvailableTime(addedList, unavailableList);
//		// 排序
//		addedList.sort(Comparator.comparing(AdSlotDTO::getId));
//		return addedList;
//	}

//	private void calcAvailableTime(List<AdSlotDTO> collect, List<AdActivityDetailDTO> unavailableList) {
//		if (CollectionUtils.isEmpty(collect)) {
//			return;
//		}
//		if (CollectionUtils.isEmpty(unavailableList)) {
//			return;
//		}
//
//		boolean needContinue = false;
//		ListIterator<AdSlotDTO> baseIterator = collect.listIterator();
//		outerLoop: while (baseIterator.hasNext()) {
//			AdSlotDTO applicableTimeDTO = baseIterator.next();
//			ListIterator<AdActivityDetailDTO> iterator = unavailableList.listIterator();
//			while (iterator.hasNext()) {
//				AdActivityDetailDTO adActivityDTO = iterator.next();
//				// 判断有没有交集
//				boolean hasIntersection = !(applicableTimeDTO.getApplicableTime1().compareTo(adActivityDTO.getEndTime()) > 0
//						|| applicableTimeDTO.getApplicableTime2().compareTo(adActivityDTO.getStartTime()) < 0);
//				if (hasIntersection) {
//					log.info("广告位[{}]时间[{}~{}]与广告活动详情[{}]时间[{}~{}]存在交集", applicableTimeDTO.getId(), applicableTimeDTO.getApplicableTime1(),
//							applicableTimeDTO.getApplicableTime2(), adActivityDTO.getId(), adActivityDTO.getStartTime(), adActivityDTO.getEndTime());
//					// 算出交集
//					Date intersectionStart = applicableTimeDTO.getApplicableTime1().compareTo(adActivityDTO.getStartTime()) >= 0
//							? applicableTimeDTO.getApplicableTime1()
//							: adActivityDTO.getStartTime();
//					Date intersectionEnd = applicableTimeDTO.getApplicableTime2().compareTo(adActivityDTO.getEndTime()) <= 0
//							? applicableTimeDTO.getApplicableTime2()
//							: adActivityDTO.getEndTime();
//
//					// 得到差集即为可用时间
//					// 1.*******|___________________|
//					// |________________|
//					if (intersectionStart.compareTo(applicableTimeDTO.getApplicableTime1()) == 0
//							&& intersectionEnd.compareTo(applicableTimeDTO.getApplicableTime2()) < 0) {
//						log.info("时间[{}~{}]变为", applicableTimeDTO.getApplicableTime1(), applicableTimeDTO.getApplicableTime2());
//						Date newTime1 = DateUtils.addSeconds(intersectionEnd, 1);
//						applicableTimeDTO.setApplicableTime1(newTime1);
//						log.info("[{}~{}]", applicableTimeDTO.getApplicableTime1(), applicableTimeDTO.getApplicableTime2());
//					}
//					// 2. |___________________|
//					// ***********|________________|
//					else if (intersectionStart.compareTo(applicableTimeDTO.getApplicableTime1()) > 0
//							&& intersectionEnd.compareTo(applicableTimeDTO.getApplicableTime2()) == 0) {
//						log.info("时间[{}~{}]变为", applicableTimeDTO.getApplicableTime1(), applicableTimeDTO.getApplicableTime2());
//						Date newTime2 = DateUtils.addSeconds(intersectionStart, -1);
//						applicableTimeDTO.setApplicableTime2(newTime2);
//						log.info("[{}~{}]", applicableTimeDTO.getApplicableTime1(), applicableTimeDTO.getApplicableTime2());
//					}
//					// 3. |___________________|
//					// ********|___________|
//					else if (intersectionStart.compareTo(applicableTimeDTO.getApplicableTime1()) > 0
//							&& intersectionEnd.compareTo(applicableTimeDTO.getApplicableTime2()) < 0) {
//						log.info("时间[{}~{}]，重新开始计算，变为", applicableTimeDTO.getApplicableTime1(), applicableTimeDTO.getApplicableTime2());
//						AdSlotDTO applicableTimeDTO2 = KsBeanUtil.copyPropertiesThird(applicableTimeDTO, AdSlotDTO.class);
//						applicableTimeDTO.setApplicableTime2(DateUtils.addSeconds(intersectionStart, -1));
//						applicableTimeDTO2.setApplicableTime1(DateUtils.addSeconds(intersectionEnd, 1));
//						baseIterator.add(applicableTimeDTO2);
//						needContinue = true;
//						log.info("[{}~{}]", applicableTimeDTO.getApplicableTime1(), applicableTimeDTO.getApplicableTime2());
//						log.info("[{}~{}]", applicableTimeDTO2.getApplicableTime1(), applicableTimeDTO2.getApplicableTime2());
//						break outerLoop;
//					}
//					// 4. |___________|
//					// |__________________|
//					else if (intersectionStart.compareTo(applicableTimeDTO.getApplicableTime1()) == 0
//							&& intersectionEnd.compareTo(applicableTimeDTO.getApplicableTime2()) == 0) {
//						baseIterator.remove();
//						needContinue = true;
//						log.info("baseIterator删除元素[{}~{}]，重新开始计算", applicableTimeDTO.getApplicableTime1(), applicableTimeDTO.getApplicableTime2());
//						break outerLoop;
//					}
//					// 匹配结束删除此时间段
//					iterator.remove();
//
//				}
//
//			}
//
//		}
//		if (needContinue) {
//			calcAvailableTime(collect, unavailableList);
//		}
//	}

	public MicroServicePage<AdActivityDTO> queryListPage(ActQueryListPageRequest request) {
		MicroServicePage<AdActivityDTO> queryListPage = adActivityRepository.queryListPage(request);
		return queryListPage;
	}

	@Transactional
	public String save(ActSaveRequest req) {
		String pid = generatorService.generate(AdConstants.AD_ACTIVITY_ID_PREFIX);
		int size = req.getActs().size();
		for (AdActivityDTO act : req.getActs()) {
			// 锁定广告位
			ActQueryConflictListRequest conflictListReq = KsBeanUtil.copyPropertiesThird(act, ActQueryConflictListRequest.class);
			String lockKey = RedisKeyConstants.ADVERTISING_SAVE_LOCK + conflictListReq.getLockKey();
			RLock fairLock = redissonClient.getFairLock(lockKey);
			fairLock.lock();
			try {
				// 校验日期,金额
				ckeckDateAndPrice(act);

				AdActivity adActivity = KsBeanUtil.copyPropertiesThird(act, AdActivity.class);
				Date date = new Date();
				String id;
				if (size > 1) {
					id = generatorService.generate(AdConstants.AD_ACTIVITY_ID_PREFIX);
				}else {
					id = pid;
				}
				adActivity.setId(id);
				adActivity.setPid(pid);
				adActivity.setCreateTime(date);
				adActivity.setActivityState(ActivityState.CREATED);
				adActivity.setUpdateTime(date);
				setJumpInfo(adActivity);
				setDuration(adActivity);
				adActivityRepository.save(adActivity);

				for (AdSlotPriceDTO slotPriceDTO : act.getPriceList()) {
					AdSlotPrice slotPrice = adSlotPriceRepository.queryBySlotIdAndEffectiveDate(slotPriceDTO.getSlotId(), slotPriceDTO.getEffectiveDate());
					if (slotPrice == null) {
						throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, MessageFormat.format("投放失败，时间[{0}]未配置", slotPriceDTO.getEffectiveDate()) );
					}
					if (slotPrice.getState() != SlotSeqState.AVAILABLE) {
						throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, MessageFormat.format("投放失败，时间[{0}]不可用", slotPriceDTO.getEffectiveDate()) );
					}
					slotPrice.setActId(id);
					slotPrice.setState(SlotSeqState.OCCUPIED);
					slotPrice.setCreateTime(date);
					adSlotPriceRepository.save(slotPrice);
				}
				
				// 有活动ID表示是驳回重提，此时删除旧数据
				if (StringUtils.isNotEmpty(act.getId())) {
					adActivityRepository.deleteById(act.getId());
				}
				
				sendMqMsgTaskExecutor.execute(() ->{
					// 发送延时消息
					Message<String> build = MessageBuilder.withPayload(id).setHeader("x-delay", configUtil.getPayCancelDelay()).build();
					resolver.resolveDestination(AdConstants.AD_PAY_CANCEL_PRODUCER).send(build);
				});

			} finally {
				fairLock.unlock();
			}
		}
		return pid;
		

	}

	private void setJumpInfo(AdActivity adActivity) {
		if (ClickJumpType.INTO_STORE_HOMEPAGE == adActivity.getClickJumpType()) {
			adActivity.setClickJumpInfo(adActivity.getStoreId().toString());
		}
	}

	private void setDuration(AdActivity adActivity) {
		if (SlotType.BOOT_PAGE == adActivity.getSlotType()) {
			adActivity.setDuration(configUtil.getBootPageDuration());
		}
	}

	private void ckeckDateAndPrice(AdActivityDTO act) {
		// 校验日期
		Date temp1 = DateUtils.addDays(act.getStartTime(), act.getDays());
		Date temp2 = DateUtils.addSeconds(act.getEndTime(), 1);
		if (temp1.compareTo(temp2) != 0) {
			throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "投放失败，活动天数与时间不匹配");
		}
		// 校验价格
		BigDecimal totalPrice = BigDecimal.ZERO;
		for (AdSlotPriceDTO slotPriceDTO : act.getPriceList()) {
			totalPrice = totalPrice.add(slotPriceDTO.getUnitPrice());
		}
		if (totalPrice.compareTo(act.getRealPrice()) != 0) {
			throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "投放失败，活动价格有误");
		}
	}
	



	/**
	 * 线上支付回调
	 * 
	 * @param request
	 */
	@LcnTransaction
	@Transactional
	public void payCallBack(ActPayCallBackRequest request) {
		log.info("广告活动[{}]收到线上支付回调", request);
		Optional<AdActivity> act = adActivityRepository.findById(request.getAdActId());
		if (!act.isPresent()) {
			log.error("广告活动[" + request.getAdActId() + "]不存在");
			return;
		}
		AdActivity adActivity = act.get();
		if (ActivityState.CREATED != adActivity.getActivityState()) {
			log.error("广告活动状态[" + request.getAdActId() + "]不是已创建");
			return;
		}
		// TODO 检查bizId
		
		savePayRecord(adActivity, request.getBizId());
		// 小程序支付不需要审核，默认审核通过
		if (adActivity.getPayType() == PayType.ONLINE_XCX) {
			adActivity.setActivityState(ActivityState.AUDIT_PASS);
		}else {
			adActivity.setActivityState(ActivityState.PAID);
		}
		adActivity.setSubmitTime(new Date());
		adActivityRepository.save(adActivity);
		
		if (adActivity.getPayType() == PayType.ONLINE) {
			log.info("广告活动[{}]付款记录处理完毕，推送IM消息给前端", request.getAdActId());
			// pay系统只有付款成功才会回调过来，所以直接推success
			pushCallBackMsg(adActivity.getBuyUserId(), AdConstants.SUCCESS);
		}

	}

	private void pushCallBackMsg(String account, String res) {
		// 发送IM消息给前端刷新页面
		SystemConfigResponse response = systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
				.configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
		JSONObject jsonObject = JSONObject.parseObject(response.getSystemConfigVOList().get(0).getContext());
		String appKey = jsonObject.getString("appKey");
		long appId = jsonObject.getLong("appId");
		TencentImCustomerUtil.sendCustomMsg(account, res, TencentImMessageType.PushAdPayRes, appId, appKey);
	}

	/**
	 * 小程序支付由mobile拉起，不走这里
	 * @param request
	 * @return
	 */
//	@LcnTransaction
//	@Transactional
//	public String pay(ActPayRequest request) {
//		String actId = request.getId();
//		String lockKey = RedisKeyConstants.ADVERTISING_PAY_LOCK + actId;
//		RLock fairLock = redissonClient.getFairLock(lockKey);
//		fairLock.lock();
//		try {
//			Optional<AdActivity> act = adActivityRepository.findById(actId);
//			if (!act.isPresent()) {
//				throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "付款失败，广告活动[" + actId + "]不存在");
//			}
//			AdActivity context = act.get();
//			if (ActivityState.CREATED != context.getActivityState()) {
//				throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "付款失败，广告活动[" + actId + "]不是已创建");
//			}
//			PayType payType = context.getPayType();
//			if (request.getPayType() != null) {
//				payType = request.getPayType();
//			}
//			log.info("广告活动[{}]开始执行付款,付款类型[{}]", context.getId(), context.getPayType());
//			if (payType == PayType.COIN) {
//				return coinPay(context);
//			} 
//			return genQRCode(context);
//		} finally {
//			fairLock.unlock();
//		}
//	}

	/**
	 * 鲸币支付
	 * 
	 * @param actId
	 * @return
	 */
//	public String coinPay(AdActivity context) {
//		// 校验鲸币余额并扣款
//		// 鲸币商家账号
//		WalletInfoRequest req = WalletInfoRequest.builder().storeFlag(true).storeId(context.getStoreId().toString()).build();
//		BaseResponse<CusWalletVO> fromWalletResp = customerWalletProvider.queryCustomerWallet(req);
//		if (fromWalletResp.getContext().getBalance().compareTo(context.getRealPrice()) < 0) {
//			throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "鲸币付款失败，商家鲸币余额不足");
//		}
//		// 鲸币平台账号
//		req.setStoreId(configUtil.getPlatformStoreId());
//		BaseResponse<CusWalletVO> toWalletResp = customerWalletProvider.queryCustomerWallet(req);
//		// 调用鲸币系统付款
//		CustomerWalletGiveRequest build = CustomerWalletGiveRequest.builder().opertionType(0).balance(context.getRealPrice())
//				.changOverToStoreAccount(toWalletResp.getContext().getCustomerAccount()).storeId(context.getStoreId().toString())
//				.relationOrderId(context.getId()).remark(AdConstants.COIN_PAY_REMARK).tradeRemark(AdConstants.COIN_PAY_REMARK + "-" + context.getId())
//				.build();
//		BaseResponse<WalletRecordVO> merchantGiveUser = walletMerchantProvider.merchantGiveUser(build);
//		String recordNo = merchantGiveUser.getContext().getRecordNo();
//		log.info("广告活动[{}]鲸币付款完毕[{}]，开始写入付款记录", context.getId(), recordNo);
//		savePayRecord(context, recordNo);
//
//		context.setActivityState(ActivityState.PAID);
//		context.setSubmitTime(new Date());
//		adActivityRepository.save(context);
//		return recordNo;
//
//	}

	/**
	 * 在线支付生成二维码
	 * @param context
	 * @return
	 */
//	public String genQRCode(AdActivity context) {
//		String actId = context.getId();
//		BigDecimal realPrice = context.getRealPrice();
//		CcbPayOrderRequest ccbRequest = new CcbPayOrderRequest();
//		// 主订单参数
//		String payOrderNo = generatorService.generate(AdConstants.AD_ACTIVITY_ID_PREFIX);
//		ccbRequest.setMainOrderNo(payOrderNo);
//		ccbRequest.setOrderAmount(realPrice);
//		ccbRequest.setTxnAmt(realPrice);
//
//		// 子订单参数
//		List<CcbSubPayOrderRequest> subOrderList = new ArrayList<>();
//		CcbSubPayOrderRequest subOrder = new CcbSubPayOrderRequest();
//		subOrder.setCmdtyOrderNo(payOrderNo + "01");
//		subOrder.setOrderAmount(realPrice);
//		subOrder.setTxnAmt(realPrice);
//		subOrder.setTid(actId);
////        subOrder.setCommissionFlag(BoolFlag.NO);
//		subOrderList.add(subOrder);
//
//		//其他参数
//		ccbRequest.setSubOrderList(subOrderList);
//		ccbRequest.setClientIp(HttpUtil.getIpAddr());
//		ccbRequest.setChannelId(32L);
//		ccbRequest.setPayOrderNo(payOrderNo);
//		ccbRequest.setBusinessId(actId);
//		// 广告付款类型
//		ccbRequest.setPayType(7);
//		String pay_qr_code = ccbPayProvider.ccbPayOrder(ccbRequest).getContext().getPay_Qr_Code();
//		log.info("广告活动[{}]付款二维码生成完毕", context.getId());
//		return pay_qr_code;
//	}

	private void savePayRecord(AdActivity context, String payOrderId) {
		AdPayRecord payRecord = new AdPayRecord();
		payRecord.setActivityId(context.getId());
		payRecord.setAmount(context.getRealPrice());
		payRecord.setCreateTime(new Date());
		payRecord.setPayOrderId(payOrderId);
		payRecord.setPayType(context.getPayType());
		payRecord.setState(1);
		adPayRecordRepository.save(payRecord);
	}

	public AdActivityDTO queryOne(String id) {
		Optional<AdActivity> act = adActivityRepository.findById(id);
		if (act.isPresent()) {
			AdActivityDTO copyPropertiesThird = KsBeanUtil.copyPropertiesThird(act.get(), AdActivityDTO.class);
			return copyPropertiesThird;
		}
		return null;
	}

//	public void audit(ActAuditRequest request) {
//		Optional<AdActivity> act = adActivityRepository.findById(request.getActId());
//		if (!act.isPresent()) {
//			throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "审核失败，广告活动[" + request.getActId() + "]不存在");
//		}
//		AdActivity context = act.get();
//		if (ActivityState.PAID != context.getActivityState()) {
//			throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "审核失败，广告活动[" + request.getActId() + "]不是待审核");
//		}
//		if (request.getPass()) {
//			context.setActivityState(ActivityState.AUDIT_PASS);
//		} else {
//			context.setActivityState(ActivityState.AUDIT_REJECTED);
//			// 退款操作
//			doRefund(context);
//		}
//		context.setAuditComment(request.getAuditComment());
//		context.setAuditTime(new Date());
//		adActivityRepository.save(context);
//	}

//	@LcnTransaction
//	@Transactional
//	public void refund(String id) {
//		String lockKey = RedisKeyConstants.ADVERTISING_REFUND_LOCK + id;
//		RLock fairLock = redissonClient.getFairLock(lockKey);
//		fairLock.lock();
//		try {
//			Optional<AdActivity> act = adActivityRepository.findById(id);
//			if (!act.isPresent()) {
//				throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "退款失败，广告活动[" + id + "]不存在");
//			}
//			AdActivity context = act.get();
//			if (ActivityState.PAID != context.getActivityState() && ActivityState.AUDIT_PASS != context.getActivityState()) {
//				throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "退款失败，广告活动[" + id + "]不是待审核|待履行");
//			}
//
//			doRefund(context);
//			context.setActivityState(ActivityState.CANCELLED);
//			adActivityRepository.save(context);
//
//		} finally {
//			fairLock.unlock();
//		}
//	}

//	public void doRefund(AdActivity context) {
//		log.info("广告活动[{}]开始执行退款,退款类型[{}]", context.getId(), context.getPayType());
//		String refundOrderId = "";
//		int refundStatus = 1;
//		if (context.getPayType() == PayType.COIN) {
//			// 校验鲸币余额并退款
//			// 鲸币平台账号
//			WalletInfoRequest req = WalletInfoRequest.builder().storeFlag(true).storeId(configUtil.getPlatformStoreId()).build();
//			BaseResponse<CusWalletVO> fromWalletResp = customerWalletProvider.queryCustomerWallet(req);
//			if (fromWalletResp.getContext().getBalance().compareTo(context.getRealPrice()) < 0) {
//				throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "鲸币退款失败，平台鲸币余额不足");
//			}
//			// 鲸币商家账号
//			req.setStoreId(context.getStoreId().toString());
//			BaseResponse<CusWalletVO> toWalletResp = customerWalletProvider.queryCustomerWallet(req);
//			// 调用鲸币系统付款
//			CustomerWalletGiveRequest build = CustomerWalletGiveRequest.builder().opertionType(0).balance(context.getRealPrice())
//					.changOverToStoreAccount(toWalletResp.getContext().getCustomerAccount()).storeId(configUtil.getPlatformStoreId())
//					.relationOrderId(context.getId()).remark(AdConstants.COIN_REFUND_REMARK)
//					.tradeRemark(AdConstants.COIN_REFUND_REMARK + "-" + context.getId()).build();
//			BaseResponse<WalletRecordVO> merchantGiveUser = walletMerchantProvider.merchantGiveUser(build);
//			refundOrderId = merchantGiveUser.getContext().getRecordNo();
//		} else {
//			// 包括线上二维码支付与线上小程序支付
//			// 在线付款退款操作
//			String refundNo = generatorService.generate(AdConstants.AD_ACTIVITY_ID_PREFIX);
//			AdPayRecord lastRecord = adPayRecordRepository.queryByActivityId(context.getId());
//			if (lastRecord == null) {
//				throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "在线付款退款失败，未找到在线付款记录");
//			}
//			String pyTrnNo = lastRecord.getPayOrderId();
//			CcbRefundAdRequest build = CcbRefundAdRequest.builder().pyTrnNo(pyTrnNo).refundAmount(context.getRealPrice()).refundNo(refundNo).build();
//			BaseResponse<CcbRefundAdResponse> adRefund = ccbPayProvider.adRefund(build);
//			// pay系统返回状态（1：成功，2：退款中，3：失败）
//			if (adRefund.getContext().getRefundStatus() == 3) {
//				refundStatus = 0;
//			}
//			refundOrderId = refundNo;
//		}
//		log.info("广告活动[{}]退款完毕[{}]，开始写入退款记录", context.getId(), refundOrderId);
//		saveRefundRecord(context, refundOrderId, refundStatus);
//	}

//	private void saveRefundRecord(AdActivity context, String refundOrderId, Integer refundStatus) {
//		AdRefundRecord payRecord = new AdRefundRecord();
//		payRecord.setActivityId(context.getId());
//		payRecord.setAmount(context.getRealPrice());
//		payRecord.setCreateTime(new Date());
//		payRecord.setRefundOrderId(refundOrderId);
//		payRecord.setRefundType(context.getPayType().toValue());
//		payRecord.setState(refundStatus);
//		adRefundRecordRepository.save(payRecord);
//	}

//	public void takeOff(String id) {
//		Optional<AdActivity> act = adActivityRepository.findById(id);
//		if (!act.isPresent()) {
//			throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "下架失败，广告活动[" + id + "]不存在");
//		}
//		AdActivity context = act.get();
//		if (ActivityState.SUCCESS != context.getActivityState()) {
//			throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "下架失败，广告活动[" + id + "]不是投放成功");
//		}
//		
//		doRefund(context);
//		
//		context.setActivityState(ActivityState.CANCELLED);
//		adActivityRepository.save(context);
//		
//		// 删除缓存
//		String key = MessageFormat.format(RedisKeyConstants.ACTIVE_AD_KEY_TPL, context.getSlotType().toValue(), context.getMarketId(),
//				context.getMallTabId(), context.getGoodsCateId(), context.getSlotGroupSeq());
//		redisCache.delete(key);
//	}

//	public void cancel(String actId) {
//		Optional<AdActivity> act = adActivityRepository.findById(actId);
//		if (!act.isPresent()) {
//			log.error("取消广告[{}]失败,广告活动不存在", actId);
//			return;
//		}
//		AdActivity adActivity = act.get();
//		if (ActivityState.CREATED != adActivity.getActivityState()) {
//			log.error("取消广告[{}]失败,广告活动不是待支付", actId);
//			return;
//		}
//		adActivity.setActivityState(ActivityState.CANCELLED);
//		adActivityRepository.save(adActivity);
//	}

	public void batchComplete() {
		List<AdActivity> queryNeedCompleteList = adActivityRepository.queryNeedCompleteList();
		List<String> delKeys = new ArrayList<>();
		for (AdActivity adActivity : queryNeedCompleteList) {
			adActivity.setActivityState(ActivityState.COMPLETED);
			String key = MessageFormat.format(RedisKeyConstants.ACTIVE_AD_KEY_TPL, adActivity.getSlotType().toValue(), adActivity.getMarketId(),
					adActivity.getMallTabId(), adActivity.getGoodsCateId(), adActivity.getSlotGroupSeq());
			delKeys.add(key);
		}
		adActivityRepository.saveAll(queryNeedCompleteList);
		
		// 更新缓存
		log.info("batchComplete更新的keys {}", delKeys);
		redisCache.delete(delKeys);
	}
	
	public void batchStart() {
		List<AdActivity> queryNeedCompleteList = adActivityRepository.queryNeedStartList();
		for (AdActivity adActivity : queryNeedCompleteList) {
			adActivity.setActivityState(ActivityState.SUCCESS);
		}
		adActivityRepository.saveAll(queryNeedCompleteList);
		
		List<String> collect = queryNeedCompleteList.stream().map(AdActivity::getId).collect(Collectors.toList());
		log.info("batchStart投放的广告活动id {}", collect);
	}


	/**
	 * 如果退款时间在建行分账时间内，建行不会立马返回退款结果，会在分账结束后通过回调接口通知
	 * @param request
	 */
//	public void adRefundCallback(AdRefundCallbackRequest request) {
//		log.info("广告支付退款中，分账后回调信息：{}", JSON.toJSONString(request));
//		// 退款编码 发起退款时传入的refundNo
//		String refundNo = request.getRefundNo();
//		// 状态（1：成功，2：退款中，3：失败）
//		Integer refundStatus = request.getRefundStatus();
//		// 根据状态业务处理
//		AdRefundRecord adRefundRecord = adRefundRecordRepository.queryByRefundOrderId(refundNo);
//		if (adRefundRecord == null) {
//			log.warn("无法完成广告支付退款回调，refundNo[{}]相关的记录不存在", refundNo);
//			return;
//		}
//		if (refundStatus == 3) {
//			refundStatus = 0;
//		}
//		adRefundRecord.setState(refundStatus);
//		adRefundRecordRepository.save(adRefundRecord);
//	}
	
	public List<AdActivityDTO> queryActiveAct(ActQueryActiveActRequest req) {
		String key = MessageFormat.format(RedisKeyConstants.ACTIVE_AD_KEY_TPL, req.getSlotType().toValue(), req.getMarketId(),
				req.getMallTabId(), req.getGoodsCateId(), req.getSlotGroupSeq());
		log.info("queryActiveAct的key[{}]", key);
		String adStr = redisCache.get(key);
		if (StringUtils.isNotEmpty(adStr)) {
			List<AdActivityDTO> parseArray = JSON.parseArray(adStr, AdActivityDTO.class);
			return parseArray;
		}
		
		List<AdActivityDTO> ad;
		// 批发市场下推荐tab页按价格排序返回
		if (req.getSlotType() == SlotType.MALL_GOOODS_LIST && req.getMallTabId() == 0) {
			ad = adActivityRepository.queryMallGooodsAdTop(req.getMarketId(), configUtil.getMallGooodsLimit());
		}else {
			ad = adActivityRepository.queryActiveAct(req);
		}
		// 更新缓存
		redisCache.set(key, JSON.toJSONString(ad), 60 * 10);
		return ad;
	}
	
	public void update(ActUpdateRequest req) {
		// 只能修改广告活动展示信息
		Optional<AdActivity> findById = adActivityRepository.findById(req.getId());
		if (!findById.isPresent()) {
			log.error("更新失败，广告活动[" + req.getId() + "]不存在");
			return;
		}
		AdActivity act = findById.get();
		if (act.getActivityState().toValue() > ActivityState.SUCCESS.toValue()) {
			log.error("更新失败，广告活动[" + req.getId() + "]状态不正确");
			return;
		}
	
		// 商品广告位只能修改商品信息
		if (act.getSlotType() == SlotType.MALL_GOOODS_LIST) {
			act.setSpuId(req.getSpuId());
			act.setSpuName(req.getSpuName());
		}else {
			act.setMaterialUrl(req.getMaterialUrl());
			act.setMaterialKey(req.getMaterialKey());
			act.setMaterialType(req.getMaterialType());
		}
		adActivityRepository.save(act);
	}
	
	public void sendUpdateAdInfoMq(ActUpdateRequest req) {
		// 老板要求第二天0点才能修改成功,算出延时时间
		Date now = new Date();
		Date truncate = DateUtils.truncate(now, Calendar.DAY_OF_MONTH);
		Date addDays = DateUtils.addDays(truncate, 1);
		long delay = addDays.getTime() - now.getTime();
		log.info("修改广告活动[{}]延时[{}]毫秒", req.getId(), delay);
		sendMqMsgTaskExecutor.execute(() ->{
			// 发送延时消息
			Message<String> build = MessageBuilder.withPayload(JSON.toJSONString(req)).setHeader("x-delay", delay).build();
			resolver.resolveDestination(AdConstants.AD_UPDATE_PRODUCER).send(build);
		});
	}
	
	public void batchSetSlotPrice(SlotBatchSetPriceRequest request) {
		for (AdSlotPriceDTO adSlotPriceDTO : request.getPrices()) {
			AdSlotPrice adSlotPrice = adSlotPriceRepository.queryBySlotIdAndEffectiveDate(adSlotPriceDTO.getSlotId(), adSlotPriceDTO.getEffectiveDate());
			adSlotPrice.setUnitPrice(adSlotPriceDTO.getUnitPrice());
			adSlotPriceRepository.save(adSlotPrice);
		}
		
	}
	
	
	
	/******************* 初始化广告位开始 ******************************/
	
	@Autowired
	private ThreadPoolTaskExecutor updateRedisTaskExecutor;
	
	public void init(SlotSaveRequest request) {
		if (request.getSlotType() == SlotType.MALL_GOOODS_LIST) {
	        final CompanyMallSupplierTabListQueryRequest mallQuery = new CompanyMallSupplierTabListQueryRequest();
	        mallQuery.setOpenStatus(MallOpenStatus.OPEN.toValue());
	        mallQuery.setDeleteFlag(DeleteFlag.NO);
	        final BaseResponse<List<CompanyMallSupplierTabVO>> mallListRes = companyIntoPlatformQueryProvider.listSupplierTab(mallQuery);
	        final List<CompanyMallSupplierTabVO> mallList = mallListRes.getContext();
	        
	        for (CompanyMallSupplierTabVO companyMallSupplierTabVO : mallList) {
				if (companyMallSupplierTabVO.getRelCatId() != null) {
			        final GoodsCateListByConditionRequest goodsCatQuery = new GoodsCateListByConditionRequest();
			        goodsCatQuery.setDelFlag(DeleteFlag.NO.toValue());
			        goodsCatQuery.setCateParentId(companyMallSupplierTabVO.getRelCatId());
			        final BaseResponse<GoodsCateListByConditionResponse> goodsCateRes = goodsCateQueryProvider.listByCondition(goodsCatQuery);
			        for (GoodsCateVO goodsCateVO : goodsCateRes.getContext().getGoodsCateVOList()) {
			        	log.info("初始化商城[{}]下类目[{}]广告位", companyMallSupplierTabVO.getTabName(), goodsCateVO.getCateName());
			        	
			        	SlotSaveRequest copyPropertiesThird = KsBeanUtil.copyPropertiesThird(request, SlotSaveRequest.class);
			        	copyPropertiesThird.setMallTabId((companyMallSupplierTabVO.getId().intValue()));
			        	copyPropertiesThird.setMallTabName(companyMallSupplierTabVO.getTabName());
			        	copyPropertiesThird.setGoodsCateId(goodsCateVO.getCateId().intValue());
			        	copyPropertiesThird.setGoodsCateName(goodsCateVO.getCateName());
			        	doInit(copyPropertiesThird);
					}
			        // 补上无类目tab页广告
			        SlotSaveRequest copyPropertiesThird = KsBeanUtil.copyPropertiesThird(request, SlotSaveRequest.class);
		        	copyPropertiesThird.setMallTabId((companyMallSupplierTabVO.getId().intValue()));
		        	copyPropertiesThird.setMallTabName(companyMallSupplierTabVO.getTabName());
			        copyPropertiesThird.setGoodsCateId(0);
			        copyPropertiesThird.setGoodsCateName("无类目");
		        	doInit(copyPropertiesThird);
				}
			}
	        return;
		}
	 	doInit(request);
	}
	
	private void doInit(SlotSaveRequest request) {
		Runnable task = () ->{
			int limit = 1;
			if (request.getSlotType() == SlotType.MALL_GOOODS_LIST) {
				limit = configUtil.getMallGooodsLimit();
			}
			if (request.getSlotType() == SlotType.HOME_PAGE_BANNER) {
				limit = configUtil.getBannerLimit();
			}
			
			List<AdSlotPrice> list = new ArrayList<>();
			for (int i = 1; i <= limit; i++) {
				request.setSlotGroupSeq(i);
				AdSlot genSlot = genSlot(request, request.getSlotGroupSeq());
				AdSlot save = adSlotRepository.save(genSlot);
				List<AdSlotPrice> genPrice = genPrice(save.getId());
				list.addAll(genPrice);
			}
			adSlotPriceRepository.batchInsert(list);
		};
		updateRedisTaskExecutor.execute(task);
	}



	private List<AdSlotPrice> genPrice(Integer slotId) {
		// 初始化60天的价格
		int initDateLimit = configUtil.getInitDateLimit();
		List<AdSlotPrice> list = new ArrayList<>();
		for (int j = 1; j <= initDateLimit; j++) {
			AdSlotPrice slotPrice = new AdSlotPrice();
			slotPrice.setSlotId(slotId);
			slotPrice.setUnit(UnitType.DAY);
			slotPrice.setUnitPrice(configUtil.getDefaultPrice());
			Date now = new Date();
			Date truncate = DateUtils.truncate(now, Calendar.DAY_OF_MONTH);
			Date addDays = DateUtils.addDays(truncate, j);
			slotPrice.setEffectiveDate(addDays);
			slotPrice.setState(SlotSeqState.AVAILABLE);
			slotPrice.setCreateTime(now);
			list.add(slotPrice);
		}
		return list;
	}

	private AdSlot genSlot(SlotSaveRequest request, int i) {
		AdSlot adSlot = new AdSlot();
		adSlot.setSlotType(request.getSlotType());
		adSlot.setSlotGroupSeq(i);
		adSlot.setSlotName("广告位-" + StringUtils.leftPad(i + "", 2 ,'0'));
		Date date = new Date();
		adSlot.setCreateTime(date);
		adSlot.setUpdateTime(date);
		String usr = "sys_init";
		adSlot.setCreateUser(usr);
		adSlot.setUpdateUser(usr);
		adSlot.setSlotState(SlotState.ADDED);
		adSlot.setMarketId(request.getMarketId());
		adSlot.setMarketName(request.getMarketName());
		if (request.getSlotType() == SlotType.MALL_GOOODS_LIST) {
			adSlot.setMallTabId(request.getMallTabId());
			adSlot.setMallTabName(request.getMallTabName());
			adSlot.setGoodsCateId(request.getGoodsCateId());
			adSlot.setGoodsCateName(request.getGoodsCateName());
		}
		return adSlot;
	}
	
	/******************* 初始化广告位结束 ******************************/
	
	
	
	
	
	public static void main(String[] args) {
		String leftPad = StringUtils.leftPad(30 + "", 2 ,'0');
		System.out.println(leftPad);
	}


	
	
	
	

	

}

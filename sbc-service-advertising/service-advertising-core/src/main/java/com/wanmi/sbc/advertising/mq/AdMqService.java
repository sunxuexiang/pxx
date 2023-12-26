package com.wanmi.sbc.advertising.mq;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.advertising.api.request.activity.ActUpdateRequest;
import com.wanmi.sbc.advertising.api.request.statistic.StatisticAddRequest;
import com.wanmi.sbc.advertising.bean.constant.AdConstants;
import com.wanmi.sbc.advertising.bean.enums.StatisticInfoType;
import com.wanmi.sbc.advertising.model.AdClick;
import com.wanmi.sbc.advertising.model.AdConsumerMsg;
import com.wanmi.sbc.advertising.model.AdImpression;
import com.wanmi.sbc.advertising.repository.AdClickRepository;
import com.wanmi.sbc.advertising.repository.AdConsumerMsgRepository;
import com.wanmi.sbc.advertising.repository.AdImpressionRepository;
import com.wanmi.sbc.advertising.service.AdActivityService;
import com.wanmi.sbc.common.util.KsBeanUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableBinding(AdSink.class)
public class AdMqService {

	@Autowired
	private AdConsumerMsgRepository adConsumerMsgRepository;

	@Autowired
	private AdImpressionRepository adImpressionRepository;

	@Autowired
	private AdClickRepository adClickRepository;

	@Autowired
	private AdActivityService adActivityService;
	
	/**
	 * mq接收修改广告活动展示信息延时消息
	 *
	 * @param msg
	 */
	@StreamListener(AdConstants.AD_UPDATE_CONSUMER)
	public void receiveAdUpdate(Message<String> receivedMessage) {
		String msgId = receivedMessage.getHeaders().get(AdConstants.AD_MSG_ID_KEY, String.class);
		String payload = receivedMessage.getPayload();
		log.info("mq接收修改广告活动展示信息延时消息,msgId[{}],payload[{}]", msgId, payload);
		// 防止重复消费
		boolean sucess = saveMessage(payload, msgId);
		if (sucess) {
			ActUpdateRequest parseObject = JSON.parseObject(payload, ActUpdateRequest.class);
			adActivityService.update(parseObject);
		}

	}
	

	/**
	 * mq接收取消购买广告延时消息
	 *
	 * @param msg
	 */
	@StreamListener(AdConstants.AD_PAY_CANCEL_CONSUMER)
	public void receivePayCancel(Message<String> receivedMessage) {
		String msgId = receivedMessage.getHeaders().get(AdConstants.AD_MSG_ID_KEY, String.class);
		String payload = receivedMessage.getPayload();
		log.info("mq接收取消购买广告延时消息,msgId[{}],payload[{}]", msgId, payload);
		// 防止重复消费
		boolean sucess = saveMessage(payload, msgId);
		if (sucess) {
//			adActivityService.cancel(payload);
		}

	}

	/**
	 * mq接收广告统计信息
	 *
	 * @param msg
	 */
	@StreamListener(AdConstants.AD_STATISTIC_ADD)
	public void receiveStatisticAdd(Message<String> receivedMessage) {
		String msgId = receivedMessage.getHeaders().get(AdConstants.AD_MSG_ID_KEY, String.class);
		String payload = receivedMessage.getPayload();
		log.info("mq接收广告统计信息,msgId[{}],payload[{}]", msgId, payload);
		// 防止重复消费
		boolean sucess = saveMessage(payload, msgId);
		if (sucess) {
			StatisticAddRequest parseObject = JSON.parseObject(payload, StatisticAddRequest.class);
			Integer statisticInfoType = parseObject.getStatisticInfoType();
			if (StatisticInfoType.IMPRESSION.ordinal() == statisticInfoType) {
				addImpressionInfo(parseObject);
			} else if (StatisticInfoType.CLICK.ordinal() == statisticInfoType) {
				addClickInfo(parseObject);
			}
		}

	}

	private void addImpressionInfo(StatisticAddRequest request) {
		AdImpression impression = KsBeanUtil.copyPropertiesThird(request, AdImpression.class);
		impression.setCreateTime(new Date());
		impression.setImpressionTime(request.getGenerateTime());
		adImpressionRepository.save(impression);
	}

	private void addClickInfo(StatisticAddRequest request) {
		AdClick adClick = KsBeanUtil.copyPropertiesThird(request, AdClick.class);
		adClick.setCreateTime(new Date());
		adClick.setClickTime(request.getGenerateTime());
		adClickRepository.save(adClick);
	}

	private boolean saveMessage(String payload, String msgId) {
		try {
			AdConsumerMsg msg = new AdConsumerMsg();
			msg.setMsgId(msgId);
			msg.setPayload(payload);
			msg.setCreateTime(new Date());
			adConsumerMsgRepository.save(msg);
			return true;
		} catch (DataIntegrityViolationException e) {
			log.warn("此广告消息[{}]已被消费", msgId);
			return false;
		}
	}
}

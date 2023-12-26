package com.wanmi.sbc.order.pickuprecord.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.pickuprecord.PickUpRecordQueryRequest;
import com.wanmi.sbc.order.api.request.trade.TradeVerifyAfterProcessingRequest;
import com.wanmi.sbc.order.bean.enums.AuditState;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.bean.enums.PaymentOrder;
import com.wanmi.sbc.order.bean.vo.PickUpRecordVO;
import com.wanmi.sbc.order.pickuprecord.model.root.PickUpRecord;
import com.wanmi.sbc.order.pickuprecord.repository.PickUpRecordRepository;
import com.wanmi.sbc.order.trade.fsm.TradeFSMService;
import com.wanmi.sbc.order.trade.fsm.event.TradeEvent;
import com.wanmi.sbc.order.trade.fsm.params.StateRequest;
import com.wanmi.sbc.order.trade.model.root.Trade;
import com.wanmi.sbc.order.trade.service.TradeService;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>测试代码生成业务逻辑</p>
 * @author lh
 * @date 2020-07-14 13:48:26
 */
@Service("PickUpRecordService")
public class PickUpRecordService {
	@Autowired
	private PickUpRecordRepository pickUpRecordRepository;

	@Autowired
	private TradeQueryProvider tradeQueryProvider;

	@Autowired
	private AuditQueryProvider auditQueryProvider;


	@Autowired
	private TradeFSMService tradeFSMService;

	@Autowired
	private TradeService tradeService;




	/**
	 * 新增测试代码生成
	 *
	 * @author lh
	 */
	@Transactional
	public PickUpRecord add(PickUpRecord entity) {
		pickUpRecordRepository.save(entity);
		return entity;
	}

	@Transactional
	public void updateChangePickUpFlag(String tradeId, Operator operator, String phone) {
		Trade trade = tradeService.detail(tradeId);
		//是否开启订单审核
		if (auditQueryProvider.isSupplierOrderAudit().getContext().isAudit() && trade.getTradeState().getAuditState()
				!= AuditState.CHECKED) {
			//只有已审核订单才能发货
			throw new SbcRuntimeException("K-050317");
		}
		// 先款后货并且未支付的情况下禁止自提
		if (trade.getPaymentOrder() == PaymentOrder.PAY_FIRST && trade.getTradeState().getPayState() == PayState.NOT_PAID) {
			throw new SbcRuntimeException("K-050318");
		}
		if (tradeQueryProvider.verifyAfterProcessing(TradeVerifyAfterProcessingRequest.builder().tid(trade.getId()).build()).getContext().getVerifyResult()) {
			throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "订单已申请售后，暂时不能发货");
		}
		if (!DeliverStatus.SHIPPED.equals(trade.getTradeState().getDeliverStatus())){
			throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "自提订单尚未发货，请先发货");
		}
		TradeEvent event;
		if (trade.getTradeState().getPayState() == PayState.PAID) {
			event = TradeEvent.COMPLETE;
		} else {
			event = TradeEvent.PICK_UP;
		}
		//更新自提状态
		pickUpRecordRepository.updatePickUpFlag(tradeId);
		StateRequest stateRequest = StateRequest
				.builder()
				.tid(tradeId)
				.operator(operator)
				.data(trade)
				.event(event)
				.build();
		tradeFSMService.changeState(stateRequest);
		//短信推送
		tradeService.sendPickUpSuccessMessage(trade);
	}

	/**
	 * 修改测试代码生成
	 *
	 * @author lh
	 */
	@Transactional
	public PickUpRecord modify(PickUpRecord entity) {
		pickUpRecordRepository.save(entity);
		return entity;
	}

	/**
	 * 单个删除测试代码生成
	 *
	 * @author lh
	 */
	@Transactional
	public void deleteById(PickUpRecord entity) {
		pickUpRecordRepository.save(entity);
	}

	/**
	 * 批量删除自提码表代码生成
	 *
	 * @author lh
	 */
	@Transactional
	public void deleteByIdList(List<PickUpRecord> infos) {
		pickUpRecordRepository.saveAll(infos);
	}

	/**
	 * 功能描述: 批量生成自提码
	 */
	@LcnTransaction
	public void addBatch(List<PickUpRecord> infos) {
		pickUpRecordRepository.saveAll(infos);
	}


	/**
	 * 单个查询自提码表代码生成
	 *
	 * @author lh
	 */
	public PickUpRecord getOne(String id) {
		return pickUpRecordRepository.findByPickUpIdAndDelFlag(id, DeleteFlag.NO)
				.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "无自提码信息"));
	}


	public PickUpRecord getOneByTradeId(String tradeId) {
		return pickUpRecordRepository.findByTradeIdAndDelFlag(tradeId, DeleteFlag.NO)
				.orElseThrow(() -> new SbcRuntimeException(CommonErrorCode.SPECIFIED, "无自提码信息"));
	}

	/**
	 * 分页查询自提码表代码生成
	 *
	 * @author lh
	 */
	public Page<PickUpRecord> page(PickUpRecordQueryRequest queryReq) {
		return pickUpRecordRepository.findAll(
				PickUpRecordWhereCriteriaBuilder.build(queryReq),
				queryReq.getPageRequest());
	}

	/**
	 * 列表查询自提码表代码生成
	 *
	 * @author lh
	 */
	public List<PickUpRecord> list(PickUpRecordQueryRequest queryReq) {
		return pickUpRecordRepository.findAll(PickUpRecordWhereCriteriaBuilder.build(queryReq));
	}

	/**
	 * 将实体包装成VO
	 *
	 * @author lh
	 */
	public PickUpRecordVO wrapperVo(PickUpRecord pickUpRecord) {
		if (pickUpRecord != null) {
			PickUpRecordVO pickUpRecordVO = KsBeanUtil.convert(pickUpRecord, PickUpRecordVO.class);
			return pickUpRecordVO;
		}
		return null;
	}
}


package com.wanmi.sbc.customer.invitationstatistics.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.request.invitationstatistics.InvitationRegisterStatisticsRequest;
import com.wanmi.sbc.customer.bean.vo.InvitationStatisticsVO;
import com.wanmi.sbc.customer.invitationstatistics.model.root.InvitationStatistics;
import com.wanmi.sbc.customer.invitationstatistics.model.root.InvitationStatisticsOrder;
import com.wanmi.sbc.customer.invitationstatistics.repository.InvitationStatisticsOrderRepository;
import com.wanmi.sbc.customer.invitationstatistics.repository.InvitationStatisticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * <p>邀新统计业务逻辑</p>
 * @author lvheng
 * @date 2021-04-23 10:57:45
 */
@Service("InvitationStatisticsService")
@Slf4j
public class InvitationStatisticsService {
	@Autowired
	private InvitationStatisticsRepository invitationStatisticsRepository;

	@Autowired
	private InvitationStatisticsOrderRepository invitationStatisticsOrderRepository;

	/**
	 * 注册统计接口
	 * 仅用于注册时统计邀新
	 *
	 * @param employeeId 员工ID
	 */
	@Transactional(rollbackFor = SbcRuntimeException.class)
	public void registerStatistics(String employeeId) {
		String nowFormat = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		//根据employeeId查询
		Optional<InvitationStatistics> byId = invitationStatisticsRepository.getById(employeeId, nowFormat);
		if (byId.isPresent()) {
			//更新注册数
			invitationStatisticsRepository.updateCount(employeeId, nowFormat);
		} else {
			//新增注册
			InvitationStatistics invitationStatistics = new InvitationStatistics();
			invitationStatistics.setEmployeeId(employeeId);
			invitationStatistics.setDate(nowFormat);
			invitationStatistics.setResultsCount(1L);
			invitationStatistics.setTradeTotal(0L);
			invitationStatistics.setTradeGoodsTotal(0L);
			invitationStatistics.setTradePriceTotal(BigDecimal.ZERO);
			log.info("invitationStatistics {}", invitationStatistics);
			invitationStatisticsRepository.save(invitationStatistics);
		}

	}

	/**
	 * 订单数据统计
	 * @param employeeId
	 * @param tracePrice+=
	 * @param goodsCount+=1
	 * 订单数量++
	 */
	@Transactional(rollbackFor = Exception.class)
	public void tradeStatistics(String employeeId, BigDecimal tracePrice, Long goodsCount,String orderId) {
		String nowFormat = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		Optional<InvitationStatisticsOrder> byDate = invitationStatisticsOrderRepository.getByDate(employeeId, nowFormat, orderId);

		log.info("employeeId ======== {}",employeeId);

		log.info("nowFormat ========== {}",nowFormat);

		log.info("byDate =========== {}", byDate);

		log.info("byDate.isPresent() =========== {}", byDate.isPresent());

		if(byDate.isPresent()){
			return;
		}else{
			InvitationStatisticsOrder invitationStatisticsOrder = new InvitationStatisticsOrder();
			invitationStatisticsOrder.setDate(nowFormat);
			invitationStatisticsOrder.setEmployeeId(employeeId);
			invitationStatisticsOrder.setOrderId(orderId);
			invitationStatisticsOrderRepository.save(invitationStatisticsOrder);
		}
		Long byId = invitationStatisticsRepository.getCountById(employeeId,nowFormat);
//		Optional<InvitationStatistics> byId = invitationStatisticsRepository.getById(employeeId, nowFormat);
//		InvitationStatistics byId = invitationStatisticsRepository.getByEmployeeIdAndDate(employeeId, nowFormat);

		log.info("byId =========== {}", byId);

//		log.info("byId.isNOtNull() =========== {}", byId.isPresent());

		if (byId>0) {
			//更新订单信息
			goodsCount = goodsCount == null ? 0L : goodsCount;
			invitationStatisticsRepository.updateTradeInfo(employeeId, nowFormat, tracePrice, goodsCount);
		}else {
			//初始化订单信息
			InvitationStatistics invitationStatistics = new InvitationStatistics();
			invitationStatistics.setEmployeeId(employeeId);
			invitationStatistics.setDate(nowFormat);
			invitationStatistics.setResultsCount(0L);
			invitationStatistics.setTradePriceTotal(tracePrice);
			invitationStatistics.setTradeGoodsTotal(goodsCount == null ? 0L : goodsCount);
			invitationStatistics.setTradeTotal(1L);
			log.info("invitationStatistics {}", invitationStatistics);
			invitationStatisticsRepository.save(invitationStatistics);
		}
	}

	/**
	 * 查询当月数据
	 * @param registerStatisticsRequest
	 * @return
	 */
	public List<InvitationStatistics> getMonthByEmployeeId(InvitationRegisterStatisticsRequest registerStatisticsRequest){
		return invitationStatisticsRepository.getMonthByEmployeeId(registerStatisticsRequest.getEmployeeId());
	}

	/**
	 * 查询当天所欲数据
	 * @param registerStatisticsRequest
	 * @return
	 */


	public List<InvitationStatistics> getToday(InvitationRegisterStatisticsRequest registerStatisticsRequest){
		return invitationStatisticsRepository.getToday(registerStatisticsRequest.getDate());
	}


	public InvitationStatisticsVO wrapperVo(InvitationStatistics invitationStatistics) {
		if (invitationStatistics != null){
			InvitationStatisticsVO invitationStatisticsVO = KsBeanUtil.convert(invitationStatistics, InvitationStatisticsVO.class);
			return invitationStatisticsVO;
		}
		return null;
	}

}


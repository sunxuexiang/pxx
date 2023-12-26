package com.wanmi.sbc.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.order.bean.enums.PayState;
import com.wanmi.sbc.order.bean.vo.TradeEventLogVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;

public class SensitiveFieldUtil {

	public static String handleName(String name) {
		if (StringUtils.isBlank(name)) {
			return name;
		}
		return name.replaceAll(".+", "****");
	}

	public static String handleAccount(String account) {
		if (StringUtils.isBlank(account)) {
			return account;
		}
//		account = account.replaceAll("(\\w{3})\\w+(\\w{3})", "$1****$2");
		return account.replaceAll(".+", "****");
	}

	public static String handlePhone(String phone) {
		if (StringUtils.isBlank(phone)) {
			return phone;
		}
		return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
	}

	/**
	 * 订单信息脱敏
	 * @author zhangchen
	 * @param tradeVO
	 */
	public static void handleTradeVO(TradeVO tradeVO) {
		if (PayState.NOT_PAID == tradeVO.getTradeState().getPayState()) {
			String name = tradeVO.getBuyer().getName();
			String account = tradeVO.getBuyer().getAccount();
			String phone = tradeVO.getConsignee().getPhone();
			name = handleName(name);
			account = handleAccount(account);
			phone = handlePhone(phone);
			tradeVO.getBuyer().setName(name);
			tradeVO.getBuyer().setAccount(account);
			tradeVO.getConsignee().setPhone(phone);

			List<TradeEventLogVO> tradeEventLogs = tradeVO.getTradeEventLogs();
			for (TradeEventLogVO tradeEventLogVO : tradeEventLogs) {
				Operator operator = tradeEventLogVO.getOperator();
				if (Platform.CUSTOMER == operator.getPlatform()) {
					String operatorName = handleName(operator.getName());
					String operatorAccount = handleAccount(operator.getAccount());
					operator.setName(operatorName);
					operator.setAccount(operatorAccount);
				}
			}

		}
	}
}

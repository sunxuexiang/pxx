package com.wanmi.sbc.wallet.paycallbackresult.model.root;


import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.wallet.bean.enums.PayWalletCallBackType;
import com.wanmi.sbc.walletorder.bean.enums.PayWalletCallBackResultStatus;
import lombok.Data;

import javax.persistence.*;

/**
 * <p>支付回调结果实体类</p>
 * @author lvzhenwei
 * @date 2020-07-01 17:34:23
 */
@Data
@Entity
@Table(name = "pay_call_back_result")
public class PayCallBackResult extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * 订单号
	 */
	@Column(name = "business_id")
	private String businessId;

	/**
	 * 回调结果xml内容
	 */
	@Column(name = "result_xml")
	private String resultXml;

	/**
	 * 回调结果内容
	 */
	@Column(name = "result_context")
	private String resultContext;

	/**
	 * 结果状态，0：待处理；1:处理中 2：处理成功；3：处理失败
	 */
	@Column(name = "result_status")
	@Enumerated
	private PayWalletCallBackResultStatus resultStatus;

	/**
	 * 处理失败次数
	 */
	@Column(name = "error_num")
	private Integer errorNum;

	/**
	 * 支付方式，0：微信；1：支付宝；2：银联 3：招商
	 */
	@Column(name = "pay_type")
	@Enumerated
	private PayWalletCallBackType payType;

}
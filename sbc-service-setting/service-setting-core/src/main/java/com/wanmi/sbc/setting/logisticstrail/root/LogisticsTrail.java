package com.wanmi.sbc.setting.logisticstrail.root;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import javax.persistence.*;
import com.wanmi.sbc.common.base.BaseEntity;

/**
 * @desc 快递轨迹记录
 * @author shiy  2023/6/8 14:17
*/
@Data
@Entity
@Table(name = "logistics_trail")
public class LogisticsTrail implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;

	/**
	 * 物流公司编号
	 */
	@Column(name="com")
	private String com;

	/**
	 * 快递单号
	 */
	@Column(name="num")
	private String num;

	/**
	 * 运单轨迹
	 */
	@Column(name="data")
	private String data;

	/**
	 * 创建时间
	 */
	@Column(name="create_time")
	private Date createTime;
}
package com.wanmi.sbc.message.imhistory.root;

import com.wanmi.sbc.message.bean.enums.ReviewStatus;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * <p>im历史</p>
 * @author sgy
 * @date 2023-07-03 15:49:24
 */
@Data
@Entity
@Table(name = "im_history")
public class ImHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * 发送人
	 */
	@Column(name = "from_account")
	private String fromAccount;

	/**
	 * 接受人
	 */
	@Column(name = "to_account")
	private String toAccount;

	/**
	 * im 时间戳
	 */
	@Column(name = "msg_time")
	private String msgTime;

	/**
	 *第三方字段
	 */
	@Column(name = "msg_seq")
	private String msgSeq;
	/**
	 *第三方字段
	 */
	@Column(name = "msg_random")
	private String msgRandom;




}
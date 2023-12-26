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
@Table(name = "im_history_msg")
public class ImHistoryMsg implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * id
	 */
	@Column(name = "im_history_id")
	private Long imHistoryId;

	/**
	 * 类型
	 */
	@Column(name = "msg_type")
	private String msgType;

	/**
	 * im 内容
	 */
	@Column(name = "msg_content")
	private String msgContent;




}
package com.wanmi.sbc.setting.publishInfo.root;

import com.wanmi.sbc.common.enums.DeleteFlag;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * <p>信息发布</p>
 * @author lwp
 * @date 2023/10/18
 */
@Data
@Entity
@Table(name = "publish_info")
public class PublishInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * 标题
	 */
	@Column(name = "title")
	private String title;

	/**
	 * 内容
	 */
	@Column(name = "content")
	private String content;

	/**
	 * 是否删除 0 否  1 是
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;
}
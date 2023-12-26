package com.wanmi.sbc.setting.publishInfo.root;

import com.wanmi.sbc.common.enums.DeleteFlag;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * <p>信息发布用户</p>
 * @author lwp
 * @date 2023/10/18
 */
@Data
@Entity
@Table(name = "publish_user")
public class PublishUser implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * 姓名
	 */
	@Column(name = "user_name")
	private String userName;

	/**
	 * 验证
	 */
	@Column(name = "user_pass")
	private String userPass;

	/**
	 * 是否删除 0 否  1 是
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;

}
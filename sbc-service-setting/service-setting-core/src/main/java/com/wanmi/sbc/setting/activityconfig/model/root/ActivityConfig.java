package com.wanmi.sbc.setting.activityconfig.model.root;

import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.common.enums.DeleteFlag;
import lombok.Data;

import javax.persistence.*;

/**
 * <p>导航配置实体类</p>
 * @author lvheng
 * @date 2021-04-19 18:49:30
 */
@Data
@Entity
@Table(name = "activity_config")
public class ActivityConfig extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 活动配置id
	 */
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	/**
	 * 配置名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 配置别名
	 */
	@Column(name = "alias")
	private String alias;

	/**
	 * 配置别名
	 */
	@Column(name = "value")
	private String value;

	/**
	 * 删除标志 0未删除 1已删除
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;

}
package com.wanmi.sbc.setting.navigationconfig.model.root;


import lombok.Data;
import javax.persistence.*;
import com.wanmi.sbc.common.base.BaseEntity;

import java.io.Serializable;

/**
 * <p>导航配置实体类</p>
 * @author lvheng
 * @date 2021-04-12 14:46:18
 */
@Data
@Entity
@Table(name = "navigation_config")
public class NavigationConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	/**
	 * 导航名称
	 */
	@Column(name = "nav_name")
	private String navName;

	/**
	 * 导航图标-未点击状态
	 */
	@Column(name = "icon_show")
	private String iconShow;

	/**
	 * 导航图标-点击状态
	 */
	@Column(name = "icon_click")
	private String iconClick;

}
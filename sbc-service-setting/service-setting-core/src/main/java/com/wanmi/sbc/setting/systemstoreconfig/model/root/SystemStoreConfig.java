package com.wanmi.sbc.setting.systemstoreconfig.model.root;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.enums.DeleteFlag;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

/**
 * <p>系统配置表实体类</p>
 * @author yang
 * @date 2019-11-05 18:33:04
 */
@Data
@Entity
@Table(name = "system_store_config")
public class SystemStoreConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *  编号
	 */
	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;

	/**
	 * 店铺主键
	 */
	@Column(name="store_id")
	private Long storeId;

	/**
	 * 键
	 */
	@Column(name="config_key")
	private String configKey;

	/**
	 * 类型
	 */
	@Column(name="config_type")
	private String configType;

	/**
	 * 名称
	 */
	@Column(name="config_name")
	private String configName;

	/**
	 * 配置内容，如json内容
	 */
	@Column(name="config_value")
	private String configValue;

	/**
	 * 状态，0:未启用1:已启用
	 */
	@Column(name="status")
	private Integer status;

	/**
	 * 删除标识,0:未删除1:已删除
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;

	/**
	 * 备注
	 */
	@Column(name="remark")
	private String remark;

	/**
	 * 创建时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "create_time")
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "update_time")
	private LocalDateTime updateTime;
}
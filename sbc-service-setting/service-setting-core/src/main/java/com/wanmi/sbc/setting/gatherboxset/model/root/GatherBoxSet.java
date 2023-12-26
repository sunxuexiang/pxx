package com.wanmi.sbc.setting.gatherboxset.model.root;

import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.EnableStatus;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * <p>凑箱设置</p>
 * @author zwb
 * @date 2020-06-19 16:11:36
 */
@Data
@Entity
@Table(name = "gather_box_set")
public class GatherBoxSet extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "gather_box_set_id")
	private String gatherBoxSetId;

	/**
	 * 凑箱设置规格个数
	 */
	@Column(name = "sku_num")
	private Long skuNum;

	@Column(name = "banner")
	private String banner;

	/**
	 * 是否启用标志 0：停用，1：启用
	 */
	@Column(name = "status")
	private EnableStatus status;

	/**
	 * 是否删除标志 0：否，1：是
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;

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

	/**
	 * 创建人
	 */
	@Column(name = "create_person")
	private String createPerson;

	/**
	 * 修改人
	 */
	@Column(name = "update_person")
	private String updatePerson;

}
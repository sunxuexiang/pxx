package com.wanmi.sbc.setting.banneradmin.model.root;

import com.wanmi.sbc.common.enums.DeleteFlag;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import java.time.LocalDateTime;

import lombok.Data;
import javax.persistence.*;
import com.wanmi.sbc.common.base.BaseEntity;

/**
 * <p>轮播管理实体类</p>
 * @author 费传奇
 * @date 2020-12-08 11:44:38
 */
@Data
@Entity
@Table(name = "banner_admin")
public class BannerAdmin extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 名称
	 */
	@Column(name = "banner_name")
	private String bannerName;

	/**
	 * 一级类ID
	 */
	@Column(name = "one_cate_id")
	private Long oneCateId;

	/**
	 * 一级分类名称
	 */
	@Column(name = "one_cate_name")
	private String oneCateName;

	/**
	 * 排序号
	 */
	@Column(name = "banner_sort")
	private Integer bannerSort;

	/**
	 * 添加链接
	 */
	@Column(name = "link")
	private String link;

	/**
	 * banner图片
	 */
	@Column(name = "banner_img")
	private String bannerImg;

	/**
	 * 状态(0.显示 1.隐藏)
	 */
	@Column(name = "is_show")
	private Integer isShow;

	/**
	 * 删除标志
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;

	/**
	 * 删除时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "delete_time")
	private LocalDateTime deleteTime;

}
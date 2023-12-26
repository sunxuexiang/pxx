package com.wanmi.sbc.setting.videoresourcecate.model.root;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 视频教程资源资源分类表实体类
 * @author hudong
 * @date 2023-06-26 16:13:19
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "video_resource_cate")
public class VideoResourceCate implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 素材分类id
	 */
	@Id
//	@GeneratedValue(generator = "system-uuid")
//	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "cate_id")
	private String cateId;

	/**
	 * 店铺标识
	 */
	@Column(name = "store_id")
	private Long storeId;

	/**
	 * 商家标识
	 */
	@Column(name = "company_info_id")
	private Long companyInfoId;

	/**
	 * 分类名称
	 */
	@Column(name = "cate_name")
	private String cateName;

	/**
	 * 父分类ID
	 */
	@Column(name = "cate_parent_id")
	private String cateParentId;

	/**
	 * 分类图片
	 */
	@Column(name = "cate_img")
	private String cateImg;

	/**
	 * 分类层次路径,例1|01|001
	 */
	@Column(name = "cate_path")
	private String catePath;

	/**
	 * 分类层级
	 */
	@Column(name = "cate_grade")
	private Integer cateGrade;

	/**
	 * 拼音
	 */
	@Column(name = "pin_yin")
	private String pinYin;

	/**
	 * 简拼
	 */
	@Column(name = "s_pin_yin")
	private String spinYin;

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
	 * 删除标识,0:未删除1:已删除
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;

	/**
	 * 排序
	 */
	@Column(name = "sort")
	private Integer sort;

	/**
	 * 是否默认,0:否1:是
	 */
	@Column(name = "is_default")
	 private DefaultFlag isDefault;

	/**
	 * 分类类别 1-商家操作视频分类 2-用户操作视频分类
	 */
	@Column(name = "cate_type")
	private Integer cateType;

}
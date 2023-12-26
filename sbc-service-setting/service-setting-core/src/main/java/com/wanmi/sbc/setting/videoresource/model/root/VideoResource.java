package com.wanmi.sbc.setting.videoresource.model.root;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.bean.enums.ResourceType;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 视频教程资源库实体类
 * @author hudong
 * @date 2019-11-05 16:12:49
 */
@Data
@Entity
@Table(name = "video_resource")
public class VideoResource implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 素材ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "resource_id")
	private Long resourceId;

	/**
	 * 资源类型(0:图片,1:视频)
	 */
	@Column(name = "resource_type")
	private ResourceType resourceType;

	/**
	 * 素材分类ID
	 */
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
	 * 素材KEY
	 */
	@Column(name = "resource_key")
	private String resourceKey;

	/**
	 * 素材名称
	 */
	@Column(name = "resource_name")
	private String resourceName;

	/**
	 * 素材地址
	 */
	@Column(name = "artwork_url")
	private String artworkUrl;

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
	 * oss服务器类型，对应system_config的config_type
	 */
	@Column(name = "server_type")
	private String serverType;

	/**
	 * 横竖屏类别 1-横屏 2-竖屏
	 */
	@Column(name = "hv_type")
	private Integer hvType;

}
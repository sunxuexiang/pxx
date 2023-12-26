package com.wanmi.sbc.setting.videomanagement.model.root;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.wanmi.sbc.common.enums.DeleteFlag;

import com.wanmi.sbc.setting.bean.enums.StateType;
import lombok.Data;
import javax.persistence.*;
import com.wanmi.sbc.common.base.BaseEntity;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>视频管理实体类</p>
 * @author zhaowei
 * @date 2021-04-17 17:47:22
 */
@Data
@Entity
@Table(name = "video_management")
public class VideoManagement implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "video_id")
	private Long videoId;

	/**
	 * 视频名称
	 */
	@Column(name = "video_name")
	private String videoName;

	/**
	 * 状态0:上架,1:下架
	 */
	@Column(name = "state")
	@Enumerated
	private StateType state;

	/**
	 * 播放数
	 */
	@Column(name = "play_few")
	private Long playFew;

	/**
	 * 素材KEY
	 */
	@Column(name = "resource_key")
	private String resourceKey;

	/**
	 * 素材地址
	 */
	@Column(name = "artwork_url")
	private String artworkUrl;

	/**
	 * 删除标识,0:未删除1:已删除
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
	 * oss服务器类型，对应system_config的config_type
	 */
	@Column(name = "server_type")
	private String serverType;

	/**
	 * 上传用户id
	 */
	@Column(name = "cover_follow_customer_id")
	private String coverFollowCustomerId;

	/**
	 * 封面图片地址
	 */
	@Column(name = "cover_img")
	private String coverImg;

	/**
	 * 店铺ID
	 */
	@Column(name = "store_id")
	private Long storeId;

	/**
	 * 商品链接
	 */
	@Column(name = "goods_link")
	private String goodsLink;

	/**
	 * 商品SKUid
	 */
	@Column(name = "goods_info_id")
	private String goodsInfoId;


	@Column(name = "goods_id")
	private String goodsId;
}
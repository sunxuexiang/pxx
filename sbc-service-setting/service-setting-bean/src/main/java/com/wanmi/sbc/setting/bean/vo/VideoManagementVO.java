package com.wanmi.sbc.setting.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTime2Serializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.bean.enums.StateType;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>视频管理VO</p>
 * @author zhaowei
 * @date 2021-04-17 17:47:22
 */
@ApiModel
@Data
public class VideoManagementVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@ApiModelProperty(value = "ID")
	private Long videoId;

	/**
	 * 视频名称
	 */
	@ApiModelProperty(value = "视频名称")
	private String videoName;

	/**
	 * 状态0:上架,1:下架
	 */
	@ApiModelProperty(value = "状态0:上架,1:下架")
	private StateType state;

	/**
	 * 播放数
	 */
	@ApiModelProperty(value = "播放数")
	private Long playFew;

	/**
	 * 素材KEY
	 */
	@ApiModelProperty(value = "素材KEY")
	private String resourceKey;

	/**
	 * 素材地址
	 */
	@ApiModelProperty(value = "素材地址")
	private String artworkUrl;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "发布时间")
	@JsonSerialize(using = CustomLocalDateTime2Serializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@ApiModelProperty(value = "更新时间")
	@JsonSerialize(using = CustomLocalDateTime2Serializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTime;


	/**
	 * oss服务器类型，对应system_config的config_type
	 */
	@ApiModelProperty(value = "oss服务器类型，对应system_config的config_type")
	private String serverType;

	/**
	 * 点赞数
	 */
	@ApiModelProperty(value = "点赞数")
	private Long videoLikeNum;

	/**
	 * 是否点赞
	 * @return:
	 */
	@ApiModelProperty(value = "是否点赞（0：没点，1:点了）")
	private Integer likeIt = 0;

	/**
	 * 上传用户id
	 */
	@ApiModelProperty(value = "上传用户id")
	private String coverFollowCustomerId;

	@ApiModelProperty(value = "是否关注 0 未关注 1 关注")
	private Integer isFollow = 0;

	/**
	 * 会员名称
	 */
	@ApiModelProperty(value = "发布用户昵称")
	private String employeeName;

	/**
	 * 封面图片地址
	 */
	@ApiModelProperty(value = "封面图片地址")
	private String coverImg;

	/**
	 * 店铺ID
	 */
	@ApiModelProperty(value = "店铺ID")
	private Long storeId;

	@ApiModelProperty(value = "店铺名称")
	private String storeName;

	@ApiModelProperty(value = "店铺Logo")
	private String storeLogo;

	/**
	 * 商品链接
	 */
	@ApiModelProperty(value = "商品链接")
	private String goodsLink;

	/**
	 * 商品skuId
	 */
	@ApiModelProperty(value = "商品skuId")
	private String goodsInfoId;

	/**
	 * 商品Id
	 */
	@ApiModelProperty(value = "商品Id")
	private String goodsId;

	@ApiModelProperty(value = "商品skuName")
	private String goodsInfoName;
}
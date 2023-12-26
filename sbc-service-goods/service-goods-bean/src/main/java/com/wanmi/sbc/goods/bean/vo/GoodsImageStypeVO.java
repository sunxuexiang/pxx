package com.wanmi.sbc.goods.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 图片类型表用于合成图片使用
 */
@ApiModel
@Data
public class GoodsImageStypeVO  {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "imagesTypeId")
	private Long imagesTypeId;

	/**
	 * SPUID
	 */
	@ApiModelProperty(value = "SPUID")
	private String goodsId;

	/**
	 * SKUID
	 */
	@ApiModelProperty(value = "SKUID")
	private String goods_info_id;

	/**
	 * 图片地址
	 */
	@ApiModelProperty(value = "图片地址")
	private String artwork_url;

	/**
	 * 类型0是促销图片1是合成图片
	 */
	@ApiModelProperty(value = "类型")
	private int  type;


	/**
	 *关联id 如果是合成图片会有这个字段
	 */
	@ApiModelProperty(value = "关联id")
	private Long relationId;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	private LocalDateTime createTime;

	/**
     * 是否删除标志
	 */
	@ApiModelProperty(value = "是否删除标志")
	private int  delFlag;


	/**
	 * 是否选中 0没选中 1选中
	 */
	@ApiModelProperty(value = "是否选中")
	private int  checkFlag;

}
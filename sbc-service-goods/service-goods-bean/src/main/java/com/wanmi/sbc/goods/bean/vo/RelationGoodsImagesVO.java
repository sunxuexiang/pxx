package com.wanmi.sbc.goods.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 图片关系表用于合成图片使用
 */
@ApiModel
@Data
public class RelationGoodsImagesVO {
	private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "relationId")
	private Long relationId;

	@ApiModelProperty(value = "imagesTypeId")
	private Long imagesTypeId;

	@ApiModelProperty(value = "cxImageId")
	private Long cxImageId;


	/**
	 * SPUID
	 */
	@ApiModelProperty(value = "goodsId")
	private String goodsId;

	/**
	 * SKUID
	 */
	@ApiModelProperty(value = "goods_info_id")
	private String goods_info_id;

	@ApiModelProperty(value = "imageId")
	private Long imageId;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "create_time")
	private LocalDateTime create_time;

	/**
	 * 是否删除标志
	 */
	@ApiModelProperty(value = "delFlag")
	private int  delFlag;

}
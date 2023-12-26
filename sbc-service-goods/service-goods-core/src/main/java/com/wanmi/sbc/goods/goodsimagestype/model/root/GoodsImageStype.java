package com.wanmi.sbc.goods.goodsimagestype.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 图片类型表用于合成图片使用
 */
@Data
@Entity
@Table(name = "goods_images_type")
public class GoodsImageStype{
	private static final long serialVersionUID = 1L;


	@Id
	@Column(name = "images_type_id")
	private Long imagesTypeId;

	/**
	 * SPUID
	 */
	@Column(name = "goods_id")
	private String goodsId;

	/**
	 * SKUID
	 */
	@Column(name = "goods_info_id")
	private String goods_info_id;

	/**
	 * 图片地址
	 */
	@Column(name = "artwork_url")
	private String artwork_url;

	/**
	 * 类型0是促销图片1是合成图片
	 */
	@Column(name = "type")
	private int  type;


	/**
	 *关联id 如果是合成图片会有这个字段
	 */
	@Column(name = "relation_id")
	private Long relationId;

//	/**
//	 * 创建时间
//	 */
//	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
//	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
//	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//	@Column(name = "create_time")
//	private LocalDateTime createTime;

	/**
	 * 是否删除标志
	 */
	@Column(name = "del_flag")
	private int  delFlag;

}
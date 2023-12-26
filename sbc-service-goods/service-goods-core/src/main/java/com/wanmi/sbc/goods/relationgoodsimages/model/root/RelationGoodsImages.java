package com.wanmi.sbc.goods.relationgoodsimages.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseEntity;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 图片关联关系表用于合成图片使用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "relation_goods_images")
public class RelationGoodsImages {
	private static final long serialVersionUID = 1L;


	@Id
	@Column(name = "relation_id")
	private Long relationId;

	@Column(name = "images_type_id")
	private Long imagesTypeId;

	@Column(name = "cx_image_id")
	private Long cxImageId;


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

	@Column(name = "image_id")
	private Long imageId;

	/**
	 * 创建时间
	 */
/*	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	@Column(name = "create_time")
	private LocalDateTime create_time;*/

	/**
     * 是否删除标志
	 */
	@Column(name = "del_flag")
	private int  delFlag;

}
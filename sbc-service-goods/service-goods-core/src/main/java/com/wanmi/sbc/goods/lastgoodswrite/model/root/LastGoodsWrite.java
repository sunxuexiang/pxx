package com.wanmi.sbc.goods.lastgoodswrite.model.root;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import javax.persistence.*;
import com.wanmi.sbc.common.base.BaseEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.time.LocalDateTime;

/**
 * <p>用户最后一次商品记录实体类</p>
 * @author 费传奇
 * @date 2021-04-23 17:33:51
 */
@Data
@Entity
@Table(name = "last_goods_write")
public class LastGoodsWrite  {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * 用户id
	 */
	@Column(name = "customer_id")
	private String customerId;

	/**
	 * 商品skuid
	 */
	@Column(name = "goods_info_id")
	private String goodsInfoId;

	/**
	 * 品牌Id
	 */
	@Column(name = "brand_id")
	private Long brandId;

	/**
	 * 类目id
	 */
	@Column(name = "cate_id")
	private Long cateId;

	@CreatedDate
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@Column(name = "create_time")
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@LastModifiedDate
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	@Column(name = "update_time")
	private LocalDateTime updateTime;

}
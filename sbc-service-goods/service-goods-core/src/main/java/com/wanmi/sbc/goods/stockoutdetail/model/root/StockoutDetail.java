package com.wanmi.sbc.goods.stockoutdetail.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;

import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.stockoutmanage.model.root.StockoutManage;
import lombok.Data;
import javax.persistence.*;
import com.wanmi.sbc.common.base.BaseEntity;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>缺货管理实体类</p>
 * @author tzx
 * @date 2020-05-27 10:48:14
 */
@Data
@Entity
@Table(name = "stockout_detail")
public class StockoutDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 缺货明细
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "stockout_detail_id")
	private String stockoutDetailId;


	/**
	 * 缺货列表id
	 */
	@Column(name = "stockout_id")
	private String stockoutId;

	/**
	 * 会员id
	 */
	@Column(name = "customer_id")
	private String customerId;

	/**
	 * sku id
	 */
	@Column(name = "goods_info_id")
	private String goodsInfoId;

	/**
	 * sku编码
	 */
	@Column(name = "goods_info_no")
	private String goodsInfoNo;

	/**
	 * 缺货数量
	 */
	@Column(name = "stockout_num")
	private Long stockoutNum;

	/**
	 * 缺货市code
	 */
	@Column(name = "city_code")
	private String cityCode;

	/**
	 * 下单人详细地址
	 */
	@Column(name = "address")
	private String address;

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
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	@Column(name = "create_time")
	private LocalDateTime createTime;

	@Column(name = "ware_id")
	private Long wareId;

}
package com.wanmi.sbc.goods.stockoutmanage.model.root;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.ReplenishmentFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.stockoutdetail.model.root.StockoutDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>缺货管理实体类</p>
 * @author tzx
 * @date 2020-05-27 09:37:01
 */
@Data
@Entity
@Table(name = "stockout_manage")
public class StockoutManage implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 缺货管理
	 */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "stockout_id")
	private String stockoutId;


	/**
	 * 商品名称
	 */
	@Column(name = "goods_name")
	private String goodsName;

	@ApiModelProperty("商品商品Id")
	@Column(name = "cate_id")
	private Long cateId;

	@ApiModelProperty("商品品类名称")
	@Column(name = "cate_name")
	private String cateName;

	@ApiModelProperty("erpSKU编码")
	@Column(name = "erp_goods_info_no")
	private String erpGoodsInfoNo;

	/**
	 * sku id
	 */
	@Column(name = "goods_info_id")
	private String goodsInfoId;

	/**
	 * sku 编码
	 */
	@Column(name = "goods_info_no")
	private String goodsInfoNo;

	/**
	 * 品牌id
	 */
	@Column(name = "brand_id")
	private Long brandId;

	/**
	 * 品牌名称
	 */
	@Column(name = "brand_name")
	private String brandName;

	/**
	 * 缺货数量
	 */
	@Column(name = "stockout_num")
	private Long stockoutNum;

	/**
	 * 缺货地区
	 */
	@Column(name = "stockout_city")
	private String stockoutCity;

	/**
	 * 删除标识,0:未删除1:已删除
	 */
	@Column(name = "del_flag")
	@Enumerated
	private DeleteFlag delFlag;

	/**
	 * 补货标识,0:暂未补齐1:已经补齐:2缺货提醒
	 */
	@Column(name = "replenishment_flag")
	@Enumerated
	private ReplenishmentFlag replenishmentFlag;

	/**
	 * 规格信息
	 */
	@OneToMany
	@JsonManagedReference
	@JoinColumn(name = "stockout_id", insertable = false, updatable = false)
	private List<StockoutDetail> stockoutDetailList;


	/**
	 * 创建时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	@Column(name = "create_time")
	private LocalDateTime createTime;

	/**
	 * 修改时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	@Column(name = "update_time")
	private LocalDateTime updateTime;
	/**
	 * 店铺id
	 */
	@Column(name = "store_id")
	private Long storeId;
	/**
	 * 商品图片
	 */
	@Column(name = "goods_info_img")
	private String goodsInfoImg;

	/**
	 * 仓库ID
	 */
	@Column(name = "ware_id")
	private Long wareId;

	/**
	 * 补货时间
	 */
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	@Column(name = "replenishment_time")
	private LocalDateTime replenishmentTime;

	/**
	 * 缺货天数
	 */
	@Column(name = "stockout_day")
	private Long stockoutDay;

	/**
	 *  来源 1 商家前端触发 2 运营后台统计
	 */
	@Column(name = "source")
	private Integer source;

	@ApiModelProperty("上下架状态,0:下架1:上架")
	@Column(name = "added_flag")
	private AddedFlag addedFlag;

	@ApiModelProperty("缺货时间")
	@Column(name = "stockout_time")
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime stockoutTime;
}
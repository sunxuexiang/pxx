package com.wanmi.sbc.goods.api.request.stockoutmanage;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.ReplenishmentFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * <p>缺货管理修改参数</p>
 * @author tzx
 * @date 2020-05-27 11:37:26
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockoutManageModifyRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 缺货管理
	 */
	@ApiModelProperty(value = "缺货管理")
	@Length(max=32)
	private String stockoutId;

	/**
	 * 商品名称
	 */
	@ApiModelProperty(value = "商品名称")
	@Length(max=255)
	private String goodsName;

	@ApiModelProperty("商品品类Id")
	private Long cateId;

	@ApiModelProperty("商品品类名称")
	private String cateName;

	/**
	 * sku id
	 */
	@ApiModelProperty(value = "sku id")
	@NotBlank
	@Length(max=32)
	private String goodsInfoId;

	/**
	 * sku 编码
	 */
	@ApiModelProperty(value = "sku 编码")
	@NotBlank
	@Length(max=32)
	private String goodsInfoNo;

	/**
	 * 品牌id
	 */
	@ApiModelProperty(value = "品牌id")
	@Max(9223372036854775807L)
	private Long brandId;

	/**
	 * 品牌名称
	 */
	@ApiModelProperty(value = "品牌名称")
	@Length(max=45)
	private String brandName;

	/**
	 * 缺货数量
	 */
	@ApiModelProperty(value = "缺货数量")
	@Max(9223372036854775807L)
	private Long stockoutNum;

	/**
	 * 缺货地区
	 */
	@ApiModelProperty(value = "缺货地区")
	@Length(max=3000)
	private String stockoutCity;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间", hidden = true)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	/**
	 * 补货标识,0:暂未补齐1:已经补齐:2缺货提醒
	 */
	@ApiModelProperty(value = "补货标识,0:暂未补齐1:已经补齐:2缺货提醒")
	private ReplenishmentFlag replenishmentFlag;
	/**
	 * 店铺id
	 */
	@ApiModelProperty(value = "店铺id")
	private Long storeId;

	/**
	 * 缺货天数
	 */
	@ApiModelProperty(value = " 缺货天数")
	private Long stockoutDay;

	/**
	 * 补货时间
	 */
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime replenishmentTime;

	/**
	 * 仓库ID
	 */
	@ApiModelProperty(value = "仓库ID ")
	private Long wareId;

	@ApiModelProperty(value = "del_flag")
	private DeleteFlag delFlag;

	/**
	 *  来源 1 商家前端触发 2 运营后台统计
	 */
	@ApiModelProperty(value = "source")
	private Integer source;

	@ApiModelProperty("上下架状态,0:下架1:上架")
	private AddedFlag addedFlag;

	@ApiModelProperty("缺货时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime stockoutTime;


	@ApiModelProperty("erpSKU编码")
	private String erpGoodsInfoNo;

	@ApiModelProperty("商品图片")
	private String goodsInfoImg;

}
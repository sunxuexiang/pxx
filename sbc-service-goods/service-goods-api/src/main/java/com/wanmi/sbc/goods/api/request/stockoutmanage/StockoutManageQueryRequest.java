package com.wanmi.sbc.goods.api.request.stockoutmanage;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.ReplenishmentFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>缺货管理通用查询请求参数</p>
 * @author tzx
 * @date 2020-05-27 11:37:26
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockoutManageQueryRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-缺货管理List
	 */
	@ApiModelProperty(value = "批量查询-缺货管理List")
	private List<String> stockoutIdList;

	/**
	 * 缺货管理
	 */
	@ApiModelProperty(value = "缺货管理")
	private String stockoutId;

	/**
	 * 商品名称
	 */
	@ApiModelProperty(value = "商品名称")
	private String goodsName;

	@ApiModelProperty("商品品类Id")
	private Long cateId;

	@ApiModelProperty("商品品类名称")
	private String cateName;

	@ApiModelProperty("erpSKU编码")
	private String erpGoodsInfoNo;

	/**
	 * 仓库id
	 */
	@ApiModelProperty(value = "仓库id")
	private Long wareId;


	/**
	 * sku id
	 */
	@ApiModelProperty(value = "sku id")
	private String goodsInfoId;

	/**
	 * sku 编码
	 */
	@ApiModelProperty(value = "sku 编码")
	private String goodsInfoNo;

	/**
	 * 品牌id
	 */
	@ApiModelProperty(value = "品牌id")
	private Long brandId;

	/**
	 * 品牌名称
	 */
	@ApiModelProperty(value = "品牌名称")
	private String brandName;

	/**
	 * 缺货数量
	 */
	@ApiModelProperty(value = "缺货数量")
	private Long stockoutNum;

	/**
	 * 缺货地区
	 */
	@ApiModelProperty(value = "缺货地区")
	private String stockoutCity;

	/**
	 * 搜索条件:修改时间开始
	 */
	@ApiModelProperty(value = "搜索条件:修改时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTimeBegin;
	/**
	 * 搜索条件:修改时间截止
	 */
	@ApiModelProperty(value = "搜索条件:修改时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTimeEnd;

	/**
	 * 搜索条件:创建时间开始
	 */
	@ApiModelProperty(value = "搜索条件:创建时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTimeBegin;
	/**
	 * 搜索条件:创建时间截止
	 */
	@ApiModelProperty(value = "搜索条件:创建时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTimeEnd;

	/**
	 * 删除标识,0:未删除1:已删除
	 */
	@ApiModelProperty(value = "删除标识,0:未删除1:已删除")
	private DeleteFlag delFlag;

	/**
	 * 补货标识,0:暂未补齐1:已经补齐:2缺货提醒
	 */
	@ApiModelProperty(value = "补货标识,0:暂未补齐1:已经补齐:2缺货提醒")
	private ReplenishmentFlag replenishmentFlag;

	@ApiModelProperty(value = "补货标识,0:暂未补齐1:已经补齐:2缺货提醒")
	private List<ReplenishmentFlag> replenishmentFlagList;


	/**
	 * 店铺id
	 */
	@ApiModelProperty(value = "店铺id")
	private Long storeId;

	/**
	 * 创建时间(缺货时间)
	 */
	@ApiModelProperty(value = "缺货时间")
	private Date createTime;

	/**
	 * 缺货管理开始时间
	 */
	@ApiModelProperty(value = "缺货管理开始时间")
	private String beginTime;

	/**
	 * 缺货管理结束时间
	 */
	@ApiModelProperty(value = "缺货管理结束时间")
	private String endTime;

	/**
	 * 来源(1 商家后台  2 运营后台)
	 */
	@ApiModelProperty(value = "来源")
	private Integer source = 0;

	/**
	 * 缺货天数
	 */
	@ApiModelProperty(value = "缺货天数")
	private Integer stockoutDay;

	@ApiModelProperty("上下架状态,0:下架1:上架")
	private AddedFlag addedFlag;

	@ApiModelProperty("缺货时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime stockoutTime;

	@ApiModelProperty(value = "搜索条件:缺货时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime stockoutBegin;

	@ApiModelProperty(value = "搜索条件:缺货时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime stockoutEnd;

	@ApiModelProperty(value = "搜索条件:补货时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime replenishmentTimeBegin;

	@ApiModelProperty(value = "搜索条件:补货时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime replenishmentTimeEnd;


}
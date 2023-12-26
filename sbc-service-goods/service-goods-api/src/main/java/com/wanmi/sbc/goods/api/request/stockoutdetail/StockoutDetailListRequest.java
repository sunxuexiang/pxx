package com.wanmi.sbc.goods.api.request.stockoutdetail;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>缺货管理列表查询请求参数</p>
 * @author tzx
 * @date 2020-05-27 11:37:12
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockoutDetailListRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-缺货明细List
	 */
	@ApiModelProperty(value = "批量查询-缺货明细List")
	private List<String> stockoutDetailIdList;

	/**
	 * 缺货明细
	 */
	@ApiModelProperty(value = "缺货明细")
	private String stockoutDetailId;

	/**
	 * 缺货列表id
	 */
	@ApiModelProperty(value = "缺货列表id")
	private String stockoutId;

	/**
	 * 会员id
	 */
	@ApiModelProperty(value = "会员id")
	private String customerId;

	/**
	 * sku id
	 */
	@ApiModelProperty(value = "sku id")
	private String goodsInfoId;

	/**
	 * sku编码
	 */
	@ApiModelProperty(value = "sku编码")
	private String goodsInfoNo;

	/**
	 * 缺货数量
	 */
	@ApiModelProperty(value = "缺货数量")
	private Long stockoutNum;

	/**
	 * 缺货市code
	 */
	@ApiModelProperty(value = "缺货市code")
	private String cityCode;

	/**
	 * 下单人详细地址
	 */
	@ApiModelProperty(value = "下单人详细地址")
	private String address;

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

}
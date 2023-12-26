package com.wanmi.sbc.goods.api.request.stockoutdetail;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>缺货管理修改参数</p>
 * @author tzx
 * @date 2020-05-27 11:37:12
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockoutDetailModifyRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 缺货明细
	 */
	@ApiModelProperty(value = "缺货明细")
	@Length(max=32)
	private String stockoutDetailId;

	/**
	 * 缺货列表id
	 */
	@ApiModelProperty(value = "缺货列表id")
	@NotBlank
	@Length(max=32)
	private String stockoutId;

	/**
	 * 会员id
	 */
	@ApiModelProperty(value = "会员id")
	@Length(max=32)
	private String customerId;

	/**
	 * sku id
	 */
	@ApiModelProperty(value = "sku id")
	@Length(max=32)
	private String goodsInfoId;

	/**
	 * sku编码
	 */
	@ApiModelProperty(value = "sku编码")
	@Length(max=32)
	private String goodsInfoNo;

	/**
	 * 缺货数量
	 */
	@ApiModelProperty(value = "缺货数量")
	@Max(9223372036854775807L)
	private Long stockoutNum;

	/**
	 * 缺货市code
	 */
	@ApiModelProperty(value = "缺货市code")
	@Length(max=32)
	private String cityCode;

	/**
	 * 下单人详细地址
	 */
	@ApiModelProperty(value = "下单人详细地址")
	@Length(max=128)
	private String address;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间", hidden = true)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

}
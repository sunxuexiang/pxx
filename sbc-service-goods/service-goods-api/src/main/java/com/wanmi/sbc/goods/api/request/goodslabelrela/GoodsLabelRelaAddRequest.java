package com.wanmi.sbc.goods.api.request.goodslabelrela;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;

/**
 * <p>邀新统计新增参数</p>
 * @author lvheng
 * @date 2021-04-23 14:20:19
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsLabelRelaAddRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */
	@ApiModelProperty(value = "商品id")
	@Length(max=32)
	private String goodsId;

	/**
	 * 标签id
	 */
	@ApiModelProperty(value = "标签id")
	@Max(9223372036854775807L)
	private Long labelId;

	/**
	 * 创建人id
	 */
	@ApiModelProperty(value = "创建人id", hidden = true)
	private String createPerson;

	/**
	 * 更新人id
	 */
	@ApiModelProperty(value = "更新人id", hidden = true)
	private String updatePerson;

}
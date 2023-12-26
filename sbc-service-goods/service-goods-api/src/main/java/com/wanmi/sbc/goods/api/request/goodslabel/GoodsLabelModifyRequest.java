package com.wanmi.sbc.goods.api.request.goodslabel;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;

/**
 * <p>导航配置修改参数</p>
 * @author lvheng
 * @date 2021-04-19 11:09:28
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsLabelModifyRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 标签id
	 */
	@ApiModelProperty(value = "标签id")
	@Max(9223372036854775807L)
	private Long id;

	/**
	 * 标签名称
	 */
	@ApiModelProperty(value = "标签名称")
	@Length(max=45)
	private String name;

	/**
	 * 商品列表展示开关 0: 关闭 1:开启
	 */
	@ApiModelProperty(value = "商品列表展示开关 0: 关闭 1:开启")
	private DefaultFlag visible;


	/**
	 * 标签图片
	 */
	@ApiModelProperty(value = "标签图片")
	@Length(max=500)
	private String image;

	/**
	 * 更新人
	 */
	private String updatePerson;


}
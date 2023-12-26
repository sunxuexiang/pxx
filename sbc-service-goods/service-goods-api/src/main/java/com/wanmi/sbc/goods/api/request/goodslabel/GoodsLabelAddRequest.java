package com.wanmi.sbc.goods.api.request.goodslabel;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

/**
 * <p>导航配置新增参数</p>
 * @author lvheng
 * @date 2021-04-19 11:09:28
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsLabelAddRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 标签名称
	 */
	@ApiModelProperty(value = "标签名称")
	@NotBlank
	@Length(max=45)
	private String name;

	/**
	 * 商品列表展示开关 0: 关闭 1:开启
	 */
	@ApiModelProperty(value = "商品列表展示开关 0: 关闭 1:开启")
	private DefaultFlag visible;

	/**
	 * 排序
	 */
	@ApiModelProperty(value = "排序")
	@Max(20)
	private Integer sort;

	/**
	 * 标签图片
	 */
	@ApiModelProperty(value = "标签图片")
	@NotBlank
	@Length(max=500)
	private String image;


	/**
	 * 删除标识 0:未删除1:已删除
	 */
	@ApiModelProperty(value = "删除标识 0:未删除1:已删除", hidden = true)
	private DeleteFlag delFlag;

	/**
	 * 创建人
	 */
	private String createPerson;

}
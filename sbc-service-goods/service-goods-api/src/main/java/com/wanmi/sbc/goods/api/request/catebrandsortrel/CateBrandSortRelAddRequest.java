package com.wanmi.sbc.goods.api.request.catebrandsortrel;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>类目品牌排序表新增参数</p>
 * @author lvheng
 * @date 2021-04-08 11:24:32
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CateBrandSortRelAddRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;



	@ApiModelProperty(value = "类目ID")
	private Long cateId;

	@ApiModelProperty(value = "品牌ID")
	private Long brandId;
	/**
	 * 品牌名称
	 */
	@ApiModelProperty(value = "品牌名称")
	@Length(max=255)
	private String name;

	/**
	 * 品牌别名
	 */
	@ApiModelProperty(value = "品牌别名")
	@Length(max=255)
	private String alias;

	/**
	 * 排序序号
	 */
	@ApiModelProperty(value = "排序序号")
	@Max(9999999999L)
	private Integer serialNo;

	/**
	 * 删除标识，0：未删除 1：已删除
	 */
	@ApiModelProperty(value = "删除标识，0：未删除 1：已删除", hidden = true)
	private DeleteFlag delFlag;

	/**
	 * 创建人
	 */
	@ApiModelProperty(value = "创建人", hidden = true)
	private String createPerson;

	/**
	 * 更新人
	 */
	@ApiModelProperty(value = "更新人", hidden = true)
	private String updatePerson;

}
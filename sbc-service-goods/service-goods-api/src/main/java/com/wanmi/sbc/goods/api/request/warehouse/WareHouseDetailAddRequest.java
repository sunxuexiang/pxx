package com.wanmi.sbc.goods.api.request.warehouse;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * <p>仓库表新增参数</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WareHouseDetailAddRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;


	/**
	 * 仓库名称
	 */
	@ApiModelProperty(value = "仓库名称")
	private String wareName;


	/**
	 * 创建人
	 */
	@ApiModelProperty(value = "创建人", hidden = true)
	private String createPerson;

	/**
	 * 编辑人
	 */
	@ApiModelProperty(value = "编辑人", hidden = true)
	private String updatePerson;

	/**
	 * 是否删除标志 0：否，1：是
	 */
	@ApiModelProperty(value = "是否删除标志 0：否，1：是", hidden = true)
	private DeleteFlag delFlag;


	@ApiModelProperty(value = "仓库ID")
	private Long wareId;

	@ApiModelProperty(value = "仓库用户须知图片")
	private String warePlayerImg;

	@ApiModelProperty(value = "唯一Id")
	private Integer id;
}
package com.wanmi.sbc.goods.api.request.warehousecity;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;


/**
 * <p>单个查询 仓库地区表请求参数</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:28:33
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WareHouseCityByIdRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@ApiModelProperty(value = "主键ID")
	@NotNull
	private Long id;

}
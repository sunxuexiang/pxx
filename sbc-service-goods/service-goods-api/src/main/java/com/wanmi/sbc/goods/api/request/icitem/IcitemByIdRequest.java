package com.wanmi.sbc.goods.api.request.icitem;

import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * <p>单个查询配送到家请求参数</p>
 * @author lh
 * @date 2020-12-05 18:16:34
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IcitemByIdRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * sku
	 */
	@ApiModelProperty(value = "sku")
	@NotNull
	private String sku;

}
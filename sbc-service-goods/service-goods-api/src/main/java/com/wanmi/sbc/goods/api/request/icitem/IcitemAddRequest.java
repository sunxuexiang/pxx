package com.wanmi.sbc.goods.api.request.icitem;

import java.math.BigDecimal;
import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>配送到家新增参数</p>
 * @author lh
 * @date 2020-12-05 18:16:34
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IcitemAddRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * name
	 */
	@ApiModelProperty(value = "name")
	@Length(max=255)
	private String name;

	/**
	 * tiji
	 */
	@ApiModelProperty(value = "tiji")
	private BigDecimal tiji;

	/**
	 * weight
	 */
	@ApiModelProperty(value = "weight")
	private BigDecimal weight;

}
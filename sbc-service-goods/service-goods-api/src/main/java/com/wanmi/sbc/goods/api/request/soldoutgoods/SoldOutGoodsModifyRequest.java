package com.wanmi.sbc.goods.api.request.soldoutgoods;

import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>类目品牌排序表修改参数</p>
 * @author lvheng
 * @date 2021-04-10 15:09:50
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoldOutGoodsModifyRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 将要被下架的商品
	 */
	@ApiModelProperty(value = "将要被下架的商品")
	@Length(max=50)
	private String goodsId;

}
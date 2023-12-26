package com.wanmi.sbc.goods.api.request.icitem;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import java.math.BigDecimal;

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
public class IcitemUpdateRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;


	/**
	 * 商品体积 单位：m3
	 */
	private BigDecimal goodsCubage;

	/**
	 * 商品重量
	 */
	private BigDecimal goodsWeight;

}
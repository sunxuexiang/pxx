package com.wanmi.sbc.goods.api.request.icitem;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>批量删除配送到家请求参数</p>
 * @author lh
 * @date 2020-12-05 18:16:34
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IcitemDelByIdListRequest extends GoodsBaseRequest {
private static final long serialVersionUID = 1L;

	/**
	 * 批量删除-skuList
	 */
	@ApiModelProperty(value = "批量删除-skuList")
	@NotEmpty
	private List<String> skuList;
}

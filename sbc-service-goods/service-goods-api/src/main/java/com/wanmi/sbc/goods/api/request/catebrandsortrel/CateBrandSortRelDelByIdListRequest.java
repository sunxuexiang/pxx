package com.wanmi.sbc.goods.api.request.catebrandsortrel;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>批量删除类目品牌排序表请求参数</p>
 * @author lvheng
 * @date 2021-04-08 11:24:32
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CateBrandSortRelDelByIdListRequest extends GoodsBaseRequest {
private static final long serialVersionUID = 1L;

	/**
	 * 批量删除-品类IDList
	 */
	@ApiModelProperty(value = "批量删除-品类IDList")
	@NotEmpty
	private List<Long> cateIdList;
}

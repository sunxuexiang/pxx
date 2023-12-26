package com.wanmi.sbc.goods.api.request.biddinggoods;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>批量删除竞价商品请求参数</p>
 * @author baijz
 * @date 2020-08-05 16:34:44
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiddingGoodsDelByIdListRequest extends GoodsBaseRequest {
private static final long serialVersionUID = 1L;

	/**
	 * 批量删除-竞价商品的IdList
	 */
	@ApiModelProperty(value = "批量删除-竞价商品的IdList")
	@NotEmpty
	private List<String> biddingGoodsIdList;
}

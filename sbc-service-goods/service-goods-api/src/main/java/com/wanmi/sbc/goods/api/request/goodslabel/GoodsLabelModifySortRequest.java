package com.wanmi.sbc.goods.api.request.goodslabel;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.goods.bean.vo.GoodsLabelVO;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;

/**
 * <p>导航配置修改参数</p>
 * @author lvheng
 * @date 2021-04-19 11:09:28
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsLabelModifySortRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * id+sort 必填，重置排序规则
	 */
	private List<GoodsLabelVO> goodsLabels;

}
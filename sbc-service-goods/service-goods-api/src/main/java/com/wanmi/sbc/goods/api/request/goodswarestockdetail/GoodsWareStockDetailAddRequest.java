package com.wanmi.sbc.goods.api.request.goodswarestockdetail;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.bean.enums.GoodsWareStockImportType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * <p> 库存明细表新增参数</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:24:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockDetailAddRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品库存关联表ID
	 */
	@ApiModelProperty(value = "商品库存关联表ID")
	@NotNull
	@Max(9223372036854775807L)
	private Long goodsWareStockId;

	/**
	 * sku编码
	 */
	@ApiModelProperty(value = "sku编码")
	@Length(max=45)
	private String goodsInfoNo;

	/**
	 * 商品id
	 */
	@ApiModelProperty(value = "商品id")
	@Length(max=45)
	private String goodsInfoId;

	/**
	 * 仓库ID
	 */
	@ApiModelProperty(value = "仓库ID")
	private Long wareId;

	/**
	 * 导入类型 0：导入，1：编辑，2：返还，3：下单扣减
	 */
	@ApiModelProperty(value = "导入类型 0：导入，1：编辑，2：返还，3：下单扣减")
	@NotNull
	private GoodsWareStockImportType importType;

	/**
	 * 操作库存
	 */
	@ApiModelProperty(value = "操作库存")
	@NotNull
	@Max(9223372036854775807L)
	private Long operateStock;

	/**
	 * 库存数量
	 */
	@ApiModelProperty(value = "库存数量")
	@NotNull
	@Max(9223372036854775807L)
	private Long stock;

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

	/**
	 * 导入编码
	 */
	@ApiModelProperty(value = "导入编码")
	@NotNull
	private String stockImportNo;

}
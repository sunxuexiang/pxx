package com.wanmi.sbc.goods.api.request.goodswarestockdetail;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.GoodsWareStockImportType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p> 库存明细表修改参数</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:24:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockDetailModifyRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@ApiModelProperty(value = "主键")
	@Max(9223372036854775807L)
	private Long id;

	/**
	 * 商品库存关联表ID
	 */
	@ApiModelProperty(value = "商品库存关联表ID")
	@NotNull
	@Max(9223372036854775807L)
	private Long goodsWareStockId;

	/**
	 * 导入编码
	 */
	@ApiModelProperty(value = "导入编码")
	@Length(max=45)
	private String goodsInfoNo;

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
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间", hidden = true)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	/**
	 * 编辑人
	 */
	@ApiModelProperty(value = "编辑人", hidden = true)
	private String updatePerson;

}
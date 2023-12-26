package com.wanmi.sbc.goods.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.GoodsWareStockImportType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p> 库存明细表VO</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:24:37
 */
@ApiModel
@Data
public class GoodsWareStockDetailVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@ApiModelProperty(value = "主键")
	private Long id;

	/**
	 * 商品库存关联表ID
	 */
	@ApiModelProperty(value = "商品库存关联表ID")
	private Long goodsWareStockId;

	/**
	 * SKU编码
	 */
	@ApiModelProperty(value = "SKU编码")
	private String goodsInfoNo;

	/**
	 * 导入编号
	 */
	@ApiModelProperty(value = "导入编号")
	private String stockImportNo;

	/**
	 * 导入类型 0：导入，1：编辑，2：返还，3：下单扣减
	 */
	@ApiModelProperty(value = "导入类型 0：导入，1：编辑，2：返还，3：下单扣减")
	private GoodsWareStockImportType importType;

	/**
	 * 操作库存
	 */
	@ApiModelProperty(value = "操作库存")
	private Long operateStock;

	/**
	 * 库存数量
	 */
	@ApiModelProperty(value = "库存数量")
	private Long stock;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	/**
	 * 创建人
	 */
	@ApiModelProperty(value = "创建人")
	private String createPerson;

}
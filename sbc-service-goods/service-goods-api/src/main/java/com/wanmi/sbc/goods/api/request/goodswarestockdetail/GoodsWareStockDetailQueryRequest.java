package com.wanmi.sbc.goods.api.request.goodswarestockdetail;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.GoodsWareStockImportType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p> 库存明细表通用查询请求参数</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:24:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsWareStockDetailQueryRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-主键List
	 */
	@ApiModelProperty(value = "批量查询-主键List")
	private List<Long> idList;

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
	 * 导入编码
	 */
	@ApiModelProperty(value = "导入编码")
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
	 * 搜索条件:创建时间开始
	 */
	@ApiModelProperty(value = "搜索条件:创建时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTimeBegin;
	/**
	 * 搜索条件:创建时间截止
	 */
	@ApiModelProperty(value = "搜索条件:创建时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTimeEnd;

	/**
	 * 创建人
	 */
	@ApiModelProperty(value = "创建人")
	private String createPerson;

	/**
	 * 搜索条件:修改时间开始
	 */
	@ApiModelProperty(value = "搜索条件:修改时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTimeBegin;
	/**
	 * 搜索条件:修改时间截止
	 */
	@ApiModelProperty(value = "搜索条件:修改时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTimeEnd;

	/**
	 * 编辑人
	 */
	@ApiModelProperty(value = "编辑人")
	private String updatePerson;

	/**
	 * 是否删除标志 0：否，1：是
	 */
	@ApiModelProperty(value = "是否删除标志 0：否，1：是")
	private DeleteFlag delFlag;

}
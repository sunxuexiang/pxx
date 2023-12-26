package com.wanmi.sbc.goods.api.request.catebrandsortrel;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import feign.Client;
import lombok.*;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>类目品牌排序表分页查询请求参数</p>
 * @author lvheng
 * @date 2021-04-08 11:24:32
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CateBrandSortRelPageRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-品类IDList
	 */
	@ApiModelProperty(value = "批量查询-品类IDList")
	private List<Long> cateIdList;

	/**
	 * 品类ID
	 */
	@ApiModelProperty(value = "品类ID")
	private Long cateId;

	/**
	 * 品牌ID
	 */
	@ApiModelProperty(value = "品牌ID")
	private Long brandId;

	/**
	 * 品牌名称
	 */
	@ApiModelProperty(value = "品牌名称")
	private String name;

	/**
	 * 品牌别名
	 */
	@ApiModelProperty(value = "品牌别名")
	private String alias;

	/**
	 * 排序序号
	 */
	@ApiModelProperty(value = "排序序号")
	private Integer serialNo;

	/**
	 * 删除标识，0：未删除 1：已删除
	 */
	@ApiModelProperty(value = "删除标识，0：未删除 1：已删除")
	private DeleteFlag delFlag;

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
	 * 搜索条件:更新时间开始
	 */
	@ApiModelProperty(value = "搜索条件:更新时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTimeBegin;
	/**
	 * 搜索条件:更新时间截止
	 */
	@ApiModelProperty(value = "搜索条件:更新时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTimeEnd;

	/**
	 * 更新人
	 */
	@ApiModelProperty(value = "更新人")
	private String updatePerson;

	/**
	 * 排序状态  NO 未排序  YES已排序
	 */
	private DeleteFlag sortStatus;

}
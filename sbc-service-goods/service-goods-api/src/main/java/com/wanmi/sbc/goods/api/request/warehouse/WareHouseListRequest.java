package com.wanmi.sbc.goods.api.request.warehouse;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.PickUpFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>仓库表列表查询请求参数</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class WareHouseListRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-wareIdList
	 */
	@ApiModelProperty(value = "批量查询-wareIdList")
	private List<Long> wareIdList;

	/**
	 * 批量查询-wareCodeList
	 */
	@ApiModelProperty(value = "批量查询-wareCodeList")
	private List<String> wareCodeList;

	/**
	 * 批量查询-storeIdList
	 */
	@ApiModelProperty(value = "批量查询-storeIdList")
	private List<Long> storeIdList;

	/**
	 * wareId
	 */
	@ApiModelProperty(value = "wareId")
	private Long wareId;

	/**
	 * 店铺标识
	 */
	@ApiModelProperty(value = "店铺标识")
	private Long storeId;

	/**
	 * 仓库名称
	 */
	@ApiModelProperty(value = "仓库名称")
	private String wareName;

	/**
	 * 仓库编号
	 */
	@ApiModelProperty(value = "仓库编号")
	private String wareCode;

	/**
	 * 省份
	 */
	@ApiModelProperty(value = "省份")
	private Long provinceId;

	/**
	 * 市
	 */
	@ApiModelProperty(value = "市")
	private Long cityId;

	/**
	 * 区
	 */
	@ApiModelProperty(value = "区")
	private Long areaId;

	/**
	 * 详细地址
	 */
	@ApiModelProperty(value = "详细地址")
	private String addressDetail;

	/**
	 * 是否默认仓 0：否，1：是
	 */
	@ApiModelProperty(value = "是否默认仓 0：否，1：是")
	private DefaultFlag defaultFlag;

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


	/**
	 * 是否为自提 0：否  1：是
	 */
	@ApiModelProperty(value = "是否为自提")
	private PickUpFlag pickUpFlag;

	@ApiModelProperty(value = "仓库类型")
	private WareHouseType wareHouseType;


}
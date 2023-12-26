package com.wanmi.sbc.setting.api.request.videoresourcecate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 视频教程资源分类表列表查询请求参数
 * @author hudong
 * @date 2023-06-26 16:13:19
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResourceCateListRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-素材分类idList
	 */
	@ApiModelProperty(value = "批量查询-素材分类idList")
	private List<String> cateIdList;

	/**
	 * 批量查询-素材资源父分类idList
	 */
	@ApiModelProperty(value = "批量查询-素材资源父分类cateParentIds")
	private List<String> cateParentIds;

	@ApiModelProperty(value = "分类层次路径,例1|01|001")
	private  String likeCatePath;


	@ApiModelProperty(value = "排除素材资源分类id")
	private  Long notCateId;

	/**
	 * 素材分类id
	 */
	@ApiModelProperty(value = "素材分类id")
	private String cateId;

	/**
	 * 店铺标识
	 */
	@ApiModelProperty(value = "店铺标识")
	private Long storeId;

	/**
	 * 商家标识
	 */
	@ApiModelProperty(value = "商家标识")
	private Long companyInfoId;

	/**
	 * 分类名称
	 */
	@ApiModelProperty(value = "分类名称")
	private String cateName;

	/**
	 * 父分类ID
	 */
	@ApiModelProperty(value = "父分类ID")
	private String cateParentId;

	/**
	 * 分类图片
	 */
	@ApiModelProperty(value = "分类图片")
	private String cateImg;

	/**
	 * 分类层次路径,例1|01|001
	 */
	@ApiModelProperty(value = "分类层次路径,例1|01|001")
	private String catePath;

	/**
	 * 分类层级
	 */
	@ApiModelProperty(value = "分类层级")
	private Integer cateGrade;

	/**
	 * 分类层级集合
	 */
	@ApiModelProperty(value = "分类层级集合")
	private List<Integer> cateGradeList;

	/**
	 * 拼音
	 */
	@ApiModelProperty(value = "拼音")
	private String pinYin;

	/**
	 * 简拼
	 */
	@ApiModelProperty(value = "简拼")
	private String spinYin;

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
	 * 删除标识,0:未删除1:已删除
	 */
	@ApiModelProperty(value = "删除标识,0:未删除1:已删除")
	private DeleteFlag delFlag;


	/**
	 * 是否默认,0:否1:是
	 */
	@ApiModelProperty(value = "是否默认,0:否1:是")
	 private DefaultFlag isDefault;

	/**
	 * 分类类别 1-商家操作视频分类 2-用户操作视频分类
	 */
	@ApiModelProperty(value = "分类类别")
	private Integer cateType;
}
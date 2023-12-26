package com.wanmi.sbc.setting.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>系统配置表VO</p>
 * @author yang
 * @date 2019-11-05 18:33:04
 */
@ApiModel
@Data
public class SystemStoreConfigVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *  编号
	 */
	@ApiModelProperty(value = " 编号")
	private Long id;

	/**
	 * 店铺主键
	 */
	@ApiModelProperty("店铺主键")
	private Long storeId;

	/**
	 * 键
	 */
	@ApiModelProperty("键")
	private String configKey;

	/**
	 * 类型
	 */
	@ApiModelProperty("类型")
	private String configType;

	/**
	 * 名称
	 */
	@ApiModelProperty("名称")
	private String configName;

	/**
	 * 配置内容，如json内容
	 */
	@ApiModelProperty("配置内容，如json内容")
	private String configValue;

	/**
	 * 状态，0:未启用1:已启用
	 */
	@ApiModelProperty("状态，0:未启用1:已启用")
	private Integer status;

	/**
	 * 删除标识，0:未删除1:已删除
	 */
	@ApiModelProperty("删除标识，0:未删除1:已删除")
	private DeleteFlag delFlag;

	/**
	 * 备注
	 */
	@ApiModelProperty("备注")
	private String remark;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@ApiModelProperty(value = "更新时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTime;

}
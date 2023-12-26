package com.wanmi.sbc.setting.api.request.systemfile;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import com.wanmi.sbc.setting.bean.enums.FileType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 *  平台文件新增参数
 * @author hudong
 * @date 2023-09-08 16:12:49
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemFileAddRequest extends SettingBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 资源类型(0:图片,1:视频)
	 */
	@ApiModelProperty(value = "资源类型(0:zip文件,1:图片,2:视频)")
	@NotNull
	private FileType type;


	/**
	 * 文件KEY
	 */
	@ApiModelProperty(value = "文件KEY")
	@Length(max=255)
	private String fileKey;

	/**
	 * 文件名称
	 */
	@ApiModelProperty(value = "文件名称")
	@Length(max=45)
	private String fileName;

	/**
	 * 文件地址
	 */
	@ApiModelProperty(value = "文件地址")
	@Length(max=255)
	private String path;

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

	/**
	 * 删除标识,0:未删除1:已删除
	 */
	@ApiModelProperty(value = "删除标识,0:未删除1:已删除")
	private DeleteFlag delFlag;

	/**
	 * oss服务器类型，对应system_config的config_type
	 */
	@ApiModelProperty(value = "oss服务器类型，对应system_config的config_type")
	@Length(max=255)
	private String serverType;

}
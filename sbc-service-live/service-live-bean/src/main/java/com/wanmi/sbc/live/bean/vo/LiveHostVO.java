package com.wanmi.sbc.live.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>主播VO</p>
 * @author 王冬明（1010331559@qq.com） Automatic Generator
 * @date 2022-09-19 11:37:24
 * @version 1.0
 * @package com.wanmi.sbc.live.bean.vo
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveHostVO implements Serializable {
    private static final long serialVersionUID = 6960666690513038223L;

	@ApiModelProperty(value = "主播ID")
	private Integer hostId;

	@ApiModelProperty(value = "主播姓名")
	private String hostName;

	@ApiModelProperty(value = "联系方式")
	private String contactPhone;

	@ApiModelProperty(value = "主播类型 0 官方 1入驻")
	private Long hostType;

	@ApiModelProperty(value = "在职状态 0 离职 1 在职")
	private Long workingState;

	@ApiModelProperty(value = "直播账号id")
	private String customerId;

	@ApiModelProperty(value = "直播账号")
	private String customerAccount;

	@ApiModelProperty(value = "运营账号")
	private String accountName;

	@ApiModelProperty(value = "运营账号id")
	private String employeeId;

	@ApiModelProperty(value = "创建时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private LocalDateTime createTime;

	@ApiModelProperty(value = "修改时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private LocalDateTime updateTime;

	@ApiModelProperty(value = "删除标识,0:未删除1:已删除 ")
	private Long delFlag;

}
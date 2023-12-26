package com.wanmi.sbc.setting.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>信息发布用户VO</p>
 * @author lwp
 * @date 2023/10/18
 */
@ApiModel
@Data
public class PublishUserVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 信息ID
	 */
	@ApiModelProperty(value = "信息ID")
	private Long id;

	/**
	 * 用户名
	 */
	private String userName;

}
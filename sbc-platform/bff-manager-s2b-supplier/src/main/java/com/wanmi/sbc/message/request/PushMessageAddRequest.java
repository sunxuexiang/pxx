package com.wanmi.sbc.message.request;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

/**
 * <p>站内信任务表新增参数</p>
 * @author xuyunpeng
 * @date 2020-01-06 11:12:11
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushMessageAddRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 任务名称
	 */
	@ApiModelProperty(value = "任务名称")
	@Length(max=40)
	private String name;

	/**
	 * 消息标题
	 */
	@ApiModelProperty(value = "消息标题")
	@Length(max=40)
	private String title;

	/**
	 * 消息内容
	 */
	@ApiModelProperty(value = "消息内容")
	private String content;

	/**
	 * 封面图
	 */
	@ApiModelProperty(value = "封面图")
	private String imgUrl;



}
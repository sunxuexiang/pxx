package com.wanmi.sbc.live.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * <p>福袋VO</p>
 * @author 刘丹（ldalc.com） Automatic Generator
 * @date 2022-09-20 13:04:21
 * @version 1.0
 * @package com.wanmi.sbc.live.bean.vo
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveBagVO implements Serializable {
    private static final long serialVersionUID = 8666702470267150944L;

	@ApiModelProperty(value = "live_bag_id")
	private Long liveBagId;

	@ApiModelProperty(value = "直播间id")
	private Long liveRoomId;

	@ApiModelProperty(value = "福袋名称")
	private String bagName;

	@ApiModelProperty(value = "中奖名额")
	private Long winningNumber;

	@ApiModelProperty(value = "发放次数")
	private Long provideNums;

	/**
	 * 发放状态：0：未发放，1：已发放
	 */
	@ApiModelProperty(value = "发放状态：0：未发放，1：已发放")
	private Long provideStatus;

	@ApiModelProperty(value = "开奖时间：分钟")
	private Double lotteryTime;

	@ApiModelProperty(value = "用户参与方式：0 指定内容")
	private Long joinType;

	@ApiModelProperty(value = "用户参与方式的内容")
	private String joinContent;

	@ApiModelProperty(value = "中奖用户兑换方式：0 自动发放")
	private Long ticketWay;

	@ApiModelProperty(value = "活动id")
	private String activityId;

	@ApiModelProperty(value = "优惠劵id")
	private String couponId;

	@ApiModelProperty(value = "create_person")
	private Long createPerson;

	@ApiModelProperty(value = "创建时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date createTime;

	@ApiModelProperty(value = "update_person")
	private Long updatePerson;

	@ApiModelProperty(value = "修改时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date updateTime;

	@ApiModelProperty(value = "删除标识,0:未删除1:已删除")
	private Integer delFlag;

	/**
	 * 中奖用户账号
	 */
	@ApiModelProperty(value = "中奖用户账号")
	private String customerAccounts;

	@ApiModelProperty(value = "发放时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date ticketTime;
}
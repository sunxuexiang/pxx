package com.wanmi.sbc.live.api.request.bag;

import java.util.Date;

import com.wanmi.sbc.common.base.BaseQueryRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>福袋PageRequest</p>
 * @author 刘丹（ldalc.com） Automatic Generator
 * @date 2022-09-20 13:04:21
 * @version 1.0
 * @package com.wanmi.sbc.live.api.request.bag
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveBagPageRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 1284888651855404020L;

	@ApiModelProperty(value = "live_bag_id")
	private Long liveBagId;
	@ApiModelProperty(value = "直播间id")
	private Long liveRoomId;
	@ApiModelProperty(value = "福袋名称")
	private String bagName;

	/** 发放次数 **/
	@ApiModelProperty(value = "发放次数")
	private Long provideNums;
	@ApiModelProperty(value = "发放状态：0：未发放，1：已发放")
	private Long provideStatus;
	@ApiModelProperty(value = "中奖名额")
	private Long winningNumber;
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
	private Date createTime;
	@ApiModelProperty(value = "update_person")
	private Long updatePerson;
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;
	@ApiModelProperty(value = "删除标识,0:未删除1:已删除")
	private Integer delFlag;
}
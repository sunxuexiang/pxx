package com.wanmi.sbc.live.bag.model.root;

import com.wanmi.sbc.common.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class LiveBag extends BaseEntity {

 	private static final long serialVersionUID = 2349315891487803628L;

	/**live_bag_id**/
	private Long liveBagId;

	/**直播间id**/
	private Long liveRoomId;

	/**福袋名称**/
	private String bagName;

	/**
	 * 发放状态：0：未发放，1：已发放
	 */
	private Long provideNums;
	

	/** 发放状态 **/
	private Long provideStatus;
	
	/**中奖名额**/
	private Long winningNumber;

	/**开奖时间：分钟**/
	private Double lotteryTime;

	/**用户参与方式：0 指定内容**/
	private Long joinType;

	/**用户参与方式的内容**/
	private String joinContent;

	/**中奖用户兑换方式：0 自动发放**/
	private Long ticketWay;

	/**活动id**/
	private String activityId;

	/**优惠劵id**/
	private String couponId;

	/**删除标识,0:未删除1:已删除**/
	private Integer delFlag;

	public LiveBag(){}



}
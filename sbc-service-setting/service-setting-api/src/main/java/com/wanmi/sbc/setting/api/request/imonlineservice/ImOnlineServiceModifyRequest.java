package com.wanmi.sbc.setting.api.request.imonlineservice;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceItemVO;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceVO;
import com.wanmi.sbc.setting.bean.vo.OnlineServiceItemVO;
import com.wanmi.sbc.setting.bean.vo.OnlineServiceVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * <p>onlineService修改参数</p>
 * @author sgy
 * @date 2023-06-05 16:10:28
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImOnlineServiceModifyRequest extends SettingBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * imOnlineService客服信息
	 */
	@ApiModelProperty(value = "imOnlineService客服信息")
	private ImOnlineServiceVO imOnlineServerRop;

	/**
	 * imOnlineServiceItem座席列表
	 */
	@ApiModelProperty(value = "imOnlineServiceItem座席列表")
	private List<ImOnlineServiceItemVO> imOnlineServerItemRopList;
	/**
	 * 商家id
	 */
	@ApiModelProperty(value = "商家id")
	private Long companyId;

	/**
	 * 店铺Id
	 */
	@ApiModelProperty(value = "店铺Id")
	private Long storeId;
	/**
	 * 店铺log
	 */
	@ApiModelProperty(value = "店铺logo")
	private String logo;
	/**
	 * 客服账号
	 */
	@ApiModelProperty(value = "客服账号")
	private String customerServiceAccount;

	/**
	 * 客服账号
	 */
	@ApiModelProperty(value = "客服账号Id")
	private String customerId;
	/**
	 * 客服主键
	 */
	@ApiModelProperty(value = "客服主键")
	private Integer imOnlineServiceId;

	@ApiModelProperty(value = "智能自动回复消息")
	private CommonChatMsgVo autoMsg;

	@ApiModelProperty(value = "快捷回复消息列表")
	private List<CommonChatMsgVo> commonMsgList;
}
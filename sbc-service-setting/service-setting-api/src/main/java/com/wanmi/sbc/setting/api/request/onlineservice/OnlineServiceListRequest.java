package com.wanmi.sbc.setting.api.request.onlineservice;

import com.wanmi.sbc.common.enums.CustomerServiceType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import lombok.*;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>onlineService座席列表查询请求参数</p>
 * @author lq
 * @date 2019-11-05 16:10:28
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnlineServiceListRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 店铺ID
	 */
	@ApiModelProperty(value = "店铺ID")
	private Long storeId;

	/**
	 * 来源店铺id
	 */
	@ApiModelProperty(value = "来源店铺id")
	private Long sourceStoreId;

	/**
	 * 来源店铺id
	 */
	@ApiModelProperty(value = "来源公司id")
	private Long sourceCompanyId;

	@ApiModelProperty(value = "App版本号")
	private String appVersion;

	@ApiModelProperty(value = "App所用手机品牌系统")
	private String appSysModel;

	@ApiModelProperty(value = "消费者IM账号")
	private String customerImAccount;

	@ApiModelProperty(value = "消费者IP地址")
	private String ipAddr;

	@ApiModelProperty(value = "在线客服启用类型：0、全部未启用；1、腾讯IM；2、智齿")
	private Integer serviceSwitchType;

	@ApiModelProperty(value = "用户端APP手机类型：0、安卓；1、IOS")
	private Integer appPlatform;

	private String storeName;

	private String storeLogo;

	@ApiModelProperty(value = "客服账号")
	private String serverAccount;

	@ApiModelProperty(value = "公司ID")
	private Long companyInfoId;

	@ApiModelProperty(value = "商家主账号手机号码")
	private String storeMasterEmployeeMobile;

	@ApiModelProperty(value = "商家主账号员工ID")
	private String storeMasterEmployeeId;

	@ApiModelProperty(value = "店铺ID")
	private Long realStoreId;

	@ApiModelProperty(value = "群组ID")
	private String imGroupId;

	private String appKey;

	private Long appId;

	@ApiModelProperty(value = "转接来源账号")
	private String switchSourceAccount;

	private String switchSourceNick;
}
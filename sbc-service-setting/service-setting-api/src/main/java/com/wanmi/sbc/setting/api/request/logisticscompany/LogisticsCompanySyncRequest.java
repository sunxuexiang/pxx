package com.wanmi.sbc.setting.api.request.logisticscompany;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.bouncycastle.cms.PasswordRecipientId;

import java.io.Serializable;
import java.util.List;

/**
 * <p>同步店铺信息</p>
 * @date 2020-11-06 13:37:51
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsCompanySyncRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 源店铺标识
	 */
	@ApiModelProperty(value = "源店铺标识")
	private Long sourceStoreId;

	@ApiModelProperty(value = "IdList")
	private List<Long> idList;

	/**
	 * 目标店铺标识
	 */
	@ApiModelProperty(value = "目标店铺标识")
	private List<Long> targetStoreIdList;

	@ApiModelProperty(value = "删除标识")
	private Integer delFlag;

	@ApiModelProperty("物流类型")
	private Integer logisticsType;

	/**
	 * 市场Id
	 */
	@ApiModelProperty(value = "市场Id")
	private Long marketId;
}
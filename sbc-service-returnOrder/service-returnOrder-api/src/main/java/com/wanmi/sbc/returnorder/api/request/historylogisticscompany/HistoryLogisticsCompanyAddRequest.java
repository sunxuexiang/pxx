package com.wanmi.sbc.returnorder.api.request.historylogisticscompany;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.Convert;
import java.time.LocalDateTime;

/**
 * <p>物流信息历史记录新增参数</p>
 * @author fcq
 * @date 2020-11-09 17:32:23
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryLogisticsCompanyAddRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 会员id
	 */
	@ApiModelProperty(value = "会员id")
	@Length(max=32)
	private String customerId;

	/**
	 * 订单id
	 */
	@ApiModelProperty(value = "订单id")
	@Length(max=32)
	private String orderId;

	/**
	 * 物流公司名称
	 */
	@ApiModelProperty(value = "物流公司名称")
	@Length(max=100)
	private String logisticsName;

	/**
	 * 物流公司电话
	 */
	@ApiModelProperty(value = "物流公司电话")
	@Length(max=20)
	private String logisticsPhone;

	/**
	 * 收货站点
	 */
	@ApiModelProperty(value = "收货站点")
	@Length(max=200)
	private String receivingSite;

	/**
	 * 删除标志
	 */
	@ApiModelProperty(value = "删除标志", hidden = true)
	private DeleteFlag delFlag;

	@CreatedDate
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	private LocalDateTime createTime;

}
package com.wanmi.sbc.returnorder.api.request.pickuprecord;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p>测试代码生成修改参数</p>
 * @author lh
 * @date 2020-07-14 13:48:26
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickUpRecordModifyRequest extends BaseRequest {


	private static final long serialVersionUID = 2434811640290841012L;
	/**
	 * 主键
	 */
	@ApiModelProperty(value = "主键")
	@Length(max=32)
	private String pickUpId;

	/**
	 * 店铺id
	 */
	@ApiModelProperty(value = "店铺id")
	@NotNull
	@Max(9223372036854775807L)
	private Long storeId;

	/**
	 * 订单id
	 */
	@ApiModelProperty(value = "订单id")
	@NotBlank
	@Length(max=32)
	private String tradeId;

	/**
	 * 自提码
	 */
	@ApiModelProperty(value = "自提码")
	@NotBlank
	@Length(max=6)
	private String pickUpCode;

	/**
	 * 是否已自提:0:未自提 1：已自提
	 */
	@ApiModelProperty(value = "是否已自提:0:未自提 1：已自提")
	@NotNull
	private DefaultFlag pickUpFlag;

	/**
	 * 自提时间
	 */
	@ApiModelProperty(value = "自提时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime pickUpTime;

	/**
	 * 手机号
	 */
	@ApiModelProperty(value = "手机号")
	private String contactPhone;

	/**
	 * 创建表时间
	 */
	@ApiModelProperty(value = "创建表时间", hidden = true)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

}
package com.wanmi.sbc.goods.api.request.goodslabelrela;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import java.time.LocalDateTime;

/**
 * <p>邀新统计修改参数</p>
 * @author lvheng
 * @date 2021-04-23 14:20:19
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsLabelRelaModifyRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品标签关联主键ID
	 */
	@ApiModelProperty(value = "商品标签关联主键ID")
	@Max(9223372036854775807L)
	private Long id;

	/**
	 * 商品id
	 */
	@ApiModelProperty(value = "商品id")
	@Length(max=32)
	private String goodsId;

	/**
	 * 标签id
	 */
	@ApiModelProperty(value = "标签id")
	@Max(9223372036854775807L)
	private Long labelId;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间", hidden = true)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	/**
	 * 更新人id
	 */
	@ApiModelProperty(value = "更新人id", hidden = true)
	private String updatePerson;

}
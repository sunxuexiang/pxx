package com.wanmi.sbc.goods.api.request.catebrandsortrel;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>类目品牌排序表修改参数</p>
 * @author lvheng
 * @date 2021-04-08 11:24:32
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CateBrandSortRelModifyRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 品类ID
	 */
	@ApiModelProperty(value = "品类ID")
	@Max(9223372036854775807L)
	private Long cateId;

	/**
	 * 品牌ID
	 */
	@ApiModelProperty(value = "品牌ID")
	@Max(9223372036854775807L)
	private Long brandId;


	@ApiModelProperty(value = "排序序号")
	@Max(9999999999L)
	private Integer serialNo;

	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "更新时间", hidden = true)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTime;

	/**
	 * 更新人
	 */
	@ApiModelProperty(value = "更新人", hidden = true)
	private String updatePerson;

}
package com.wanmi.sbc.goods.api.request.lastgoodswrite;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.*;
import javax.validation.constraints.*;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

/**
 * <p>用户最后一次商品记录新增参数</p>
 * @author 费传奇
 * @date 2021-04-23 17:33:51
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LastGoodsWriteAddRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	@ApiModelProperty(value = "用户id")
	@Length(max=255)
	private String customerId;

	/**
	 * 商品skuid
	 */
	@ApiModelProperty(value = "商品skuid")
	@Length(max=255)
	private String goodsInfoId;

	/**
	 * 品牌Id
	 */
	@ApiModelProperty(value = "品牌Id")
	private Long brandId;

	/**
	 * 类目id
	 */
	@ApiModelProperty(value = "类目id")
	private Long cateId;

	@ApiModelProperty(value = "创建时间", hidden = true)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

}
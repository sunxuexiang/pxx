package com.wanmi.sbc.goods.api.request.bidding;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.bean.enums.BiddingType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>校验关键词的请求参数</p>
 * @author baijz
 * @date 2020-08-05 16:27:45
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiddingValidateGoodsRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品skuid
	 */
	@ApiModelProperty(value = "商品skuid")
	private List<String> goodsInfoIds;

	/**
     * 竞价配置的Id
	 */
	@ApiModelProperty(value = "竞价配置的Id")
	private String biddingId;

	/**
	 * 竞价分类
	 */
	@ApiModelProperty(value = "竞价分类")
	private BiddingType biddingType;

	/**
	 * 开始时间
	 */
	@ApiModelProperty(value = "开始时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime startTime;

	/**
	 * 结束时间
	 */
	@ApiModelProperty(value = "结束时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime endTime;
}
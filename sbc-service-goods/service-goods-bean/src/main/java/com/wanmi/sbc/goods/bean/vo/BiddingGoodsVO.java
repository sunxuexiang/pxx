package com.wanmi.sbc.goods.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import lombok.Data;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * <p>竞价商品VO</p>
 * @author baijz
 * @date 2020-08-05 16:34:44
 */
@ApiModel
@Data
public class BiddingGoodsVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 竞价商品的Id
	 */
	@ApiModelProperty(value = "竞价商品的Id")
	private String biddingGoodsId;

	/**
	 * 竞价的Id
	 */
	@ApiModelProperty(value = "竞价的Id")
	private String biddingId;

	/**
	 * 排名
	 */
	@ApiModelProperty(value = "排名")
	private Integer sort;

	/**
	 * skuId
	 */
	@ApiModelProperty(value = "skuId")
	private String goodsInfoId;

	/**
	 * SKU信息
	 */
	@ApiModelProperty(value = "SKU信息")
	private GoodsInfoVO goodsInfo;

	/**
	 * 修改时间
	 */
	@ApiModelProperty(value = "修改时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime modifyTime;

	/**
	 * 删除时间
	 */
	@ApiModelProperty(value = "删除时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime delTime;

}
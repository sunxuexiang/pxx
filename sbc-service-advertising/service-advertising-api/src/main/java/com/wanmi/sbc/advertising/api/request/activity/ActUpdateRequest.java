package com.wanmi.sbc.advertising.api.request.activity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wanmi.sbc.advertising.bean.enums.MaterialType;
import com.wanmi.sbc.advertising.bean.enums.SlotType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zc
 *
 */
@ApiModel
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActUpdateRequest{

	/**
	 * 广告活动id
	 */
	@ApiModelProperty("广告活动id")
	@NotBlank
	private String id;
	
	/**
	 * 商品id
	 */
	@ApiModelProperty("商品id")
	private String spuId;
	
	/**
	 * 商品名称
	 */
	@ApiModelProperty("商品名称")
	private String spuName;
	
	/**
	 * 素材url
	 */
	@ApiModelProperty("素材url")
	private String materialUrl;
	
	/**
	 * 素材文件key
	 */
	@ApiModelProperty("素材文件key")
	private String materialKey;
	
	/**
	 * 素材类型
	 */
	@ApiModelProperty("素材类型")
	private MaterialType materialType;
	
	


}

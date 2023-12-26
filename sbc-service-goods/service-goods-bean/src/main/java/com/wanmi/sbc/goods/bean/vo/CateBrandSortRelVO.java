package com.wanmi.sbc.goods.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * <p>类目品牌排序表VO</p>
 * @author lvheng
 * @date 2021-04-08 11:24:32
 */
@ApiModel
@Data
public class CateBrandSortRelVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品类ID
	 */
	@ApiModelProperty(value = "品类ID")
	private Long cateId;

	/**
	 * 品牌ID
	 */
	@ApiModelProperty(value = "品牌ID")
	private Long brandId;

	/**
	 * 品牌名称
	 */
	@ApiModelProperty(value = "品牌名称")
	private String name;

	/**
	 * 品牌别名
	 */
	@ApiModelProperty(value = "品牌别名")
	private String alias;

	/**
	 * 排序序号
	 */
	@ApiModelProperty(value = "排序序号")
	private Long serialNo;

	/**
	 * 创建人ID
	 */
	@ApiModelProperty(value = "创建人ID")
	private String createPerson;

	@ApiModelProperty(value = "创建时间", hidden = true)
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

	private DeleteFlag delFlag;

	/**
	 * 签约商家
	 */
	private List<String> storeNames;


}
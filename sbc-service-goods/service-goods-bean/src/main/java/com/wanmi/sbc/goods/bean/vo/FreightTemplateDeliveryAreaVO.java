package com.wanmi.sbc.goods.bean.vo;

import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.goods.bean.enums.freightTemplateDeliveryType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>配送到家范围VO</p>
 * @author zhaowei
 * @date 2021-03-25 16:57:57
 */
@ApiModel
@Data
public class FreightTemplateDeliveryAreaVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键标识
	 */
	@ApiModelProperty(value = "主键标识")
	private Long id;

	/**
	 * 配送地id(逗号分隔)
	 */
	@ApiModelProperty(value = "配送地id(逗号分隔)")
	private String[] destinationArea;

	/**
	 * 配送地名称(逗号分隔)
	 */
	@ApiModelProperty(value = "配送地名称(逗号分隔)")
	private String[] destinationAreaName;

	/**
	 * 店铺标识
	 */
	@ApiModelProperty(value = "店铺标识")
	private Long storeId;

	/**
	 * 公司信息ID
	 */
	@ApiModelProperty(value = "公司信息ID")
	private Long companyInfoId;

	/**
	 * 免费店配类型（0常规，1乡镇满十件免配送区域）
	 */
	@ApiModelProperty(value = "免费店配类型（0常规，1乡镇满十件免配送区域）")
	private freightTemplateDeliveryType destinationType;

	private String destinationTypeDesc;

	public String getDestinationTypeDesc() {
		if(null!=destinationType){
			return destinationType.getDesc();
		}
		return Constants.EMPTY_STR;
	}

	/**
	 * 仓库id
	 */
	@ApiModelProperty(value = "仓库id")
	private Long wareId;

	/**
	 * 发货仓名称
	 */
	@ApiModelProperty(value = "发货仓名称")
	private String wareName;

	/**
	 * 免运费起始数量
	 */
	@ApiModelProperty(value = "免运费起始数量")
	private Long freightFreeNumber;

	/**
	 * 是否启用(0:否,1:是)
	 */
	@ApiModelProperty(value = "是否启用(0:否,1:是)")
	private Integer openFlag;

	/**
	 * @desc  自定义配置
	 * @author shiy  2023/9/22 10:17
	 */
	@ApiModelProperty(value = "自定义配置")
	private FreightTemplateDeliveryAreaCustomCfgVO customCfgVO;
}
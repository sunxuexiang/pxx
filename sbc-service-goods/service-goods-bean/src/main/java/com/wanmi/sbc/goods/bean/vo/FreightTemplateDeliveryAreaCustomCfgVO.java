package com.wanmi.sbc.goods.bean.vo;

import com.wanmi.sbc.goods.bean.enums.freightTemplateDeliveryType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @desc  配置表
 * @author shiy  2023/9/22 10:33
*/
@ApiModel
@Data
public class FreightTemplateDeliveryAreaCustomCfgVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * @desc  上门时间设置
	 * @author shiy  2023/9/22 10:38
	*/
	private String pickTime;

}
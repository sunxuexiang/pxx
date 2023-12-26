package com.wanmi.sbc.goods.api.request.freight;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import com.wanmi.sbc.goods.bean.enums.freightTemplateDeliveryType;
import com.wanmi.sbc.goods.bean.vo.FreightTemplateDeliveryAreaCustomCfgVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;


/**
 * <p>配送到家范围新增参数</p>
 * @author zhaowei
 * @date 2021-03-25 16:57:57
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreightTemplateDeliveryAreaSaveRequest extends GoodsBaseRequest {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "主键")
	private Long id;

	/**
	 * 配送地id(逗号分隔)
	 */
	@ApiModelProperty(value = "配送地id", notes = "逗号分隔")
	private String[] destinationArea;

	/**
	 * 配送地名称(逗号分隔)
	 */
	@ApiModelProperty(value = "配送地名称", notes = "逗号分隔")
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

	/**
	 * 仓库id
	 */
	@ApiModelProperty(value = "仓库id")
	//@NotNull
	private Long wareId;

	/**
	 * 免运费起始数量
	 */
	@ApiModelProperty(value = "免运费起始数量")
	//@NotNull
	private Long freightFreeNumber;

	/**
	 * 是否启用(0:否,1:是)
	 */
	@ApiModelProperty(value = "是否启用(0:否,1:是)")
	private Integer openFlag;

	@ApiModelProperty(value = "自定义配置")
	private FreightTemplateDeliveryAreaCustomCfgVO customCfgVO;

	private Integer systemInit;
	public Integer getSystemInit() {
		return systemInit==null?0:systemInit;
	}
}
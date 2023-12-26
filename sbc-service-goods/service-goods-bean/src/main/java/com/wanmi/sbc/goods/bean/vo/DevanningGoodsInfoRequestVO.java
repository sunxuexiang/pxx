package com.wanmi.sbc.goods.bean.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * <p>查询拆箱表VO</p>
 * @author lwp
 */
@ApiModel
@Data
public class DevanningGoodsInfoRequestVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * skuId集合
	 */
	@ApiModelProperty(value = "skuId集合")
	private List<String> goodsInfoIds;



}
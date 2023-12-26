package com.wanmi.sbc.goods.api.request.catebrandsortrel;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.bean.vo.CateBrandSortRelVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import java.util.List;

/**
 * <p>类目品牌排序表新增参数</p>
 * @author lvheng
 * @date 2021-04-08 11:24:32
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CateBrandSortRelBatchAddRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	private List<CateBrandSortRelVO> cateBrandSortRelVO;
}
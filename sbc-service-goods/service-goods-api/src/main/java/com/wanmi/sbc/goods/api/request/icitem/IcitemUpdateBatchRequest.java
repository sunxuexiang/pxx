package com.wanmi.sbc.goods.api.request.icitem;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.Map;

/**
 * <p>配送到家新增参数</p>
 * @author lh
 * @date 2020-12-05 18:16:34
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IcitemUpdateBatchRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	private Map<String,IcitemUpdateRequest> request;

	private IcitemUpdateBatchRequest IcitemUpdateBatchRequest;
}